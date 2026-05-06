package hr.algebra.mangaapp.controller;

import hr.algebra.mangaapp.model.Author;
import hr.algebra.mangaapp.model.BaseEntity;
import hr.algebra.mangaapp.model.Genre;
import hr.algebra.mangaapp.model.Manga;
import hr.algebra.mangaapp.model.Publisher;
import hr.algebra.mangaapp.model.StoryCharacter;
import hr.algebra.mangaapp.model.enums.MangaStatus;
import hr.algebra.mangaapp.repository.AuthorRepository;
import hr.algebra.mangaapp.repository.GenreRepository;
import hr.algebra.mangaapp.repository.MangaRepository;
import hr.algebra.mangaapp.repository.PublisherRepository;
import hr.algebra.mangaapp.repository.RepositoryFactory;
import hr.algebra.mangaapp.repository.StoryCharacterRepository;
import hr.algebra.mangaapp.repository.search.MangaSearchCriteria;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class MangaController {

    @FXML
    private TableView<Manga> mangaTableView;

    @FXML
    private TableColumn<Manga, Long> idColumn;

    @FXML
    private TableColumn<Manga, String> titleColumn;

    @FXML
    private TableColumn<Manga, Integer> releaseYearColumn;

    @FXML
    private TableColumn<Manga, Integer> volumesColumn;

    @FXML
    private TableColumn<Manga, String> statusColumn;

    @FXML
    private TableColumn<Manga, String> publisherColumn;

    @FXML
    private TableColumn<Manga, String> authorsColumn;

    @FXML
    private TableColumn<Manga, String> genresColumn;

    @FXML
    private TableColumn<Manga, String> charactersColumn;

    @FXML
    private TextField searchTitleTextField;

    @FXML
    private ComboBox<MangaStatus> searchStatusComboBox;

    @FXML
    private ComboBox<Publisher> searchPublisherComboBox;

    @FXML
    private TextField titleTextField;

    @FXML
    private TextArea descriptionTextArea;

    @FXML
    private TextField releaseYearTextField;

    @FXML
    private TextField volumesTextField;

    @FXML
    private TextField imagePathTextField;

    @FXML
    private ComboBox<MangaStatus> statusComboBox;

    @FXML
    private ComboBox<Publisher> publisherComboBox;

    @FXML
    private ComboBox<Author> authorComboBox;

    @FXML
    private ComboBox<Genre> genreComboBox;

    @FXML
    private ComboBox<StoryCharacter> characterComboBox;

    @FXML
    private ListView<Author> selectedAuthorsListView;

    @FXML
    private ListView<Genre> selectedGenresListView;

    @FXML
    private ListView<StoryCharacter> selectedCharactersListView;

    @FXML
    private Label messageLabel;

    private final MangaRepository mangaRepository = RepositoryFactory.getMangaRepository();
    private final PublisherRepository publisherRepository = RepositoryFactory.getPublisherRepository();
    private final AuthorRepository authorRepository = RepositoryFactory.getAuthorRepository();
    private final GenreRepository genreRepository = RepositoryFactory.getGenreRepository();
    private final StoryCharacterRepository characterRepository = RepositoryFactory.getStoryCharacterRepository();

    private final ObservableList<Manga> mangaItems = FXCollections.observableArrayList();

    private final ObservableList<Author> selectedAuthors = FXCollections.observableArrayList();
    private final ObservableList<Genre> selectedGenres = FXCollections.observableArrayList();
    private final ObservableList<StoryCharacter> selectedCharacters = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        setupTableColumns();
        setupComboBoxes();
        setupListViews();
        setupSelectionListener();

        mangaTableView.setItems(mangaItems);

        loadMangas();
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getId()));

        titleColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(nullSafe(cellData.getValue().getTitle())));

        releaseYearColumn.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getReleaseYear()));

        volumesColumn.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getVolumes()));

        statusColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue().getStatus() != null
                                ? cellData.getValue().getStatus().name()
                                : ""
                ));

        publisherColumn.setCellValueFactory(cellData -> {
            Publisher publisher = cellData.getValue().getPublisher();

            return new SimpleStringProperty(
                    publisher != null ? publisher.getName() : ""
            );
        });

        authorsColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(formatAuthors(cellData.getValue().getAuthors()))
        );

        genresColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(formatGenres(cellData.getValue().getGenres()))
        );

        charactersColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(formatCharacters(cellData.getValue().getCharacters()))
        );
    }

    private void setupComboBoxes() {
        ObservableList<MangaStatus> statuses =
                FXCollections.observableArrayList(MangaStatus.values());

        statusComboBox.setItems(statuses);
        searchStatusComboBox.setItems(FXCollections.observableArrayList(MangaStatus.values()));

        ObservableList<Publisher> publishers =
                FXCollections.observableArrayList(publisherRepository.findAll());

        publisherComboBox.setItems(publishers);
        searchPublisherComboBox.setItems(FXCollections.observableArrayList(publisherRepository.findAll()));

        authorComboBox.setItems(
                FXCollections.observableArrayList(authorRepository.findAll())
        );

        genreComboBox.setItems(
                FXCollections.observableArrayList(genreRepository.findAll())
        );

        characterComboBox.setItems(
                FXCollections.observableArrayList(characterRepository.findAll())
        );
    }

    private void setupListViews() {
        selectedAuthorsListView.setItems(selectedAuthors);
        selectedGenresListView.setItems(selectedGenres);
        selectedCharactersListView.setItems(selectedCharacters);
    }

    private void setupSelectionListener() {
        mangaTableView.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, selectedManga) -> {
                    if (selectedManga != null) {
                        fillForm(selectedManga);
                    }
                });
    }

    @FXML
    private void handleAdd() {
        Manga manga = buildMangaFromForm(null);

        if (manga == null) {
            return;
        }

        mangaRepository.create(manga);

        loadMangas();
        clearForm();

        messageLabel.setText("Manga added.");
    }

    @FXML
    private void handleUpdate() {
        Manga selectedManga = mangaTableView.getSelectionModel().getSelectedItem();

        if (selectedManga == null) {
            messageLabel.setText("Select a manga first.");
            return;
        }

        Manga updatedManga = buildMangaFromForm(selectedManga.getId());

        if (updatedManga == null) {
            return;
        }

        mangaRepository.update(updatedManga);

        loadMangas();
        mangaTableView.getSelectionModel().clearSelection();
        clearForm();

        messageLabel.setText("Manga updated.");
    }

    @FXML
    private void handleDelete() {
        Manga selectedManga = mangaTableView.getSelectionModel().getSelectedItem();

        if (selectedManga == null) {
            messageLabel.setText("Select a manga first.");
            return;
        }

        mangaRepository.delete(selectedManga.getId());

        loadMangas();
        mangaTableView.getSelectionModel().clearSelection();
        clearForm();

        messageLabel.setText("Manga deleted.");
    }

    @FXML
    private void handleSearch() {
        MangaSearchCriteria criteria = new MangaSearchCriteria();

        criteria.setTitle(searchTitleTextField.getText());

        if (searchStatusComboBox.getValue() != null) {
            criteria.setStatus(searchStatusComboBox.getValue());
        }

        if (searchPublisherComboBox.getValue() != null) {
            criteria.setPublisherId(searchPublisherComboBox.getValue().getId());
        }

        mangaItems.setAll(mangaRepository.search(criteria));
        mangaTableView.refresh();

        messageLabel.setText("Search completed.");
    }

    @FXML
    private void handleRefresh() {
        searchTitleTextField.clear();

        loadMangas();

        resetComboBox(searchStatusComboBox, "Status");
        resetComboBox(searchPublisherComboBox, "Publisher");

        messageLabel.setText("");
    }

    private void resetComboBox(ComboBox comboBox, String promptText) {
        comboBox.setValue(null);
        comboBox.getSelectionModel().clearSelection();
        comboBox.setPromptText(promptText);

        comboBox.setButtonCell(new ListCell() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(promptText);
                } else {
                    setText(item.toString());
                }
            }
        });
    }

    @FXML
    private void handleClear() {
        mangaTableView.getSelectionModel().clearSelection();
        clearForm();

        resetComboBox(statusComboBox, "Status");
        resetComboBox(publisherComboBox, "Publisher");
        resetComboBox(authorComboBox, "Author");
        resetComboBox(genreComboBox, "Genre");
        resetComboBox(characterComboBox, "Character");

        messageLabel.setText("");
    }

    @FXML
    private void handleAddAuthor() {
        Author author = authorComboBox.getValue();

        if (author == null) {
            messageLabel.setText("Select an author first.");
            return;
        }

        addIfNotPresent(selectedAuthors, author);
    }

    @FXML
    private void handleRemoveAuthor() {
        Author author = selectedAuthorsListView.getSelectionModel().getSelectedItem();

        if (author != null) {
            selectedAuthors.remove(author);
        }
    }

    @FXML
    private void handleAddGenre() {
        Genre genre = genreComboBox.getValue();

        if (genre == null) {
            messageLabel.setText("Select a genre first.");
            return;
        }

        addIfNotPresent(selectedGenres, genre);
    }

    @FXML
    private void handleRemoveGenre() {
        Genre genre = selectedGenresListView.getSelectionModel().getSelectedItem();

        if (genre != null) {
            selectedGenres.remove(genre);
        }
    }

    @FXML
    private void handleAddCharacter() {
        StoryCharacter character = characterComboBox.getValue();

        if (character == null) {
            messageLabel.setText("Select a character first.");
            return;
        }

        addIfNotPresent(selectedCharacters, character);
    }

    @FXML
    private void handleRemoveCharacter() {
        StoryCharacter character = selectedCharactersListView.getSelectionModel().getSelectedItem();

        if (character != null) {
            selectedCharacters.remove(character);
        }
    }

    private Manga buildMangaFromForm(Long id) {
        String title = titleTextField.getText();
        String description = descriptionTextArea.getText();
        String imagePath = imagePathTextField.getText();

        if (title == null || title.isBlank()) {
            messageLabel.setText("Title is required.");
            return null;
        }

        Integer releaseYear = parseInteger(releaseYearTextField.getText(), "Release year");
        if (releaseYear == null) {
            return null;
        }

        Integer volumes = parseInteger(volumesTextField.getText(), "Volumes");
        if (volumes == null) {
            return null;
        }

        MangaStatus status = statusComboBox.getValue();

        if (status == null) {
            messageLabel.setText("Status is required.");
            return null;
        }

        Publisher publisher = publisherComboBox.getValue();

        if (publisher == null) {
            messageLabel.setText("Publisher is required.");
            return null;
        }

        description = description == null || description.isBlank()
                ? null
                : description.trim();

        imagePath = imagePath == null || imagePath.isBlank()
                ? null
                : imagePath.trim();

        Set<StoryCharacter> characters = new HashSet<>(selectedCharacters);
        Set<Genre> genres = new HashSet<>(selectedGenres);
        Set<Author> authors = new HashSet<>(selectedAuthors);

        if (id == null) {
            return new Manga(
                    title.trim(),
                    description,
                    releaseYear,
                    volumes,
                    publisher,
                    imagePath,
                    status,
                    characters,
                    genres,
                    authors
            );
        }

        return new Manga(
                id,
                title.trim(),
                description,
                releaseYear,
                volumes,
                publisher,
                imagePath,
                status,
                characters,
                genres,
                authors
        );
    }

    private Integer parseInteger(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            messageLabel.setText(fieldName + " is required.");
            return null;
        }

        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            messageLabel.setText(fieldName + " must be a number.");
            return null;
        }
    }

    private void loadMangas() {
        mangaItems.setAll(mangaRepository.findAll());
        mangaTableView.refresh();
    }

    private void fillForm(Manga manga) {
        titleTextField.setText(nullSafe(manga.getTitle()));
        descriptionTextArea.setText(nullSafe(manga.getDescription()));
        releaseYearTextField.setText(String.valueOf(manga.getReleaseYear()));
        volumesTextField.setText(String.valueOf(manga.getVolumes()));
        imagePathTextField.setText(nullSafe(manga.getImagePath()));

        statusComboBox.setValue(manga.getStatus());
        publisherComboBox.setValue(manga.getPublisher());

        selectedAuthors.setAll(
                manga.getAuthors() != null
                        ? manga.getAuthors()
                        : Set.of()
        );

        selectedGenres.setAll(
                manga.getGenres() != null
                        ? manga.getGenres()
                        : Set.of()
        );

        selectedCharacters.setAll(
                manga.getCharacters() != null
                        ? manga.getCharacters()
                        : Set.of()
        );
    }

    private void clearForm() {
        titleTextField.clear();
        descriptionTextArea.clear();
        releaseYearTextField.clear();
        volumesTextField.clear();
        imagePathTextField.clear();

        statusComboBox.getSelectionModel().clearSelection();
        publisherComboBox.getSelectionModel().clearSelection();

        authorComboBox.getSelectionModel().clearSelection();
        genreComboBox.getSelectionModel().clearSelection();
        characterComboBox.getSelectionModel().clearSelection();

        selectedAuthors.clear();
        selectedGenres.clear();
        selectedCharacters.clear();
    }

    private <T extends BaseEntity> void addIfNotPresent(ObservableList<T> list, T item) {
        boolean exists = list.stream()
                .anyMatch(existing -> existing.getId().equals(item.getId()));

        if (!exists) {
            list.add(item);
        }
    }

    private String formatAuthors(Set<Author> authors) {
        if (authors == null || authors.isEmpty()) {
            return "";
        }

        return authors.stream()
                .map(Author::getFullName)
                .sorted()
                .collect(Collectors.joining(", "));
    }

    private String formatGenres(Set<Genre> genres) {
        if (genres == null || genres.isEmpty()) {
            return "";
        }

        return genres.stream()
                .map(Genre::getName)
                .sorted()
                .collect(Collectors.joining(", "));
    }

    private String formatCharacters(Set<StoryCharacter> characters) {
        if (characters == null || characters.isEmpty()) {
            return "";
        }

        return characters.stream()
                .map(StoryCharacter::getFullName)
                .sorted()
                .collect(Collectors.joining(", "));
    }

    private String nullSafe(String value) {
        return value != null ? value : "";
    }
}