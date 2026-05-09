package hr.algebra.mangaapp.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import hr.algebra.mangaapp.model.Author;
import hr.algebra.mangaapp.model.Genre;
import hr.algebra.mangaapp.model.Manga;
import hr.algebra.mangaapp.model.Publisher;
import hr.algebra.mangaapp.model.StoryCharacter;
import hr.algebra.mangaapp.model.enums.AuthorType;
import hr.algebra.mangaapp.model.enums.CharacterRole;
import hr.algebra.mangaapp.model.enums.MangaStatus;
import hr.algebra.mangaapp.repository.AuthorRepository;
import hr.algebra.mangaapp.repository.GenreRepository;
import hr.algebra.mangaapp.repository.MangaRepository;
import hr.algebra.mangaapp.repository.PublisherRepository;
import hr.algebra.mangaapp.repository.RepositoryFactory;
import hr.algebra.mangaapp.repository.StoryCharacterRepository;
import hr.algebra.mangaapp.repository.search.MangaSearchCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;

public class JikanMangaImportService {

    private static final Logger log = LoggerFactory.getLogger(JikanMangaImportService.class);

    private static final URI TOP_MANGA_URI =
            URI.create("https://api.jikan.moe/v4/top/manga?type=manga&limit=25");

    private static final String MANGA_CHARACTERS_URL =
            "https://api.jikan.moe/v4/manga/%d/characters";

