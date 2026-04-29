package hr.algebra.mangaapp.repository;

import hr.algebra.mangaapp.model.StoryCharacter;

import java.util.List;

public interface StoryCharacterRepository extends Repository<StoryCharacter> {

    List<StoryCharacter> search(String query);
}