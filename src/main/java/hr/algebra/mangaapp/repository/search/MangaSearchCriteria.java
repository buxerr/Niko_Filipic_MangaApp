package hr.algebra.mangaapp.repository.search;

import hr.algebra.mangaapp.model.enums.MangaStatus;

public class MangaSearchCriteria {

    private String title;
    private Long genreId;
    private Long authorId;
    private Long publisherId;
    private MangaStatus status;
    private Integer releaseYearFrom;
    private Integer releaseYearTo;

    public MangaSearchCriteria() {
    }

    public String getTitle() {
        return title;
    }

    public MangaSearchCriteria setTitle(String title) {
        this.title = title;
        return this;
    }

    public Long getGenreId() {
        return genreId;
    }

    public MangaSearchCriteria setGenreId(Long genreId) {
        this.genreId = genreId;
        return this;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public MangaSearchCriteria setAuthorId(Long authorId) {
        this.authorId = authorId;
        return this;
    }

    public Long getPublisherId() {
        return publisherId;
    }

    public MangaSearchCriteria setPublisherId(Long publisherId) {
        this.publisherId = publisherId;
        return this;
    }

    public MangaStatus getStatus() {
        return status;
    }

    public MangaSearchCriteria setStatus(MangaStatus status) {
        this.status = status;
        return this;
    }

    public Integer getReleaseYearFrom() {
        return releaseYearFrom;
    }

    public MangaSearchCriteria setReleaseYearFrom(Integer releaseYearFrom) {
        this.releaseYearFrom = releaseYearFrom;
        return this;
    }

    public Integer getReleaseYearTo() {
        return releaseYearTo;
    }

    public MangaSearchCriteria setReleaseYearTo(Integer releaseYearTo) {
        this.releaseYearTo = releaseYearTo;
        return this;
    }

    public boolean isEmpty() {
        return (title == null || title.isBlank())
                && genreId == null
                && authorId == null
                && publisherId == null
                && status == null
                && releaseYearFrom == null
                && releaseYearTo == null;
    }
}