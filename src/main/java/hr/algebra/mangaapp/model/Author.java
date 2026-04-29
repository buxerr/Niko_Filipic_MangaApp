package hr.algebra.mangaapp.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Author extends BaseEntity implements Comparable<Author> {

    private String firstName;
    private String lastName;

    public Author() {
    }

    public Author(String firstName, String lastName) {
        super();
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Author(Long id, String firstName, String lastName) {
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
    public int compareTo(Author o) {
        if (o == null || o.getFullName() == null) {
            return 1;
        }

        return this.getFullName().compareToIgnoreCase(o.getFullName());
    }

    @Override
    public String toString() {
        String fullName = getFullName();
        return !fullName.isBlank() ? fullName : "Unnamed author";
    }
}
