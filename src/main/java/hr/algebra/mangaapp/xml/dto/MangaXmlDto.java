package hr.algebra.mangaapp.xml.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;

import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class MangaXmlDto {

    private String title;
    private String description;
    private int releaseYear;
    private int volumes;
    private String status;
    private String publisher;
    private String imagePath;

    @XmlElementWrapper(name = "authors")
    @XmlElement(name = "author")
    private List<String> authors = new ArrayList<>();

    @XmlElementWrapper(name = "genres")
    @XmlElement(name = "genre")
    private List<String> genres = new ArrayList<>();

    @XmlElementWrapper(name = "characters")
    @XmlElement(name = "character")
    private List<String> characters = new ArrayList<>();

    public MangaXmlDto() {
    }

    public MangaXmlDto(
            String title,
            String description,
            int releaseYear,
            int volumes,
            String status,
            String publisher,
            String imagePath,
            List<String> authors,
            List<String> genres,
            List<String> characters
    ) {
        this.title = title;
        this.description = description;
        this.releaseYear = releaseYear;
        this.volumes = volumes;
        this.status = status;
        this.publisher = publisher;
        this.imagePath = imagePath;
        this.authors = authors;
        this.genres = genres;
        this.characters = characters;
    }
}