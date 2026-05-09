package hr.algebra.mangaapp.xml.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class AuthorBackupXmlDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String orientation;

    public AuthorBackupXmlDto() {
    }

    public AuthorBackupXmlDto(Long id, String firstName, String lastName, String orientation) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.orientation = orientation;
    }
}