    private static final int MAX_CHARACTERS_PER_MANGA = 10;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(15))
            .build();

    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private final MangaRepository mangaRepository = RepositoryFactory.getMangaRepository();
    private final PublisherRepository publisherRepository = RepositoryFactory.getPublisherRepository();
    private final GenreRepository genreRepository = RepositoryFactory.getGenreRepository();
    private final AuthorRepository authorRepository = RepositoryFactory.getAuthorRepository();
    private final StoryCharacterRepository characterRepository = RepositoryFactory.getStoryCharacterRepository();

    public ImportResult importTopMangaCatalog() {
        return importTopMangaCatalog(() -> false);
    }

    public ImportResult importTopMangaCatalog(BooleanSupplier isCancelled) {
        ImportCounters counters = new ImportCounters();

        JikanTopMangaResponse response = fetchJson(TOP_MANGA_URI, JikanTopMangaResponse.class);

        for (JikanMangaDto mangaDto : response.data) {
            checkCancellation(isCancelled);

            if (isBlank(mangaDto.title) || mangaAlreadyExists(mangaDto.title)) {
                counters.skippedManga++;
                continue;
            }

            Publisher publisher = ensurePublisher(resolvePublisherName(mangaDto), counters);
            checkCancellation(isCancelled);

            Set<Author> authors = importAuthors(mangaDto.authors, counters);
            checkCancellation(isCancelled);

            Set<Genre> genres = importGenres(mangaDto.genres, counters);
            checkCancellation(isCancelled);

            Set<StoryCharacter> characters = importCharacters(mangaDto.malId, counters);
            checkCancellation(isCancelled);

            Manga manga = new Manga(
                    mangaDto.title.trim(),
                    blankToNull(mangaDto.synopsis),
                    resolveReleaseYear(mangaDto),
                    resolveVolumes(mangaDto),
                    publisher,
                    downloadCover(mangaDto, counters),
                    resolveStatus(mangaDto.status),
                    characters,
                    genres,
                    authors
            );

            checkCancellation(isCancelled);
            mangaRepository.create(manga);
            counters.importedManga++;
        }

        return counters.toResult();
    }

    private <T> T fetchJson(URI uri, Class<T> responseType) {
        HttpRequest request = HttpRequest.newBuilder(uri)
                .timeout(Duration.ofSeconds(30))
                .header("Accept", "application/json")
                .header("User-Agent", "MangaApp/1.0")
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IllegalStateException(
                        "Jikan API returned HTTP status " + response.statusCode()
                );
            }

            return objectMapper.readValue(response.body(), responseType);

        } catch (IOException e) {
            throw new IllegalStateException("Failed to parse JSON from Jikan API", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CancellationException("Jikan API request was cancelled");
        }
    }

    private Set<Author> importAuthors(List<JikanNamedResourceDto> authorDtos, ImportCounters counters) {
        if (authorDtos == null || authorDtos.isEmpty()) {
            return Set.of();
        }

        return authorDtos.stream()
                .filter(author -> !isBlank(author.name))
                .map(author -> ensureAuthor(author.name, counters))
                .collect(Collectors.toCollection(HashSet::new));
    }

    private Set<Genre> importGenres(List<JikanNamedResourceDto> genreDtos, ImportCounters counters) {
        if (genreDtos == null || genreDtos.isEmpty()) {
            return Set.of();
        }

        return genreDtos.stream()
                .filter(genre -> !isBlank(genre.name))
                .map(genre -> ensureGenre(genre.name, counters))
                .collect(Collectors.toCollection(HashSet::new));
    }

    private Set<StoryCharacter> importCharacters(Long mangaMalId, ImportCounters counters) {
        if (mangaMalId == null) {
            return Set.of();
        }

        URI charactersUri = URI.create(String.format(MANGA_CHARACTERS_URL, mangaMalId));

        try {
            pauseBeforeDetailRequest();

            JikanCharactersResponse response = fetchJson(charactersUri, JikanCharactersResponse.class);

            return response.data.stream()
                    .filter(entry -> entry.character != null && !isBlank(entry.character.name))
                    .limit(MAX_CHARACTERS_PER_MANGA)
                    .map(entry -> ensureCharacter(
                            entry.character.name,
                            resolveCharacterRole(entry.role),
                            counters
                    ))
                    .collect(Collectors.toCollection(HashSet::new));

        } catch (CancellationException e) {
            throw e;
        } catch (RuntimeException e) {
            log.warn("Skipping character import for Jikan manga id={}", mangaMalId, e);
            return Set.of();
        }
    }

    private void pauseBeforeDetailRequest() {
        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CancellationException("Jikan API import was cancelled");
        }
    }

    private Publisher ensurePublisher(String publisherName, ImportCounters counters) {
        Optional<Publisher> existingPublisher = publisherRepository.search(publisherName).stream()
                .filter(publisher -> equalsIgnoreCase(publisher.getName(), publisherName))
                .findFirst();

        if (existingPublisher.isPresent()) {
            return existingPublisher.get();
        }

        Long publisherId = publisherRepository.create(new Publisher(publisherName));
        counters.importedPublishers++;

        return publisherRepository.findById(publisherId).orElseThrow();
    }

    private Genre ensureGenre(String genreName, ImportCounters counters) {
        Optional<Genre> existingGenre = genreRepository.search(genreName).stream()
                .filter(genre -> equalsIgnoreCase(genre.getName(), genreName))
                .findFirst();

        if (existingGenre.isPresent()) {
            return existingGenre.get();
        }

        Long genreId = genreRepository.create(
                new Genre(genreName, "Imported from Jikan API")
        );
        counters.importedGenres++;

        return genreRepository.findById(genreId).orElseThrow();
    }

    private Author ensureAuthor(String fullName, ImportCounters counters) {
        Optional<Author> existingAuthor = authorRepository.search(fullName).stream()
                .filter(author -> equalsIgnoreCase(author.getFullName(), fullName))
                .findFirst();

        if (existingAuthor.isPresent()) {
            return existingAuthor.get();
        }

        NameParts nameParts = splitName(fullName);
        Long authorId = authorRepository.create(
                new Author(nameParts.firstName(), nameParts.lastName(), AuthorType.MANGAKA)
        );
        counters.importedAuthors++;

        return authorRepository.findById(authorId).orElseThrow();
    }

    private StoryCharacter ensureCharacter(
            String fullName,
            CharacterRole role,
            ImportCounters counters
    ) {
        Optional<StoryCharacter> existingCharacter = characterRepository.search(fullName).stream()
                .filter(character -> equalsIgnoreCase(character.getFullName(), fullName))
                .findFirst();

        if (existingCharacter.isPresent()) {
            return existingCharacter.get();
        }

        NameParts nameParts = splitName(fullName);
        Long characterId = characterRepository.create(
                new StoryCharacter(nameParts.firstName(), nameParts.lastName(), role)
        );
        counters.importedCharacters++;

        return characterRepository.findById(characterId).orElseThrow();
    }

    private boolean mangaAlreadyExists(String title) {
        MangaSearchCriteria criteria = new MangaSearchCriteria().setTitle(title);

        return mangaRepository.search(criteria).stream()
                .anyMatch(manga -> equalsIgnoreCase(manga.getTitle(), title));
    }

    private String resolvePublisherName(JikanMangaDto mangaDto) {
        if (mangaDto.serializations != null) {
            Optional<String> serializationName = mangaDto.serializations.stream()
                    .map(serialization -> serialization.name)
                    .filter(name -> !isBlank(name))
                    .findFirst();

            if (serializationName.isPresent()) {
                return serializationName.get().trim();
            }
        }

        return "Jikan Import";
    }

    private MangaStatus resolveStatus(String jikanStatus) {
        if (isBlank(jikanStatus)) {
            return MangaStatus.ONGOING;
        }

        String normalizedStatus = jikanStatus.toLowerCase(Locale.ROOT);

        if (normalizedStatus.contains("finished")) {
            return MangaStatus.COMPLETED;
        }

        if (normalizedStatus.contains("hiatus")) {
            return MangaStatus.HIATUS;
        }

        if (normalizedStatus.contains("discontinued")) {
            return MangaStatus.CANCELLED;
        }

        return MangaStatus.ONGOING;
    }

    private CharacterRole resolveCharacterRole(String jikanRole) {
        if (isBlank(jikanRole)) {
            return CharacterRole.SUPPORTING;
        }

        String normalizedRole = jikanRole.toLowerCase(Locale.ROOT);

        if (normalizedRole.contains("main")) {
            return CharacterRole.MAIN;
        }

        if (normalizedRole.contains("antagonist")) {
            return CharacterRole.ANTAGONIST;
        }

        return CharacterRole.SUPPORTING;
    }

    private int resolveReleaseYear(JikanMangaDto mangaDto) {
        if (mangaDto.published == null
                || mangaDto.published.prop == null
                || mangaDto.published.prop.from == null
                || mangaDto.published.prop.from.year == null) {
            return 0;
        }

        return mangaDto.published.prop.from.year;
    }

    private int resolveVolumes(JikanMangaDto mangaDto) {
        return mangaDto.volumes != null ? mangaDto.volumes : 0;
    }

    private String downloadCover(JikanMangaDto mangaDto, ImportCounters counters) {
        String imageUrl = resolveImageUrl(mangaDto);

        if (isBlank(imageUrl) || mangaDto.malId == null) {
            return null;
        }

        try {
            Path coversDirectory = Path.of("assets", "covers");
            Files.createDirectories(coversDirectory);

            Path destination = coversDirectory.resolve(
                    "jikan-" + mangaDto.malId + resolveImageExtension(imageUrl)
            );

            if (Files.exists(destination)) {
                return normalizePath(destination);
            }

            HttpRequest request = HttpRequest.newBuilder(URI.create(imageUrl))
                    .timeout(Duration.ofSeconds(30))
                    .header("User-Agent", "MangaApp/1.0")
                    .GET()
                    .build();

            HttpResponse<byte[]> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofByteArray()
            );

            if (response.statusCode() < 200 || response.statusCode() >= 300
                    || response.body().length == 0) {
                return null;
            }

            Files.write(
                    destination,
                    response.body(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );
            counters.downloadedCovers++;

            return normalizePath(destination);

        } catch (IOException e) {
            log.warn("Failed to store cover image for manga title={}", mangaDto.title, e);
            return null;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CancellationException("Cover download was cancelled");
        } catch (CancellationException e) {
            throw e;
        } catch (RuntimeException e) {
            log.warn("Failed to download cover image from {}", imageUrl, e);
            return null;
        }
    }

    private void checkCancellation(BooleanSupplier isCancelled) {
        if (isCancelled.getAsBoolean() || Thread.currentThread().isInterrupted()) {
            throw new CancellationException("Online import was cancelled");
        }
    }

    private String resolveImageUrl(JikanMangaDto mangaDto) {
        if (mangaDto.images == null || mangaDto.images.jpg == null) {
            return null;
        }

        return mangaDto.images.jpg.imageUrl;
    }

    private String resolveImageExtension(String imageUrl) {
        String lowerCaseUrl = imageUrl.toLowerCase(Locale.ROOT);

        if (lowerCaseUrl.contains(".png")) {
            return ".png";
        }

        if (lowerCaseUrl.contains(".webp")) {
            return ".webp";
        }

        if (lowerCaseUrl.contains(".jpeg")) {
            return ".jpeg";
        }

        return ".jpg";
    }

    private NameParts splitName(String fullName) {
        String normalizedName = fullName.trim();
        int lastSpaceIndex = normalizedName.lastIndexOf(' ');

        if (lastSpaceIndex < 0) {
            return new NameParts(normalizedName, "");
        }

        return new NameParts(
                normalizedName.substring(0, lastSpaceIndex).trim(),
                normalizedName.substring(lastSpaceIndex + 1).trim()
        );
    }

    private String normalizePath(Path path) {
        return path.toString().replace('\\', '/');
    }

    private String blankToNull(String value) {
        return isBlank(value) ? null : value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private boolean equalsIgnoreCase(String firstValue, String secondValue) {
        if (firstValue == null || secondValue == null) {
            return false;
        }

        return firstValue.trim().equalsIgnoreCase(secondValue.trim());
    }

    private record NameParts(String firstName, String lastName) {
    }

    public record ImportResult(
            int importedManga,
            int skippedManga,
            int importedPublishers,
            int importedGenres,
            int importedAuthors,
            int importedCharacters,
            int downloadedCovers
    ) {

        public String toUserMessage() {
            return """
                    Online JSON import finished.

                    Manga imported: %d
                    Manga skipped because title already exists: %d
                    Publishers created: %d
                    Genres created: %d
                    Authors created: %d
                    Characters created: %d
                    Covers downloaded: %d
                    """.formatted(
                    importedManga,
                    skippedManga,
                    importedPublishers,
                    importedGenres,
                    importedAuthors,
                    importedCharacters,
                    downloadedCovers
            );
        }
    }

    private static class ImportCounters {
        private int importedManga;
        private int skippedManga;
        private int importedPublishers;
        private int importedGenres;
        private int importedAuthors;
        private int importedCharacters;
        private int downloadedCovers;

        private ImportResult toResult() {
            return new ImportResult(
                    importedManga,
                    skippedManga,
                    importedPublishers,
                    importedGenres,
                    importedAuthors,
                    importedCharacters,
                    downloadedCovers
            );
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class JikanTopMangaResponse {
        private List<JikanMangaDto> data = List.of();

        public void setData(List<JikanMangaDto> data) {
            this.data = data != null ? data : List.of();
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class JikanMangaDto {
        @JsonProperty("mal_id")
        private Long malId;

        private String title;
        private String synopsis;
        private Integer volumes;
        private String status;
        private JikanPublishedDto published;
        private JikanImagesDto images;
        private List<JikanNamedResourceDto> authors = List.of();
        private List<JikanNamedResourceDto> genres = List.of();
        private List<JikanNamedResourceDto> serializations = List.of();

        public void setMalId(Long malId) {
            this.malId = malId;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setSynopsis(String synopsis) {
            this.synopsis = synopsis;
        }

        public void setVolumes(Integer volumes) {
            this.volumes = volumes;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public void setPublished(JikanPublishedDto published) {
            this.published = published;
        }

        public void setImages(JikanImagesDto images) {
            this.images = images;
        }

        public void setAuthors(List<JikanNamedResourceDto> authors) {
            this.authors = authors != null ? authors : List.of();
        }

        public void setGenres(List<JikanNamedResourceDto> genres) {
            this.genres = genres != null ? genres : List.of();
        }

        public void setSerializations(List<JikanNamedResourceDto> serializations) {
            this.serializations = serializations != null ? serializations : List.of();
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class JikanCharactersResponse {
        private List<JikanCharacterEntryDto> data = List.of();

        public void setData(List<JikanCharacterEntryDto> data) {
            this.data = data != null ? data : List.of();
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class JikanCharacterEntryDto {
        private JikanNamedResourceDto character;
        private String role;

        public void setCharacter(JikanNamedResourceDto character) {
            this.character = character;
        }

        public void setRole(String role) {
            this.role = role;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class JikanNamedResourceDto {
        private String name;

        public void setName(String name) {
            this.name = name;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class JikanPublishedDto {
        private JikanPublishedPropDto prop;

        public void setProp(JikanPublishedPropDto prop) {
            this.prop = prop;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class JikanPublishedPropDto {
        private JikanDatePartsDto from;

        public void setFrom(JikanDatePartsDto from) {
            this.from = from;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class JikanDatePartsDto {
        private Integer year;

        public void setYear(Integer year) {
            this.year = year;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class JikanImagesDto {
        private JikanImageFormatDto jpg;

        public void setJpg(JikanImageFormatDto jpg) {
            this.jpg = jpg;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class JikanImageFormatDto {
        @JsonProperty("image_url")
        private String imageUrl;

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }
    }
}
