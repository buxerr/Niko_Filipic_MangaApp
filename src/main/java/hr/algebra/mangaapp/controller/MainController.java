package hr.algebra.mangaapp.controller;

import hr.algebra.mangaapp.exception.ViewLoadException;
import hr.algebra.mangaapp.model.Author;
import hr.algebra.mangaapp.model.Manga;
import hr.algebra.mangaapp.model.User;
import hr.algebra.mangaapp.repository.AdminRepository;
import hr.algebra.mangaapp.repository.AuthorRepository;
import hr.algebra.mangaapp.repository.MangaRepository;
import hr.algebra.mangaapp.repository.RepositoryFactory;
import hr.algebra.mangaapp.service.BackgroundTaskService;
import hr.algebra.mangaapp.service.CoverImageService;
import hr.algebra.mangaapp.service.StatisticsService;
import hr.algebra.mangaapp.util.XmlConfigUtils;
import hr.algebra.mangaapp.xml.MangaXmlExportService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Dimension2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

public class MainController {

    @FXML
    public BorderPane root;

    @FXML
    private StackPane contentPane;

    @FXML
    private Label welcomeLabel;

    @FXML
    private MenuBar menuBar;

    @FXML
    private Menu adminMenu;

    @FXML
    public Menu manageMenu;

    private User currentUser;

    private AdminRepository adminRepository = RepositoryFactory.getAdminRepository();

    private final MangaRepository mangaRepository = RepositoryFactory.getMangaRepository();

    private final AuthorRepository authorRepository =
            RepositoryFactory.getAuthorRepository();

    private final MangaXmlExportService mangaXmlExportService =
            new MangaXmlExportService();

    private final StatisticsService statisticsService = new StatisticsService();

    private final BackgroundTaskService backgroundTaskService = new BackgroundTaskService();

    private final CoverImageService coverImageService = new CoverImageService();

    private static final Logger log = LoggerFactory.getLogger(MainController.class);

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;

        log.info("Main window opened for user: {}, role: {}",
                currentUser.getUsername(),
                currentUser.getRole());

        boolean isAdmin = currentUser.isAdmin();

        if (!isAdmin) {
            menuBar.getMenus().remove(adminMenu);
        } else {
            if (!menuBar.getMenus().contains(manageMenu)) {
                menuBar.getMenus().add(manageMenu);
            }

            if (!menuBar.getMenus().contains(adminMenu)) {
                menuBar.getMenus().add(adminMenu);
            }
        }

