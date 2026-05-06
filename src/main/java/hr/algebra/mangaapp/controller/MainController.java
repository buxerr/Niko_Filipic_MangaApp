package hr.algebra.mangaapp.controller;

import hr.algebra.mangaapp.exception.ViewLoadException;
import hr.algebra.mangaapp.model.Genre;
import hr.algebra.mangaapp.model.Manga;
import hr.algebra.mangaapp.model.User;
import hr.algebra.mangaapp.model.enums.MangaStatus;
import hr.algebra.mangaapp.repository.AdminRepository;
import hr.algebra.mangaapp.repository.MangaRepository;
import hr.algebra.mangaapp.repository.RepositoryFactory;
import hr.algebra.mangaapp.repository.sql.SqlAdminRepository;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    private static final Logger log = LoggerFactory.getLogger(MainController.class);

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;

        log.info("Main window opened for user: {}, role: {}",
                currentUser.getUsername(),
                currentUser.getRole());

        boolean isAdmin = currentUser.isAdmin();

        if (!isAdmin) {
            menuBar.getMenus().remove(manageMenu);
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

        String report = buildStatisticsReport(mangas);

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

    private String buildStatisticsReport(List<Manga> mangas) {
        if (mangas == null || mangas.isEmpty()) {
            return "No manga/comics available in the catalog.";
        }

        long totalManga = mangas.size();

        double averageVolumes = mangas.stream()
                .mapToInt(Manga::getVolumes)
                .average()
                .orElse(0);

        Manga oldestManga = mangas.stream()
                .min(Comparator.comparingInt(Manga::getReleaseYear))
                .orElse(null);

        Map<MangaStatus, Long> mangaByStatus = mangas.stream()
                .filter(manga -> manga.getStatus() != null)
                .collect(Collectors.groupingBy(
                        Manga::getStatus,
                        Collectors.counting()
                ));

        Map<String, Long> mangaByPublisher = mangas.stream()
                .filter(manga -> manga.getPublisher() != null)
                .collect(Collectors.groupingBy(
                        manga -> manga.getPublisher().getName(),
                        Collectors.counting()
                ));

        Map<String, Long> genreUsage = mangas.stream()
                .filter(manga -> manga.getGenres() != null)
                .flatMap(manga -> manga.getGenres().stream())
                .collect(Collectors.groupingBy(
                        Genre::getName,
                        Collectors.counting()
                ));

        StringBuilder sb = new StringBuilder();

        sb.append("GENERAL\n");
        sb.append("Total manga/comics: ").append(totalManga).append("\n");
        sb.append("Average number of volumes: ")
                .append(String.format("%.2f", averageVolumes))
                .append("\n");

        if (oldestManga != null) {
            sb.append("Oldest title: ")
                    .append(oldestManga.getTitle())
                    .append(" (")
                    .append(oldestManga.getReleaseYear())
                    .append(")\n");
        }

        sb.append("\nMANGA BY STATUS\n");
        mangaByStatus.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry ->
                        sb.append(entry.getKey())
                                .append(": ")
                                .append(entry.getValue())
                                .append("\n")
                );

        sb.append("\nMANGA BY PUBLISHER\n");
        mangaByPublisher.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .forEach(entry ->
                        sb.append(entry.getKey())
                                .append(": ")
                                .append(entry.getValue())
                                .append("\n")
                );

        sb.append("\nGENRE USAGE\n");
        genreUsage.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .forEach(entry ->
                        sb.append(entry.getKey())
                                .append(": ")
                                .append(entry.getValue())
                                .append("\n")
                );

        return sb.toString();
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

            Scene scene = new Scene(loader.load(), 400, 300);
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
                        "The admin account will be recreated."
        );

        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                adminRepository.clearAllData();

                contentPane.getChildren().clear();
                welcomeLabel.setText("All data cleared. Admin account was recreated.");
                contentPane.getChildren().setAll(welcomeLabel);

                showInfo("All data was cleared successfully.");
            }
        });
    }

    @FXML
    private void handleImportData() {
        System.out.println("Import data clicked");
    }

    @FXML
    private void handleBackupXml() {
        System.out.println("Backup XML clicked");
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