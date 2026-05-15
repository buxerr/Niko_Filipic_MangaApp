package hr.algebra.mangaapp.controller;

import hr.algebra.mangaapp.model.StoryCharacter;
import hr.algebra.mangaapp.model.enums.CharacterRole;
import hr.algebra.mangaapp.repository.RepositoryFactory;
import hr.algebra.mangaapp.repository.StoryCharacterRepository;
import hr.algebra.mangaapp.util.ComboBoxUtils;
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
    private TableColumn<StoryCharacter, String> roleColumn;

    @FXML
    private TextField searchTextField;

    @FXML
    private ComboBox<CharacterRole> searchRoleComboBox;

    @FXML
    private TextField firstNameTextField;

    @FXML
    private TextField lastNameTextField;

    @FXML
    private ComboBox<CharacterRole> roleComboBox;

    @FXML
    private Label messageLabel;

    private final StoryCharacterRepository characterRepository =
            RepositoryFactory.getStoryCharacterRepository();

    private final ObservableList<StoryCharacter> characterItems =
            FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        setupTableColumns();
        setupComboBoxes();
        setupSelectionListener();

        characterTableView.setItems(characterItems);

        loadCharacters();
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getId()));

        firstNameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(nullSafe(cellData.getValue().getFirstName())));

        lastNameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(nullSafe(cellData.getValue().getLastName())));

        roleColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(formatRole(cellData.getValue().getRole())));
    }

    private void setupComboBoxes() {
        roleComboBox.setItems(
                FXCollections.observableArrayList(CharacterRole.values())
        );

        searchRoleComboBox.setItems(
                FXCollections.observableArrayList(CharacterRole.values())
        );

        ComboBoxUtils.resetWithPrompt(roleComboBox, "Character role");
        ComboBoxUtils.resetWithPrompt(searchRoleComboBox, "Role");
    }

    private void setupSelectionListener() {
        characterTableView.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, selectedCharacter) -> {
                    if (selectedCharacter != null) {
                        fillForm(selectedCharacter);
                    }
                });
    }

    @FXML
    private void handleAdd() {
        String firstName = firstNameTextField.getText();
        String lastName = lastNameTextField.getText();
        CharacterRole role = roleComboBox.getValue();

        if (firstName == null || firstName.isBlank()) {
            messageLabel.setText("First name is required.");
            return;
        }

        if (role == null) {
            messageLabel.setText("Character role is required.");
            return;
        }

        String finalFirstName = firstName.trim();
        String finalLastName = lastName == null || lastName.isBlank()
                ? null
                : lastName.trim();

        boolean alreadyExists = characterRepository.search(finalFirstName).stream()
                .anyMatch(character ->
                        safeEqualsIgnoreCase(character.getFirstName(), finalFirstName)
                                && safeEqualsIgnoreCase(character.getLastName(), finalLastName)
                                && character.getRole() == role
                );

        if (alreadyExists) {
            messageLabel.setText("Character already exists.");
            return;
        }

        StoryCharacter newCharacter = new StoryCharacter(
                finalFirstName,
                finalLastName,
                role
        );

        characterRepository.create(newCharacter);

        loadCharacters();
        clearForm();

        messageLabel.setText("Character added.");
    }

    @FXML
    private void handleUpdate() {
        StoryCharacter selectedCharacter =
                characterTableView.getSelectionModel().getSelectedItem();

        if (selectedCharacter == null) {
            messageLabel.setText("Select a character first.");
            return;
        }

        String firstName = firstNameTextField.getText();
        String lastName = lastNameTextField.getText();
        CharacterRole role = roleComboBox.getValue();

        if (firstName == null || firstName.isBlank()) {
            messageLabel.setText("First name is required.");
            return;
        }

        if (role == null) {
            messageLabel.setText("Character role is required.");
            return;
        }

        String finalFirstName = firstName.trim();
        String finalLastName = lastName == null || lastName.isBlank()
                ? null
                : lastName.trim();

        boolean duplicateCharacter = characterRepository.search(finalFirstName).stream()
                .anyMatch(character ->
                        safeEqualsIgnoreCase(character.getFirstName(), finalFirstName)
                                && safeEqualsIgnoreCase(character.getLastName(), finalLastName)
                                && character.getRole() == role
                                && !character.getId().equals(selectedCharacter.getId())
                );

        if (duplicateCharacter) {
            messageLabel.setText("Another character with this name and role already exists.");
            return;
        }

        StoryCharacter updatedCharacter = new StoryCharacter(
                selectedCharacter.getId(),
                finalFirstName,
                finalLastName,
                role
        );

        characterRepository.update(updatedCharacter);

        loadCharacters();
        characterTableView.getSelectionModel().clearSelection();
        clearForm();

        messageLabel.setText("Character updated.");
    }

    @FXML
    private void handleDelete() {
        StoryCharacter selectedCharacter =
                characterTableView.getSelectionModel().getSelectedItem();

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
        CharacterRole selectedRole = searchRoleComboBox.getValue();

        characterItems.setAll(
                characterRepository.search(query).stream()
                        .filter(character ->
                                selectedRole == null || character.getRole() == selectedRole
                        )
                        .toList()
        );

        characterTableView.refresh();
        messageLabel.setText("Search completed.");
    }

    @FXML
    private void handleRefresh() {
        searchTextField.clear();
        ComboBoxUtils.resetWithPrompt(searchRoleComboBox, "Role");

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
        firstNameTextField.setText(nullSafe(character.getFirstName()));
        lastNameTextField.setText(nullSafe(character.getLastName()));
        roleComboBox.setValue(character.getRole());
    }

    private void clearForm() {
        firstNameTextField.clear();
        lastNameTextField.clear();
        ComboBoxUtils.resetWithPrompt(roleComboBox, "Character role");
    }

    private String formatRole(CharacterRole role) {
        if (role == null) {
            return "";
        }

        return switch (role) {
            case MAIN -> "Main";
            case SUPPORTING -> "Supporting";
            case ANTAGONIST -> "Antagonist";
        };
    }

    private String nullSafe(String value) {
        return value != null ? value : "";
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