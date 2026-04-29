package hr.algebra.mangaapp.repository;

import hr.algebra.mangaapp.model.Publisher;

import java.util.List;

public interface PublisherRepository extends Repository<Publisher> {

    List<Publisher> search(String query);
}