        loadView("/hr/algebra/mangaapp/view/home.fxml");
    }

    @FXML
    private void handleStatistics() {
        List<Manga> mangas = mangaRepository.findAll();

        String report = statisticsService.buildReport(mangas);

        TextArea textArea = new TextArea(report);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefWidth(650);
        textArea.setPrefHeight(450);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("MangaApp Statistics");
        alert.setHeaderText("Catalog statistics");
        alert.getDialogPane().setContent(textArea);
        alert.showAndWait();
    }

    @FXML
    private void handleLogout() {
        if (currentUser != null) {
            log.info("User logged out: {}", currentUser.getUsername());
        } else {
            log.warn("Logout requested but currentUser is null");
        }
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/hr/algebra/mangaapp/view/login.fxml")
            );

            Dimension2D smallScreen = XmlConfigUtils.getSmallScreen();
            Scene scene = new Scene(
                    loader.load(),
                    smallScreen.getWidth(),
                    smallScreen.getHeight()
            );
            Stage stage = (Stage) root.getScene().getWindow();
            stage.setTitle("MangaApp - Login");
            stage.setScene(scene);
            stage.setResizable(false);
        } catch (Exception e) {
            throw new ViewLoadException("Error while loading login view", e);
        }

    }

    @FXML
    private void handleExit() {
        log.info("Application exit requested by user: {}",
                currentUser != null ? currentUser.getUsername() : "unknown");

        Platform.exit();
    }

    @FXML
    private void handleHome() {
        log.info("User opened Home view: {}", currentUser.getUsername());
        loadView("/hr/algebra/mangaapp/view/home.fxml");
    }

    @FXML
    private void handleManga() {
        log.info("User opened Manga management view: {}", currentUser.getUsername());
        loadView("/hr/algebra/mangaapp/view/manga.fxml");
    }

    @FXML
    private void handleGenres() {
        log.info("User opened Genre management view: {}", currentUser.getUsername());
        loadView("/hr/algebra/mangaapp/view/genre.fxml");
    }

    @FXML
    private void handleAuthors() {
        log.info("User opened Author management view: {}", currentUser.getUsername());
        loadView("/hr/algebra/mangaapp/view/author.fxml");
    }

    @FXML
    private void handlePublishers() {
        log.info("User opened Publisher management view: {}", currentUser.getUsername());
        loadView("/hr/algebra/mangaapp/view/publisher.fxml");
    }

    @FXML
    private void handleCharacters() {
        log.info("User opened Character management view: {}", currentUser.getUsername());
        loadView("/hr/algebra/mangaapp/view/story-character.fxml");
    }

    @FXML
    private void handleClearData() {
        if (currentUser == null || !currentUser.isAdmin()) {
            showError("Only administrators can clear data.");
            return;
        }

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Clear all data");
        confirmationAlert.setHeaderText("Are you sure you want to clear all application data?");
        confirmationAlert.setContentText(
                "This will delete all manga, genres, authors, publishers, characters and users. " +
                        "Referenced cover images will be deleted from assets/covers. " +
                        "The admin account will be recreated."
        );

        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                List<String> coverImagePaths = mangaRepository.findAll().stream()
                        .map(Manga::getImagePath)
                        .toList();

                adminRepository.clearAllData();

                long deletedCoverCount = coverImagePaths.stream()
                        .distinct()
                        .filter(coverImageService::deleteCoverIfExists)
                        .count();

                log.info("Clear data deleted cover files: {}", deletedCoverCount);

                contentPane.getChildren().clear();
                welcomeLabel.setText("All data cleared. Admin account was recreated.");
                contentPane.getChildren().setAll(welcomeLabel);

                showInfo("All data was cleared successfully.");
            }
        });
    }

    @FXML
    private void handleExportXml() {
        List<Author> authors = authorRepository.findAll();

        if (authors.isEmpty()) {
            showError("No authors available for XML export.");
            return;
        }

        ChoiceDialog<Author> authorDialog = new ChoiceDialog<>(authors.get(0), authors);
        authorDialog.setTitle("Export XML");
        authorDialog.setHeaderText("Export manga catalog by author");
        authorDialog.setContentText("Choose author:");

        authorDialog.showAndWait().ifPresent(selectedAuthor -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save XML catalog");

            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("XML files", "*.xml")
            );

            fileChooser.setInitialFileName(
                    selectedAuthor.getFullName()
                            .replaceAll("\\s+", "_")
                            .toLowerCase()
                            + "_catalog.xml"
            );

            File destinationFile = fileChooser.showSaveDialog(
                    contentPane.getScene().getWindow()
            );

            if (destinationFile == null) {
                return;
            }

            runXmlExportTask(selectedAuthor, destinationFile);
        });
    }

    private void runXmlExportTask(Author selectedAuthor, File destinationFile) {
        backgroundTaskService.run(
                "Export XML",
                "Exporting XML catalog",
                "Creating XML catalog...",
                "Cancelling XML export...",
                "manga-xml-export-task",
                isCancelled -> {
                    if (!isCancelled.getAsBoolean()) {
                        mangaXmlExportService.exportCatalogByAuthor(
                                selectedAuthor,
                                destinationFile
                        );
                    }

                    return null;
                },
                ignored -> {
                    log.info(
                            "XML catalog exported for author: {}, file={}",
                            selectedAuthor.getFullName(),
                            destinationFile.getAbsolutePath()
                    );

                    showInfo("XML catalog exported successfully.");
                },
                exception -> {
                    log.error(
                            "Failed to export XML catalog for author: {}",
                            selectedAuthor.getFullName(),
                            exception
                    );

                    showError("Failed to export XML catalog.");
                },
                () -> log.info("XML export cancelled for author: {}", selectedAuthor.getFullName())
        );
    }

    private void loadView(String fxmlPath) {
        try {
            log.debug("Loading view: {}", fxmlPath);

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            contentPane.getChildren().setAll(view);

            log.debug("View loaded successfully: {}", fxmlPath);

        } catch (Exception e) {
            log.error("Failed to load view: {}", fxmlPath, e);
            throw new ViewLoadException("Error while loading view: " + fxmlPath, e);
        }
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
