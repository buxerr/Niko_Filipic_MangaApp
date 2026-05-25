package hr.algebra.mangaapp.model;

import hr.algebra.mangaapp.model.enums.AuthorType;

public class Author extends BaseEntity implements Comparable<Author> {

    private String firstName;
    private String lastName;
    private AuthorType orientation;

    public Author() {
    }

    public Author(String firstName, String lastName, AuthorType orientation) {
        super();
        this.firstName = firstName;
        this.lastName = lastName;
        this.orientation = orientation;
    }

    public Author(Long id, String firstName, String lastName, AuthorType orientation) {
        super(id);
        this.firstName = firstName;
        this.lastName = lastName;
        this.orientation = orientation;
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

    public AuthorType getOrientation() {
        return orientation;
    }

    public void setOrientation(AuthorType orientation) {
        this.orientation = orientation;
    }

    public String getFullName() {
        String first = firstName != null ? firstName : "";
        String last = lastName != null ? lastName : "";

        return (first + " " + last).trim();
    }


    @Override
    public int compareTo(Author o) {
        if (o == null || o.getFullName() == null) {
            return 1;
        }

        return this.getFullName().compareToIgnoreCase(o.getFullName());
    }

    @Override
    public String toString() {
        String fullName = getFullName();
        return !fullName.isBlank()
                ? fullName + " (" + orientation + ")"
                : "Unnamed author";
    }
}
