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
import hr.algebra.mangaapp.service.CoverImageService;
import hr.algebra.mangaapp.service.MangaDisplayService;
import hr.algebra.mangaapp.service.MangaFormService;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

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
    private ImageView coverImageView;

    @FXML
    private Label messageLabel;

    @FXML
    private ListView<StoryCharacter> availableCharactersListView;

    private static final Logger log = LoggerFactory.getLogger(MangaController.class);

    private final MangaRepository mangaRepository = RepositoryFactory.getMangaRepository();
    private final PublisherRepository publisherRepository = RepositoryFactory.getPublisherRepository();
    private final AuthorRepository authorRepository = RepositoryFactory.getAuthorRepository();
    private final GenreRepository genreRepository = RepositoryFactory.getGenreRepository();
    private final StoryCharacterRepository characterRepository = RepositoryFactory.getStoryCharacterRepository();

    private final CoverImageService coverImageService = new CoverImageService();
    private final MangaDisplayService mangaDisplayService = new MangaDisplayService();
    private final MangaFormService mangaFormService = new MangaFormService();

    private final ObservableList<Manga> mangaItems = FXCollections.observableArrayList();
    private final ObservableList<Author> selectedAuthors = FXCollections.observableArrayList();
    private final ObservableList<Genre> selectedGenres = FXCollections.observableArrayList();
    private final ObservableList<StoryCharacter> selectedCharacters = FXCollections.observableArrayList();
    private final ObservableList<StoryCharacter> availableCharacters = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        setupTableColumns();
        setupComboBoxes();
        setupListViews();
        setupSelectionListener();

        imagePathTextField.textProperty().addListener((observable, oldValue, newValue) ->
                showCoverPreview(newValue)
        );

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
                new SimpleStringProperty(mangaDisplayService.formatStatus(cellData.getValue().getStatus())));

        publisherColumn.setCellValueFactory(cellData -> {
            Publisher publisher = cellData.getValue().getPublisher();

            return new SimpleStringProperty(
                    publisher != null ? publisher.getName() : ""
            );
        });

        authorsColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(mangaDisplayService.formatAuthors(cellData.getValue().getAuthors()))
        );

        genresColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(mangaDisplayService.formatGenres(cellData.getValue().getGenres()))
        );

        charactersColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(mangaDisplayService.formatCharacters(cellData.getValue().getCharacters()))
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

        ObservableList<StoryCharacter> characters =
                FXCollections.observableArrayList(characterRepository.findAll());

        characterComboBox.setItems(characters);
        availableCharacters.setAll(characters);
    }

    private void setupListViews() {
        selectedAuthorsListView.setItems(selectedAuthors);
        selectedGenresListView.setItems(selectedGenres);
        selectedCharactersListView.setItems(selectedCharacters);

        availableCharactersListView.setItems(availableCharacters);

        setupCharacterDragAndDrop();
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

        log.info("Manga added: {}", manga.getTitle());
        loadMangas();
        clearForm();

        messageLabel.setText("Manga added.");
    }

    private void showCoverPreview(String imagePath) {
        coverImageView.setImage(coverImageService.loadCover(imagePath, 140, 200));
    }

    @FXML
    private void handleUpdate() {
        Manga selectedManga = mangaTableView.getSelectionModel().getSelectedItem();

        if (selectedManga == null) {
            messageLabel.setText("Select a manga first.");
            return;
        }

        String oldImagePath = selectedManga.getImagePath();
        Manga updatedManga = buildMangaFromForm(selectedManga.getId());

        if (updatedManga == null) {
            return;
        }

        mangaRepository.update(updatedManga);
        deleteOldCoverIfImagePathChanged(oldImagePath, updatedManga.getImagePath());

        log.info("Manga updated: id={}, title={}", updatedManga.getId(), updatedManga.getTitle());
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
        deleteCoverIfNoLongerUsed(selectedManga.getImagePath());

        log.info("Manga deleted: id={}, title={}", selectedManga.getId(), selectedManga.getTitle());
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

        log.info(
                "Manga search completed: title={}, status={}, publisherId={}, results={}",
                criteria.getTitle(),
                criteria.getStatus(),
                criteria.getPublisherId(),
                mangaItems.size()
        );
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

    private void setupCharacterDragAndDrop() {
        availableCharactersListView.setOnDragDetected(event -> {
            StoryCharacter selectedCharacter =
                    availableCharactersListView.getSelectionModel().getSelectedItem();

            if (selectedCharacter == null || selectedCharacter.getId() == null) {
                return;
            }

            Dragboard dragboard = availableCharactersListView.startDragAndDrop(TransferMode.COPY);

            ClipboardContent content = new ClipboardContent();
            content.putString(String.valueOf(selectedCharacter.getId()));

            dragboard.setContent(content);

            event.consume();
        });

        selectedCharactersListView.setOnDragOver(event -> {
            if (event.getGestureSource() != selectedCharactersListView
                    && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.COPY);
            }

            event.consume();
        });

        selectedCharactersListView.setOnDragDropped(event -> {
            Dragboard dragboard = event.getDragboard();

            boolean success = false;

            if (dragboard.hasString()) {
                try {
                    Long characterId = Long.parseLong(dragboard.getString());

                    availableCharacters.stream()
                            .filter(character -> character.getId().equals(characterId))
                            .findFirst()
                            .ifPresent(character -> {
                                addIfNotPresent(selectedCharacters, character);
                                messageLabel.setText("Character added by drag and drop.");
                            });

                    success = true;

                } catch (NumberFormatException e) {
                    messageLabel.setText("Invalid character drag data.");
                }
            }

            event.setDropCompleted(success);
            event.consume();
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
        MangaFormService.MangaFormResult result = mangaFormService.buildManga(
                id,
                titleTextField.getText(),
                descriptionTextArea.getText(),
                releaseYearTextField.getText(),
                volumesTextField.getText(),
                imagePathTextField.getText(),
                statusComboBox.getValue(),
                publisherComboBox.getValue(),
                selectedCharacters,
                selectedGenres,
                selectedAuthors
        );

        if (!result.isSuccessful()) {
            messageLabel.setText(result.errorMessage());
            return null;
        }

        return result.manga();
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
        showCoverPreview(manga.getImagePath());

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
        coverImageView.setImage(null);

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

    private String nullSafe(String value) {
        return value != null ? value : "";
    }

    private void deleteOldCoverIfImagePathChanged(String oldImagePath, String newImagePath) {
        if (!sameImagePath(oldImagePath, newImagePath)) {
            deleteCoverIfNoLongerUsed(oldImagePath);
        }
    }

    private void deleteCoverIfNoLongerUsed(String imagePath) {
        if (imagePath == null || imagePath.isBlank()) {
            return;
        }

        boolean imageStillInUse = mangaRepository.findAll().stream()
                .map(Manga::getImagePath)
                .anyMatch(existingPath -> sameImagePath(existingPath, imagePath));

        if (!imageStillInUse) {
            coverImageService.deleteCoverIfExists(imagePath);
        }
    }

    private boolean sameImagePath(String firstPath, String secondPath) {
        return normalizeImagePath(firstPath).equalsIgnoreCase(normalizeImagePath(secondPath));
    }

    private String normalizeImagePath(String imagePath) {
        return imagePath == null ? "" : imagePath.trim().replace('\\', '/');
    }
}
