package hr.algebra.mangaapp.xml.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "mangaCatalog")
@XmlAccessorType(XmlAccessType.FIELD)
public class MangaCatalogXmlDto {

    @XmlAttribute
    private String author;

    @XmlElement(name = "manga")
    private List<MangaXmlDto> mangas = new ArrayList<>();

    public MangaCatalogXmlDto() {
    }

    public MangaCatalogXmlDto(String author, List<MangaXmlDto> mangas) {
        this.author = author;
        this.mangas = mangas;
    }

    public String getAuthor() {
        return author;
    }

    public List<MangaXmlDto> getMangas() {
        return mangas;
    }
}