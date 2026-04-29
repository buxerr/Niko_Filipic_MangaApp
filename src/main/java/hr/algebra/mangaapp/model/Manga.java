package hr.algebra.mangaapp.model;

import java.util.HashSet;
import java.util.Set;

public class Manga extends BaseEntity implements Comparable<Manga> {

    private String title;
    private String description;
    private int releaseYear;
    private int volumes;
    private String imagePath;

    private Publisher publisher;
    private Set<StoryCharacter> characters = new HashSet<>();
    private Set<Genre> genres = new HashSet<>();
    private Set<Author> authors = new HashSet<>();

    public Manga() {}

    public Manga(String title, String description, int releaseYear, int volumes, Publisher publisher, String imagePath, Set<StoryCharacter> characters, Set<Genre> genres, Set<Author> authors) {
        super();
        this.title = title;
        this.description = description;
        this.releaseYear = releaseYear;
        this.volumes = volumes;
        this.imagePath = imagePath;
        this.publisher = publisher;
        this.characters = characters != null ? characters : new HashSet<>();
        this.genres = genres != null ? genres : new HashSet<>();
        this.authors = authors != null ? authors : new HashSet<>();
    }

    public Manga(Long id, String title, String description, int releaseYear, int volumes, Publisher publisher, String imagePath, Set<StoryCharacter> characters, Set<Genre> genres, Set<Author> authors) {
        super(id);
        this.title = title;
        this.description = description;
        this.releaseYear = releaseYear;
        this.volumes = volumes;
        this.publisher = publisher;
        this.imagePath = imagePath;
        this.characters = characters != null ? characters : new HashSet<>();
        this.genres = genres != null ? genres : new HashSet<>();
        this.authors = authors != null ? authors : new HashSet<>();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public int getVolumes() {
        return volumes;
    }

    public void setVolumes(int volumes) {
        this.volumes = volumes;
    }

    public Publisher getPublisher() {
        return publisher;
    }

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }

    public Set<StoryCharacter> getCharacters() {
        return characters;
    }

    public void setCharacters(Set<StoryCharacter> characters) {
        this.characters = characters != null ? characters : new HashSet<>();
    }

    public Set<Genre> getGenres() {
        return genres;
    }

    public void setGenres(Set<Genre> genres) {
        this.genres = genres != null ? genres : new HashSet<>();
    }

    public Set<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(Set<Author> authors) {
        this.authors = authors != null ? authors : new HashSet<>();
    }

    @Override
    public int compareTo(Manga other) {
        if (other == null || other.getTitle() == null) {
            return 1;
        }

        if (this.title == null) {
            return -1;
        }

        return this.title.compareToIgnoreCase(other.getTitle());
    }

    @Override
    public String toString() {
        return (title != null ? title : "Untitled") + " (" + releaseYear + ")";
    }
}
