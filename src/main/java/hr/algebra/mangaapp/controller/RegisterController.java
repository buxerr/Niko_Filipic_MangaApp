package hr.algebra.mangaapp.controller;

import hr.algebra.mangaapp.MangaApp;
import hr.algebra.mangaapp.exception.ViewLoadException;
import hr.algebra.mangaapp.model.User;
import hr.algebra.mangaapp.model.enums.UserRole;
import hr.algebra.mangaapp.repository.RepositoryFactory;
import hr.algebra.mangaapp.repository.UserRepository;
import hr.algebra.mangaapp.util.PasswordUtils;
import hr.algebra.mangaapp.util.ConfigUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Dimension2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class RegisterController {

    @FXML
    private VBox root;

    @FXML
    private TextField usernameTextField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField passwordRepeatField;

    @FXML
    private Label messageLabel;

    private final UserRepository userRepository = RepositoryFactory.getUserRepository();

    @FXML
    private void initialize() {
        usernameTextField.requestFocus();
    }

    public void handleRegister() {

        String username = usernameTextField.getText();
        String password = passwordField.getText();
        String passwordRepeat = passwordRepeatField.getText();

        if (username == null || username.isBlank()
                || password == null || password.isBlank()
                || passwordRepeat == null || passwordRepeat.isBlank()) {
            messageLabel.setText("Please fill all the fields.");
            return;
        }

        username = username.trim();

        if (username.length() < 3) {
            messageLabel.setText("Username must be at least 3 characters long.");
            return;
        }

        if (password.length() < 8) {
            messageLabel.setText("Password must be at least 8 characters long.");
            return;
        }

        if (!password.equals(passwordRepeat)) {
            messageLabel.setText("Passwords do not match.");
            return;
        }

        if (userRepository.usernameExists(username)) {
            messageLabel.setText("Username already exists.");
            return;
        }

        String passwordHash = PasswordUtils.hashPassword(password);

        Long userId = userRepository.create(
                new User(username, passwordHash, UserRole.USER)
        );

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("Created user could not be found."));

        openMainWindow(user);
    }

    private void openMainWindow(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/hr/algebra/mangaapp/view/main.fxml")
            );

            Dimension2D bigScreen = ConfigUtils.getBigScreen();
            Scene scene = new Scene(
                    loader.load(),
                    bigScreen.getWidth(),
                    bigScreen.getHeight()
            );

            MainController mainController = loader.getController();
            mainController.setCurrentUser(user);

            Stage stage = (Stage) usernameTextField.getScene().getWindow();
            stage.setTitle("MangaApp");
            stage.setScene(scene);
            stage.setResizable(true);
            stage.centerOnScreen();

        } catch (IOException e) {
            throw new ViewLoadException("Error while opening main window", e);
        }
    }

    public void handleOpenLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    MangaApp.class.getResource("/hr/algebra/mangaapp/view/login.fxml")
            );

            Dimension2D smallScreen = ConfigUtils.getSmallScreen();
            Scene scene = new Scene(
                    loader.load(),
                    smallScreen.getWidth(),
                    smallScreen.getHeight()
            );

            Stage stage = (Stage) root.getScene().getWindow();
            stage.setTitle("MangaApp - Login");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

        } catch (IOException e) {
            throw new ViewLoadException("Error while opening login view", e);
        }
    }
}
