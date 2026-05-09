package hr.algebra.mangaapp.xml;

import hr.algebra.mangaapp.model.Author;
import hr.algebra.mangaapp.model.Genre;
import hr.algebra.mangaapp.model.Manga;
import hr.algebra.mangaapp.model.StoryCharacter;
import hr.algebra.mangaapp.repository.MangaRepository;
import hr.algebra.mangaapp.repository.RepositoryFactory;
import hr.algebra.mangaapp.repository.search.MangaSearchCriteria;
import hr.algebra.mangaapp.xml.dto.MangaCatalogXmlDto;
import hr.algebra.mangaapp.xml.dto.MangaXmlDto;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;

import java.io.File;
import java.util.List;

public class MangaXmlExportService {

    private final MangaRepository mangaRepository =
            RepositoryFactory.getMangaRepository();

    public void exportCatalogByAuthor(Author author, File destinationFile) {
        if (author == null || author.getId() == null) {
            throw new IllegalArgumentException("Author is required for XML export.");
        }

        if (destinationFile == null) {
            throw new IllegalArgumentException("Destination file is required.");
        }

        MangaSearchCriteria criteria = new MangaSearchCriteria();
        criteria.setAuthorId(author.getId());

        List<Manga> mangas = mangaRepository.search(criteria);

        List<MangaXmlDto> mangaDtos = mangas.stream()
                .map(this::mapToXmlDto)
                .toList();

        MangaCatalogXmlDto catalog = new MangaCatalogXmlDto(
                author.getFullName(),
                mangaDtos
        );

        try {
            JAXBContext context = JAXBContext.newInstance(MangaCatalogXmlDto.class);
            Marshaller marshaller = context.createMarshaller();

            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(catalog, destinationFile);

        } catch (Exception e) {
            throw new RuntimeException("Error while exporting manga catalog to XML", e);
        }
    }

    private MangaXmlDto mapToXmlDto(Manga manga) {
        List<String> authors = manga.getAuthors() == null
                ? List.of()
                : manga.getAuthors().stream()
                  .map(Author::getFullName)
                  .sorted()
                  .toList();

        List<String> genres = manga.getGenres() == null
                ? List.of()
                : manga.getGenres().stream()
                  .map(Genre::getName)
                  .sorted()
                  .toList();

        List<String> characters = manga.getCharacters() == null
                ? List.of()
                : manga.getCharacters().stream()
                  .map(this::formatCharacter)
                  .sorted()
                  .toList();

        return new MangaXmlDto(
                manga.getTitle(),
                manga.getDescription(),
                manga.getReleaseYear(),
                manga.getVolumes(),
                manga.getStatus() != null ? manga.getStatus().name() : "",
                manga.getPublisher() != null ? manga.getPublisher().getName() : "",
                manga.getImagePath(),
                authors,
                genres,
                characters
        );
    }

    private String formatCharacter(StoryCharacter character) {
        String fullName = character.getFullName();

        if (character.getRole() == null) {
            return fullName;
        }

        return fullName + " (" + character.getRole().name() + ")";
    }
}