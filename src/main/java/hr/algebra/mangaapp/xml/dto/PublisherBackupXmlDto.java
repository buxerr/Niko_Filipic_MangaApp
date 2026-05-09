package hr.algebra.mangaapp.xml.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class PublisherBackupXmlDto {

    private Long id;
    private String name;

    public PublisherBackupXmlDto() {
    }

    public PublisherBackupXmlDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
