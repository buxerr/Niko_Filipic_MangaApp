package hr.algebra.mangaapp.model;

public class StoryCharacter extends BaseEntity {

    private String firstName;
    private String lastName;

    public StoryCharacter() {
    }

    public StoryCharacter(String firstName, String lastName) {
        super();
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public StoryCharacter(Long id, String firstName, String lastName) {
        super(id);
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        String first = firstName != null ? firstName : "";
        String last = lastName != null ? lastName : "";

        return (first + " " + last).trim();
    }

    @Override
    public String toString() {
        String fullName = getFullName();
        return !fullName.isBlank() ? fullName : "Unnamed character";
    }
}
