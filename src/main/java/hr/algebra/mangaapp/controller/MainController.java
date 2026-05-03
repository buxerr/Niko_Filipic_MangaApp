package hr.algebra.mangaapp.controller;

import hr.algebra.mangaapp.model.User;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;

public class MainController {

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
        // TODO: later return to login.fxml
        Platform.exit();
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
        System.out.println("Genres clicked");
    }

    @FXML
    private void handleAuthors() {
        System.out.println("Authors clicked");
    }

    @FXML
    private void handlePublishers() {
        System.out.println("Publishers clicked");
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
}