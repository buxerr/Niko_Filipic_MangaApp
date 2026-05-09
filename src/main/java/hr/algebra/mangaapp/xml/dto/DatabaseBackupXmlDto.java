package hr.algebra.mangaapp.xml.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "databaseBackup")
@XmlAccessorType(XmlAccessType.FIELD)
public class DatabaseBackupXmlDto {

    @XmlAttribute
    private String generatedAt;

    @XmlElementWrapper(name = "publishers")
    @XmlElement(name = "publisher")
    private List<PublisherBackupXmlDto> publishers = new ArrayList<>();

    @XmlElementWrapper(name = "authors")
    @XmlElement(name = "author")
    private List<AuthorBackupXmlDto> authors = new ArrayList<>();

    @XmlElementWrapper(name = "genres")
    @XmlElement(name = "genre")
    private List<GenreBackupXmlDto> genres = new ArrayList<>();

    @XmlElementWrapper(name = "characters")
    @XmlElement(name = "character")
    private List<StoryCharacterBackupXmlDto> characters = new ArrayList<>();

    @XmlElementWrapper(name = "mangas")
    @XmlElement(name = "manga")
    private List<MangaBackupXmlDto> mangas = new ArrayList<>();

    @XmlElementWrapper(name = "users")
    @XmlElement(name = "user")
    private List<UserBackupXmlDto> users = new ArrayList<>();

    public DatabaseBackupXmlDto() {
    }

    public DatabaseBackupXmlDto(
            String generatedAt,
            List<PublisherBackupXmlDto> publishers,
            List<AuthorBackupXmlDto> authors,
            List<GenreBackupXmlDto> genres,
            List<StoryCharacterBackupXmlDto> characters,
            List<MangaBackupXmlDto> mangas,
            List<UserBackupXmlDto> users
    ) {
        this.generatedAt = generatedAt;
        this.publishers = publishers != null ? publishers : new ArrayList<>();
        this.authors = authors != null ? authors : new ArrayList<>();
        this.genres = genres != null ? genres : new ArrayList<>();
        this.characters = characters != null ? characters : new ArrayList<>();
        this.mangas = mangas != null ? mangas : new ArrayList<>();
        this.users = users != null ? users : new ArrayList<>();
    }
}
