package hr.algebra.mangaapp.model;

import hr.algebra.mangaapp.model.enums.CharacterRole;

public class StoryCharacter extends BaseEntity {

    private String firstName;
    private String lastName;
    private CharacterRole role;

    public StoryCharacter() {
    }

    public StoryCharacter(String firstName, String lastName, CharacterRole role) {
        super();
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
    }

    public StoryCharacter(Long id, String firstName, String lastName, CharacterRole role) {
        super(id);
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
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

    public CharacterRole getRole() {
        return role;
    }

    public void setRole(CharacterRole role) {
        this.role = role;
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
