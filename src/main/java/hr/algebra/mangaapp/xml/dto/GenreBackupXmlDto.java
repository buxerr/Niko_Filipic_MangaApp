package hr.algebra.mangaapp.xml.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class GenreBackupXmlDto {

    private Long id;
    private String name;
    private String description;

    public GenreBackupXmlDto() {
    }

    public GenreBackupXmlDto(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
}
