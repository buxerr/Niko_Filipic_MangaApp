package hr.algebra.mangaapp.repository;

import hr.algebra.mangaapp.model.User;

import java.util.Optional;

public interface UserRepository extends Repository<User> {

    Optional<User> findByUsername(String username);

    boolean usernameExists(String username);
}