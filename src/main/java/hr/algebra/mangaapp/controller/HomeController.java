package hr.algebra.mangaapp.controller;

import hr.algebra.mangaapp.model.Author;
import hr.algebra.mangaapp.model.Genre;
import hr.algebra.mangaapp.model.Manga;
import hr.algebra.mangaapp.model.Publisher;
import hr.algebra.mangaapp.model.enums.MangaStatus;
import hr.algebra.mangaapp.repository.AuthorRepository;
import hr.algebra.mangaapp.repository.GenreRepository;
import hr.algebra.mangaapp.repository.MangaRepository;
import hr.algebra.mangaapp.repository.PublisherRepository;
import hr.algebra.mangaapp.repository.RepositoryFactory;
import hr.algebra.mangaapp.repository.search.MangaSearchCriteria;
import hr.algebra.mangaapp.service.CoverImageService;
import hr.algebra.mangaapp.service.MangaDisplayService;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

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
    private ComboBox<Publisher> publisherComboBox;

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
    private Label detailDescriptionLabel;

    @FXML
    private VBox detailsBox;

    @FXML
    private Label messageLabel;

    @FXML
    private ImageView detailCoverImageView;

    private final MangaRepository mangaRepository = RepositoryFactory.getMangaRepository();
    private final GenreRepository genreRepository = RepositoryFactory.getGenreRepository();
    private final AuthorRepository authorRepository = RepositoryFactory.getAuthorRepository();
    private final PublisherRepository publisherRepository = RepositoryFactory.getPublisherRepository();

    private final CoverImageService coverImageService = new CoverImageService();
    private final MangaDisplayService mangaDisplayService = new MangaDisplayService();

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
        genreComboBox.setItems(
                FXCollections.observableArrayList(genreRepository.findAll())
        );

        authorComboBox.setItems(
                FXCollections.observableArrayList(authorRepository.findAll())
        );

        publisherComboBox.setItems(
                FXCollections.observableArrayList(publisherRepository.findAll())
        );

        statusComboBox.setItems(
                FXCollections.observableArrayList(MangaStatus.values())
        );

        resetComboBox(genreComboBox, "Genre");
        resetComboBox(authorComboBox, "Author");
        resetComboBox(publisherComboBox, "Publisher");
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

        Publisher selectedPublisher = publisherComboBox.getValue();

        if (selectedPublisher != null) {
            criteria.setPublisherId(selectedPublisher.getId());
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
    private void handleClearSelection() {
        clearDetails();
        messageLabel.setText("");
    }

    @FXML
    private void handleRefresh() {
        searchTitleTextField.clear();

        resetComboBox(genreComboBox, "Genre");
        resetComboBox(authorComboBox, "Author");
        resetComboBox(publisherComboBox, "Publisher");
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
        detailsBox.setVisible(true);
        detailsBox.setManaged(true);

        detailTitleLabel.setText("Title: " + nullSafe(manga.getTitle()));

        if (manga.getPublisher() != null && manga.getPublisher().getName() != null) {
            detailPublisherLabel.setText("Publisher: " + manga.getPublisher().getName());
        } else {
            detailPublisherLabel.setText("");
        }

        if (manga.getReleaseYear() > 0) {
            detailYearLabel.setText("Year: " + manga.getReleaseYear());
        } else {
            detailYearLabel.setText("");
        }

        if (manga.getVolumes() > 0) {
            detailVolumesLabel.setText("Volumes: " + manga.getVolumes());
        } else {
            detailVolumesLabel.setText("");
        }

        if (manga.getStatus() != null) {
            detailStatusLabel.setText("Status: " + mangaDisplayService.formatStatus(manga.getStatus()));
        } else {
            detailStatusLabel.setText("");
        }

        String authors = mangaDisplayService.formatAuthors(manga.getAuthors());
        detailAuthorsLabel.setText(authors.isBlank() ? "" : "Authors: " + authors);

        String genres = mangaDisplayService.formatGenres(manga.getGenres());
        detailGenresLabel.setText(genres.isBlank() ? "" : "Genres: " + genres);

        String characters = mangaDisplayService.formatCharacters(manga.getCharacters());
        detailCharactersLabel.setText(characters.isBlank() ? "" : "Characters: " + characters);

        String description = nullSafe(manga.getDescription());
        detailDescriptionLabel.setText(description.isBlank() ? "" : "Synopsis: " + description);

        showCoverPreview(manga.getImagePath());
    }

    private void showCoverPreview(String imagePath) {
        detailCoverImageView.setImage(coverImageService.loadCover(imagePath, 140, 200));
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
        detailDescriptionLabel.setText("");
        detailCoverImageView.setImage(null);

        detailsBox.setVisible(false);
        detailsBox.setManaged(false);

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

    private String nullSafe(String value) {
        return value != null ? value : "";
    }
}
