package hr.algebra.mangaapp.model;

import hr.algebra.mangaapp.model.enums.UserRole;

public class User extends BaseEntity {

    private String username;
    private String passwordHash;
    private UserRole role;

    public User() {
    }

    public User(String username, String passwordHash, UserRole role) {
        super();
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public User(Long id, String username, String passwordHash, UserRole role) {
        super(id);
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public boolean isAdmin() {
        return role == UserRole.ADMIN;
    }

    @Override
    public String toString() {
        return username != null ? username : "Unnamed user";
    }
}