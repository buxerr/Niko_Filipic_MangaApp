package hr.algebra.mangaapp.model;

public class Publisher extends BaseEntity {

    private String name;

    public Publisher() {
    }

    public Publisher(String name) {
        super();
        this.name = name;
    }

    public Publisher(Long id, String name) {
        super(id);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name != null ? name : "Unnamed publisher";
    }
}
