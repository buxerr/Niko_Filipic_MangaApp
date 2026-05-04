package hr.algebra.mangaapp.controller;

import hr.algebra.mangaapp.model.Genre;
import hr.algebra.mangaapp.repository.GenreRepository;
import hr.algebra.mangaapp.repository.sql.SqlGenreRepository;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;

public class GenreController {

    @FXML
    public BorderPane root;

    @FXML
    private TableView<Genre> genreTableView;

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
                new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getId()));

        nameColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));

        descriptionColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDescription()));

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

        genreRepository.create(new Genre(name.trim(), description));
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

        selectedGenre.setName(name.trim());
        selectedGenre.setDescription(description);

        genreRepository.update(selectedGenre);
        messageLabel.setText("Genre updated.");

        clearForm();
        loadGenres();
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
        genreTableView.setItems(
                FXCollections.observableArrayList(genreRepository.findAll())
        );
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