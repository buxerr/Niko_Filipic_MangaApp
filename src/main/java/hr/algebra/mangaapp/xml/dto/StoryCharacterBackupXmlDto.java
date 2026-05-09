package hr.algebra.mangaapp.xml.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class StoryCharacterBackupXmlDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String role;

    public StoryCharacterBackupXmlDto() {
    }

    public StoryCharacterBackupXmlDto(Long id, String firstName, String lastName, String role) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
    }
}
