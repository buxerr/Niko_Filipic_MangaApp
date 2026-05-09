package hr.algebra.mangaapp.xml.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class UserBackupXmlDto {

    private Long id;
    private String username;
    private String passwordHash;
    private String role;

    public UserBackupXmlDto() {
    }

    public UserBackupXmlDto(Long id, String username, String passwordHash, String role) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
    }
}
