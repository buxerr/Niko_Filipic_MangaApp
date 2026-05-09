package hr.algebra.mangaapp.xml.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;

import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class MangaBackupXmlDto {

    private Long id;
    private String title;
    private String description;
    private int releaseYear;
    private int volumes;
    private String status;
    private String imagePath;
    private Long publisherId;

    @XmlElementWrapper(name = "authorIds")
    @XmlElement(name = "authorId")
    private List<Long> authorIds = new ArrayList<>();

    @XmlElementWrapper(name = "genreIds")
    @XmlElement(name = "genreId")
    private List<Long> genreIds = new ArrayList<>();

    @XmlElementWrapper(name = "characterIds")
    @XmlElement(name = "characterId")
    private List<Long> characterIds = new ArrayList<>();

    public MangaBackupXmlDto() {
    }

    public MangaBackupXmlDto(
            Long id,
            String title,
            String description,
            int releaseYear,
            int volumes,
            String status,
            String imagePath,
            Long publisherId,
            List<Long> authorIds,
            List<Long> genreIds,
            List<Long> characterIds
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.releaseYear = releaseYear;
        this.volumes = volumes;
        this.status = status;
        this.imagePath = imagePath;
        this.publisherId = publisherId;
        this.authorIds = authorIds != null ? authorIds : new ArrayList<>();
        this.genreIds = genreIds != null ? genreIds : new ArrayList<>();
        this.characterIds = characterIds != null ? characterIds : new ArrayList<>();
    }
}
