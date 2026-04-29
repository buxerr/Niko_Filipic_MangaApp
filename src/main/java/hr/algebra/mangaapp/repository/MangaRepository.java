package hr.algebra.mangaapp.repository;

import hr.algebra.mangaapp.model.Manga;
import hr.algebra.mangaapp.repository.search.MangaSearchCriteria;

import java.util.List;

public interface MangaRepository extends Repository<Manga> {

    List<Manga> search(MangaSearchCriteria criteria);

    void addGenre(Long mangaId, Long genreId);

    void removeGenre(Long mangaId, Long genreId);

    void addAuthor(Long mangaId, Long authorId);

    void removeAuthor(Long mangaId, Long authorId);

    void addCharacter(Long mangaId, Long characterId);

    void removeCharacter(Long mangaId, Long characterId);


}