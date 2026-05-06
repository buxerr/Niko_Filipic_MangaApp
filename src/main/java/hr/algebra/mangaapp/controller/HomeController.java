package hr.algebra.mangaapp.controller;

import hr.algebra.mangaapp.model.Author;
import hr.algebra.mangaapp.model.Genre;
import hr.algebra.mangaapp.model.Manga;
import hr.algebra.mangaapp.model.Publisher;
import hr.algebra.mangaapp.model.StoryCharacter;
import hr.algebra.mangaapp.model.enums.MangaStatus;
import hr.algebra.mangaapp.repository.AuthorRepository;
import hr.algebra.mangaapp.repository.GenreRepository;
import hr.algebra.mangaapp.repository.MangaRepository;
import hr.algebra.mangaapp.repository.RepositoryFactory;
import hr.algebra.mangaapp.repository.search.MangaSearchCriteria;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.Set;
import java.util.stream.Collectors;

public class HomeController {

    @FXML
    private TableView<Manga> mangaTableView;

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
    private ComboBox<Genre> genreComboBox;

    @FXML
    private ComboBox<Author> authorComboBox;

    @FXML
    private ComboBox<MangaStatus> statusComboBox;

    @FXML
    private Label detailTitleLabel;

    @FXML
    private Label detailPublisherLabel;

    @FXML
    private Label detailYearLabel;

    @FXML
    private Label detailVolumesLabel;

    @FXML
    private Label detailStatusLabel;

    @FXML
    private Label detailAuthorsLabel;

    @FXML
    private Label detailGenresLabel;

    @FXML
    private Label detailCharactersLabel;

    @FXML
    private TextArea detailDescriptionTextArea;

    @FXML
    private Label messageLabel;

    private final MangaRepository mangaRepository = RepositoryFactory.getMangaRepository();
    private final GenreRepository genreRepository = RepositoryFactory.getGenreRepository();
    private final AuthorRepository authorRepository = RepositoryFactory.getAuthorRepository();

    private final ObservableList<Manga> mangaItems = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        setupTableColumns();
        setupComboBoxes();
        setupSelectionListener();

        mangaTableView.setItems(mangaItems);

        loadMangas();
    }

    private void setupTableColumns() {
        titleColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(nullSafe(cellData.getValue().getTitle())));

        releaseYearColumn.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getReleaseYear()));

        volumesColumn.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getVolumes()));

        statusColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(formatStatus(cellData.getValue().getStatus())));

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
        genreComboBox.setItems(
                FXCollections.observableArrayList(genreRepository.findAll())
        );

        authorComboBox.setItems(
                FXCollections.observableArrayList(authorRepository.findAll())
        );

        statusComboBox.setItems(
                FXCollections.observableArrayList(MangaStatus.values())
        );

        resetComboBox(genreComboBox, "Genre");
        resetComboBox(authorComboBox, "Author");
        resetComboBox(statusComboBox, "Status");
    }

    private void setupSelectionListener() {
        mangaTableView.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, selectedManga) -> {
                    if (selectedManga != null) {
                        fillDetails(selectedManga);
                    }
                });
    }

    @FXML
    private void handleSearch() {
        MangaSearchCriteria criteria = new MangaSearchCriteria();

        String title = searchTitleTextField.getText();

        if (title != null && !title.isBlank()) {
            criteria.setTitle(title.trim());
        }

        Genre selectedGenre = genreComboBox.getValue();

        if (selectedGenre != null) {
            criteria.setGenreId(selectedGenre.getId());
        }

        Author selectedAuthor = authorComboBox.getValue();

        if (selectedAuthor != null) {
            criteria.setAuthorId(selectedAuthor.getId());
        }

        MangaStatus selectedStatus = statusComboBox.getValue();

        if (selectedStatus != null) {
            criteria.setStatus(selectedStatus);
        }

        mangaItems.setAll(mangaRepository.search(criteria));
        mangaTableView.refresh();

        clearDetails();

        messageLabel.setText("Search completed.");
    }

    @FXML
    private void handleRefresh() {
        searchTitleTextField.clear();

        resetComboBox(genreComboBox, "Genre");
        resetComboBox(authorComboBox, "Author");
        resetComboBox(statusComboBox, "Status");

        loadMangas();
        clearDetails();

        messageLabel.setText("");
    }

    private void loadMangas() {
        mangaItems.setAll(mangaRepository.findAll());
        mangaTableView.refresh();
    }

    private void fillDetails(Manga manga) {
        detailTitleLabel.setText(nullSafe(manga.getTitle()));

        detailPublisherLabel.setText(
                manga.getPublisher() != null ? manga.getPublisher().getName() : ""
        );

        detailYearLabel.setText(String.valueOf(manga.getReleaseYear()));
        detailVolumesLabel.setText(String.valueOf(manga.getVolumes()));
        detailStatusLabel.setText(formatStatus(manga.getStatus()));

        detailAuthorsLabel.setText(formatAuthors(manga.getAuthors()));
        detailGenresLabel.setText(formatGenres(manga.getGenres()));
        detailCharactersLabel.setText(formatCharacters(manga.getCharacters()));

        detailDescriptionTextArea.setText(nullSafe(manga.getDescription()));
    }

    private void clearDetails() {
        detailTitleLabel.setText("");
        detailPublisherLabel.setText("");
        detailYearLabel.setText("");
        detailVolumesLabel.setText("");
        detailStatusLabel.setText("");
        detailAuthorsLabel.setText("");
        detailGenresLabel.setText("");
        detailCharactersLabel.setText("");
        detailDescriptionTextArea.clear();

        mangaTableView.getSelectionModel().clearSelection();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
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

    private String formatStatus(MangaStatus status) {
        if (status == null) {
            return "";
        }

        return switch (status) {
            case ONGOING -> "Ongoing";
            case COMPLETED -> "Completed";
            case HIATUS -> "Hiatus";
            case CANCELLED -> "Cancelled";
        };
    }

    private String nullSafe(String value) {
        return value != null ? value : "";
    }
}