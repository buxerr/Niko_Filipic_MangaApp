package hr.algebra.mangaapp.controller;

import hr.algebra.mangaapp.model.Publisher;
import hr.algebra.mangaapp.repository.PublisherRepository;
import hr.algebra.mangaapp.repository.RepositoryFactory;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;

public class PublisherController {

    @FXML
    private BorderPane root;

    @FXML
    private TableView<Publisher> publisherTableView;

    @FXML
    private TableColumn<Publisher, Long> idColumn;

    @FXML
    private TableColumn<Publisher, String> nameColumn;

    @FXML
    private TextField searchTextField;

    @FXML
    private TextField nameTextField;

    @FXML
    private Label messageLabel;

    private final PublisherRepository publisherRepository = RepositoryFactory.getPublisherRepository();

    private final ObservableList<Publisher> publisherItems = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getId()));

        nameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getName()));

        publisherTableView.setItems(publisherItems);

        publisherTableView.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, selectedPublisher) -> {
                    if (selectedPublisher != null) {
                        fillForm(selectedPublisher);
                    }
                });

        loadPublishers();
    }

    @FXML
    private void handleAdd() {
        String name = nameTextField.getText();

        if (name == null || name.isBlank()) {
            messageLabel.setText("Publisher name is required.");
            return;
        }

        String finalName = name.trim();

        boolean alreadyExists = publisherRepository.search(name).stream()
                .anyMatch(publisher -> publisher.getName().equalsIgnoreCase(finalName));

        if (alreadyExists) {
            messageLabel.setText("Publisher already exists.");
            return;
        }

        publisherRepository.create(new Publisher(finalName));

        messageLabel.setText("Publisher added.");
        clearForm();
        loadPublishers();
    }

    @FXML
    private void handleUpdate() {
        Publisher selectedPublisher = publisherTableView.getSelectionModel().getSelectedItem();

        if (selectedPublisher == null) {
            messageLabel.setText("Select a publisher first.");
            return;
        }

        String name = nameTextField.getText();

        if (name == null || name.isBlank()) {
            messageLabel.setText("Publisher name is required.");
            return;
        }

        String finalName = name.trim();

        boolean duplicateName = publisherRepository.search(name).stream()
                .anyMatch(publisher ->
                        publisher.getName().equalsIgnoreCase(finalName)
                                && !publisher.getId().equals(selectedPublisher.getId())
                );

        if (duplicateName) {
            messageLabel.setText("Another publisher with this name already exists.");
            return;
        }

        Publisher updatedPublisher = new Publisher(
                selectedPublisher.getId(),
                finalName
        );

        publisherRepository.update(updatedPublisher);

        loadPublishers();
        publisherTableView.getSelectionModel().clearSelection();
        clearForm();

        messageLabel.setText("Publisher updated.");
    }

    @FXML
    private void handleDelete() {
        Publisher selectedPublisher = publisherTableView.getSelectionModel().getSelectedItem();

        if (selectedPublisher == null) {
            messageLabel.setText("Select a publisher first.");
            return;
        }

        publisherRepository.delete(selectedPublisher.getId());

        loadPublishers();
        publisherTableView.getSelectionModel().clearSelection();
        clearForm();

        messageLabel.setText("Publisher deleted.");
    }

    @FXML
    private void handleSearch() {
        String query = searchTextField.getText();
        publisherItems.setAll(publisherRepository.search(query));
        publisherTableView.refresh();
    }

    @FXML
    private void handleRefresh() {
        searchTextField.clear();
        loadPublishers();
        messageLabel.setText("");
    }

    @FXML
    private void handleClear() {
        publisherTableView.getSelectionModel().clearSelection();
        clearForm();
        messageLabel.setText("");
    }

    private void loadPublishers() {
        publisherItems.setAll(publisherRepository.findAll());
        publisherTableView.refresh();
    }

    private void fillForm(Publisher publisher) {
        nameTextField.setText(publisher.getName());
    }

    private void clearForm() {
        nameTextField.clear();
    }
}