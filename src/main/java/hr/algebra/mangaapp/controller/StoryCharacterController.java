package hr.algebra.mangaapp.controller;

import hr.algebra.mangaapp.model.StoryCharacter;
import hr.algebra.mangaapp.repository.StoryCharacterRepository;
import hr.algebra.mangaapp.repository.sql.SqlStoryCharacterRepository;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class StoryCharacterController {

    @FXML
    private TableView<StoryCharacter> characterTableView;

    @FXML
    private TableColumn<StoryCharacter, Long> idColumn;

    @FXML
    private TableColumn<StoryCharacter, String> firstNameColumn;

    @FXML
    private TableColumn<StoryCharacter, String> lastNameColumn;

    @FXML
    private TextField searchTextField;

    @FXML
    private TextField firstNameTextField;

    @FXML
    private TextField lastNameTextField;

    @FXML
    private Label messageLabel;

    private final StoryCharacterRepository characterRepository = new SqlStoryCharacterRepository();

    private final ObservableList<StoryCharacter> characterItems = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getId()));

        firstNameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue().getFirstName() != null
                                ? cellData.getValue().getFirstName()
                                : ""
                ));

        lastNameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue().getLastName() != null
                                ? cellData.getValue().getLastName()
                                : ""
                ));

        characterTableView.setItems(characterItems);

        characterTableView.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, selectedCharacter) -> {
                    if (selectedCharacter != null) {
                        fillForm(selectedCharacter);
                    }
                });

        loadCharacters();
    }

    @FXML
    private void handleAdd() {
        String firstName = firstNameTextField.getText();
        String lastName = lastNameTextField.getText();

        if (firstName == null || firstName.isBlank()) {
            messageLabel.setText("First name is required.");
            return;
        }

        String finalFirstName = firstName.trim();
        String finalLastName = lastName == null || lastName.isBlank() ? null : lastName.trim();

        boolean alreadyExists = characterRepository.search(firstName).stream()
                .anyMatch(character ->
                        safeEqualsIgnoreCase(character.getFirstName(), finalFirstName)
                                && safeEqualsIgnoreCase(character.getLastName(), finalLastName)
                );

        if (alreadyExists) {
            messageLabel.setText("Character already exists.");
            return;
        }

        characterRepository.create(new StoryCharacter(firstName, lastName));

        loadCharacters();
        clearForm();

        messageLabel.setText("Character added.");
    }

    @FXML
    private void handleUpdate() {
        StoryCharacter selectedCharacter = characterTableView.getSelectionModel().getSelectedItem();

        if (selectedCharacter == null) {
            messageLabel.setText("Select a character first.");
            return;
        }

        String firstName = firstNameTextField.getText();
        String lastName = lastNameTextField.getText();

        if (firstName == null || firstName.isBlank()) {
            messageLabel.setText("First name is required.");
            return;
        }

        String finalFirstName = firstName.trim();
        String finalLastName = lastName == null || lastName.isBlank() ? null : lastName.trim();

        boolean duplicateCharacter = characterRepository.search(firstName).stream()
                .anyMatch(character ->
                        safeEqualsIgnoreCase(character.getFirstName(), finalFirstName)
                                && safeEqualsIgnoreCase(character.getLastName(), finalLastName)
                                && !character.getId().equals(selectedCharacter.getId())
                );

        if (duplicateCharacter) {
            messageLabel.setText("Another character with this name already exists.");
            return;
        }

        StoryCharacter updatedCharacter = new StoryCharacter(
                selectedCharacter.getId(),
                firstName,
                lastName
        );

        characterRepository.update(updatedCharacter);

        loadCharacters();
        characterTableView.getSelectionModel().clearSelection();
        clearForm();

        messageLabel.setText("Character updated.");
    }

    @FXML
    private void handleDelete() {
        StoryCharacter selectedCharacter = characterTableView.getSelectionModel().getSelectedItem();

        if (selectedCharacter == null) {
            messageLabel.setText("Select a character first.");
            return;
        }

        characterRepository.delete(selectedCharacter.getId());

        loadCharacters();
        characterTableView.getSelectionModel().clearSelection();
        clearForm();

        messageLabel.setText("Character deleted.");
    }

    @FXML
    private void handleSearch() {
        String query = searchTextField.getText();
        characterItems.setAll(characterRepository.search(query));
        characterTableView.refresh();
    }

    @FXML
    private void handleRefresh() {
        searchTextField.clear();
        loadCharacters();
        messageLabel.setText("");
    }

    @FXML
    private void handleClear() {
        characterTableView.getSelectionModel().clearSelection();
        clearForm();
        messageLabel.setText("");
    }

    private void loadCharacters() {
        characterItems.setAll(characterRepository.findAll());
        characterTableView.refresh();
    }

    private void fillForm(StoryCharacter character) {
        firstNameTextField.setText(character.getFirstName() != null ? character.getFirstName() : "");
        lastNameTextField.setText(character.getLastName() != null ? character.getLastName() : "");
    }

    private void clearForm() {
        firstNameTextField.clear();
        lastNameTextField.clear();
    }

    private boolean safeEqualsIgnoreCase(String first, String second) {
        if (first == null && second == null) {
            return true;
        }

        if (first == null || second == null) {
            return false;
        }

        return first.equalsIgnoreCase(second);
    }
}