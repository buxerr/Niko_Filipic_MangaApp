package hr.algebra.mangaapp.model;

public class Genre extends BaseEntity{

    private String name;
    private String description;

    public Genre() {
    }

    public Genre(String name, String description) {
        super();
        this.name = name;
        this.description = description;
    }

    public Genre(Long id, String name, String description) {
        super(id);
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return name;
    }
}
