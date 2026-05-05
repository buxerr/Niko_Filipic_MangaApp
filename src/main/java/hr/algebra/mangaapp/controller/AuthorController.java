package hr.algebra.mangaapp.controller;

import hr.algebra.mangaapp.model.Author;
import hr.algebra.mangaapp.model.enums.AuthorType;
import hr.algebra.mangaapp.repository.AuthorRepository;
import hr.algebra.mangaapp.repository.RepositoryFactory;
import hr.algebra.mangaapp.repository.sql.SqlAuthorRepository;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class AuthorController {

    @FXML
    private TableView<Author> authorTableView;

    @FXML
    private TableColumn<Author, Long> idColumn;

    @FXML
    private TableColumn<Author, String> firstNameColumn;

    @FXML
    private TableColumn<Author, String> lastNameColumn;

    @FXML
    private TableColumn<Author, String> orientationColumn;

    @FXML
    private TextField searchTextField;

    @FXML
    private TextField firstNameTextField;

    @FXML
    private TextField lastNameTextField;

    @FXML
    private ComboBox<AuthorType> orientationComboBox;

    @FXML
    private Label messageLabel;

    private final AuthorRepository authorRepository = RepositoryFactory.getAuthorRepository();

    private final ObservableList<Author> authorItems = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getId()));

        firstNameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getFirstName()));

        lastNameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getLastName()));

        orientationColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue().getOrientation() != null
                                ? cellData.getValue().getOrientation().name()
                                : ""
                ));

        orientationComboBox.setItems(
                FXCollections.observableArrayList(AuthorType.values())
        );

        authorTableView.setItems(authorItems);

        authorTableView.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, selectedAuthor) -> {
                    if (selectedAuthor != null) {
                        fillForm(selectedAuthor);
                    }
                });

        loadAuthors();
    }

    @FXML
    private void handleAdd() {
        String firstName = firstNameTextField.getText();
        String lastName = lastNameTextField.getText();
        AuthorType orientation = orientationComboBox.getValue();

        if (firstName == null || firstName.isBlank()) {
            messageLabel.setText("First name is required.");
            return;
        }

        if (lastName == null || lastName.isBlank()) {
            messageLabel.setText("Last name is required.");
            return;
        }

        if (orientation == null) {
            messageLabel.setText("Orientation is required.");
            return;
        }

        String finalFirstName = firstName.trim();
        String finalLastName = lastName.trim();

        boolean alreadyExists = authorRepository.search(firstName + " " + lastName).stream()
                .anyMatch(author ->
                        author.getFirstName().equalsIgnoreCase(finalFirstName)
                                && author.getLastName().equalsIgnoreCase(finalLastName)
                                && author.getOrientation() == orientation
                );

        if (alreadyExists) {
            messageLabel.setText("Author already exists.");
            return;
        }

        authorRepository.create(new Author(firstName, lastName, orientation));

        loadAuthors();
        clearForm();

        messageLabel.setText("Author added.");
    }

    @FXML
    private void handleUpdate() {
        Author selectedAuthor = authorTableView.getSelectionModel().getSelectedItem();

        if (selectedAuthor == null) {
            messageLabel.setText("Select an author first.");
            return;
        }

        String firstName = firstNameTextField.getText();
        String lastName = lastNameTextField.getText();
        AuthorType orientation = orientationComboBox.getValue();

        if (firstName == null || firstName.isBlank()) {
            messageLabel.setText("First name is required.");
            return;
        }

        if (lastName == null || lastName.isBlank()) {
            messageLabel.setText("Last name is required.");
            return;
        }

        if (orientation == null) {
            messageLabel.setText("Orientation is required.");
            return;
        }

        String finalFirstName = firstName.trim();
        String finalLastName = lastName.trim();

        boolean duplicateAuthor = authorRepository.search(firstName + " " + lastName).stream()
                .anyMatch(author ->
                        author.getFirstName().equalsIgnoreCase(finalFirstName)
                                && author.getLastName().equalsIgnoreCase(finalLastName)
                                && author.getOrientation() == orientation
                                && !author.getId().equals(selectedAuthor.getId())
                );

        if (duplicateAuthor) {
            messageLabel.setText("Another author with this name and orientation already exists.");
            return;
        }

        Author updatedAuthor = new Author(
                selectedAuthor.getId(),
                firstName,
                lastName,
                orientation
        );

        authorRepository.update(updatedAuthor);

        loadAuthors();
        authorTableView.getSelectionModel().clearSelection();
        clearForm();

        messageLabel.setText("Author updated.");
    }

    @FXML
    private void handleDelete() {
        Author selectedAuthor = authorTableView.getSelectionModel().getSelectedItem();

        if (selectedAuthor == null) {
            messageLabel.setText("Select an author first.");
            return;
        }

        authorRepository.delete(selectedAuthor.getId());

        loadAuthors();
        authorTableView.getSelectionModel().clearSelection();
        clearForm();

        messageLabel.setText("Author deleted.");
    }

    @FXML
    private void handleSearch() {
        String query = searchTextField.getText();
        authorItems.setAll(authorRepository.search(query));
        authorTableView.refresh();
    }

    @FXML
    private void handleRefresh() {
        searchTextField.clear();
        loadAuthors();
        messageLabel.setText("");
    }

    @FXML
    private void handleClear() {
        authorTableView.getSelectionModel().clearSelection();
        clearForm();
        messageLabel.setText("");
    }

    private void loadAuthors() {
        authorItems.setAll(authorRepository.findAll());
        authorTableView.refresh();
    }

    private void fillForm(Author author) {
        firstNameTextField.setText(author.getFirstName());
        lastNameTextField.setText(author.getLastName());
        orientationComboBox.setValue(author.getOrientation());
    }

    private void clearForm() {
        firstNameTextField.clear();
        lastNameTextField.clear();
        orientationComboBox.getSelectionModel().clearSelection();
    }
}