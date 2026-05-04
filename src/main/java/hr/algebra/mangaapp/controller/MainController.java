package hr.algebra.mangaapp.controller;

import hr.algebra.mangaapp.exception.ViewLoadException;
import hr.algebra.mangaapp.model.User;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MainController {

    @FXML
    public BorderPane root;

    @FXML
    private StackPane contentPane;

    @FXML
    private Label welcomeLabel;

    @FXML
    private Menu adminMenu;

    private User currentUser;

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;

        welcomeLabel.setText("Welcome, " + currentUser.getUsername()
                + " (" + currentUser.getRole() + ")");

        adminMenu.setDisable(!currentUser.isAdmin());
    }

    @FXML
    private void handleLogout() {

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
        Platform.exit();
    }

    @FXML
    private void handleManga() {
        System.out.println("Manga clicked");
    }

    @FXML
    private void handleGenres() {
        loadView("/hr/algebra/mangaapp/view/genre.fxml");
    }

    @FXML
    private void handleAuthors() {
        loadView("/hr/algebra/mangaapp/view/author.fxml");
    }

    @FXML
    private void handlePublishers() {
        loadView("/hr/algebra/mangaapp/view/publisher.fxml");
    }

    @FXML
    private void handleCharacters() {
        System.out.println("Characters clicked");
    }

    @FXML
    private void handleClearData() {
        System.out.println("Clear data clicked");
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            contentPane.getChildren().setAll(view);
        } catch (Exception e) {
            throw new ViewLoadException("Error while loading view: " + fxmlPath, e);
        }
    }

}