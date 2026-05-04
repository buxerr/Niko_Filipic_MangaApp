package hr.algebra.mangaapp.controller;

import hr.algebra.mangaapp.model.Genre;
import hr.algebra.mangaapp.repository.GenreRepository;
import hr.algebra.mangaapp.repository.sql.SqlGenreRepository;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;

public class GenreController {

    @FXML
    public BorderPane root;

    @FXML
    private TableView<Genre> genreTableView;
    private final ObservableList<Genre> genreItems = FXCollections.observableArrayList();

    @FXML
    private TableColumn<Genre, Long> idColumn;

    @FXML
    private TableColumn<Genre, String> nameColumn;

    @FXML
    private TableColumn<Genre, String> descriptionColumn;

    @FXML
    private TextField searchTextField;

    @FXML
    private TextField nameTextField;

    @FXML
    private TextArea descriptionTextArea;

    @FXML
    private Label messageLabel;

    private final GenreRepository genreRepository = new SqlGenreRepository();

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getId()));

        nameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getName()));

        descriptionColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue().getDescription() != null
                                ? cellData.getValue().getDescription()
                                : ""
                ));

        genreTableView.setItems(genreItems);

        genreTableView.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, selectedGenre) -> {
                    if (selectedGenre != null) {
                        fillForm(selectedGenre);
                    }
                });

        loadGenres();
    }

    @FXML
    private void handleAdd() {
        String name = nameTextField.getText();
        String description = descriptionTextArea.getText();

        if (name == null || name.isBlank()) {
            messageLabel.setText("Genre name is required.");
            return;
        }

        name = name.trim();

        if (genreRepository.existsByName(name)) {
            messageLabel.setText("Genre already exists.");
            return;
        }

        genreRepository.create(new Genre(name, description));
        messageLabel.setText("Genre added.");

        clearForm();
        loadGenres();
    }

    @FXML
    private void handleUpdate() {
        Genre selectedGenre = genreTableView.getSelectionModel().getSelectedItem();

        if (selectedGenre == null) {
            messageLabel.setText("Select a genre first.");
            return;
        }

        String name = nameTextField.getText();
        String description = descriptionTextArea.getText();

        if (name == null || name.isBlank()) {
            messageLabel.setText("Genre name is required.");
            return;
        }

        Genre updatedGenre = new Genre(
                selectedGenre.getId(),
                name.trim(),
                description == null || description.isBlank() ? null : description.trim()
        );

        genreRepository.update(updatedGenre);

        loadGenres();

        genreTableView.getSelectionModel().clearSelection();
        clearForm();

        messageLabel.setText("Genre updated.");
    }

    @FXML
    private void handleDelete() {
        Genre selectedGenre = genreTableView.getSelectionModel().getSelectedItem();

        if (selectedGenre == null) {
            messageLabel.setText("Select a genre first.");
            return;
        }

        genreRepository.delete(selectedGenre.getId());
        messageLabel.setText("Genre deleted.");

        clearForm();
        loadGenres();
    }

    @FXML
    private void handleSearch() {
        String query = searchTextField.getText();
        genreTableView.setItems(
                FXCollections.observableArrayList(genreRepository.search(query))
        );
    }

    @FXML
    private void handleRefresh() {
        searchTextField.clear();
        loadGenres();
    }

    @FXML
    private void handleClear() {
        clearForm();
        genreTableView.getSelectionModel().clearSelection();
    }

    private void loadGenres() {
        genreItems.setAll(genreRepository.findAll());
        genreTableView.refresh();
    }

    private void fillForm(Genre genre) {
        nameTextField.setText(genre.getName());
        descriptionTextArea.setText(genre.getDescription());
    }

    private void clearForm() {
        nameTextField.clear();
        descriptionTextArea.clear();
    }
}