package hr.algebra.mangaapp.service;

import hr.algebra.mangaapp.model.Genre;
import hr.algebra.mangaapp.model.Manga;
import hr.algebra.mangaapp.model.enums.MangaStatus;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class StatisticsService {

    public String buildReport(List<Manga> mangas) {
        if (mangas == null || mangas.isEmpty()) {
            return "No manga/comics available in the catalog.";
        }

        long totalManga = mangas.size();

        double averageVolumes = mangas.stream()
                .mapToInt(Manga::getVolumes)
                .average()
                .orElse(0);

        Optional<Manga> oldestManga = mangas.stream()
                .min(Comparator.comparingInt(Manga::getReleaseYear));

        Predicate<Manga> hasStatus = manga -> manga.getStatus() != null;
        Predicate<Manga> hasPublisher = manga -> manga.getPublisher() != null;
        Predicate<Manga> hasGenres = manga -> manga.getGenres() != null;

        Function<Manga, MangaStatus> statusMapper = Manga::getStatus;
        Function<Manga, String> publisherNameMapper = manga -> manga.getPublisher().getName();
        Function<Genre, String> genreNameMapper = Genre::getName;

        Map<MangaStatus, Long> mangaByStatus = mangas.stream()
                .filter(hasStatus)
                .collect(Collectors.groupingBy(
                        statusMapper,
                        Collectors.counting()
                ));

        Map<String, Long> mangaByPublisher = mangas.stream()
                .filter(hasPublisher)
                .collect(Collectors.groupingBy(
                        publisherNameMapper,
                        Collectors.counting()
                ));

        Map<String, Long> genreUsage = mangas.stream()
                .filter(hasGenres)
                .flatMap(manga -> manga.getGenres().stream())
                .collect(Collectors.groupingBy(
                        genreNameMapper,
                        Collectors.counting()
                ));

        StringBuilder sb = new StringBuilder();
        Consumer<Map.Entry<?, Long>> appendEntry = entry ->
                sb.append(entry.getKey())
                        .append(": ")
                        .append(entry.getValue())
                        .append("\n");

        sb.append("GENERAL\n");
        sb.append("Total manga/comics: ").append(totalManga).append("\n");
        sb.append("Average number of volumes: ")
                .append(String.format("%.2f", averageVolumes))
                .append("\n");

        oldestManga.ifPresent(manga -> {
            sb.append("Oldest title: ")
                    .append(manga.getTitle())
                    .append(" (")
                    .append(manga.getReleaseYear())
                    .append(")\n");
        });

        sb.append("\nMANGA BY STATUS\n");
        mangaByStatus.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(appendEntry);

        sb.append("\nMANGA BY PUBLISHER\n");
        mangaByPublisher.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .forEach(appendEntry);

        sb.append("\nGENRE USAGE\n");
        genreUsage.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .forEach(appendEntry);

        return sb.toString();
    }
}
