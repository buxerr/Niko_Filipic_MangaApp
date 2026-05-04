package hr.algebra.mangaapp.controller;

import hr.algebra.mangaapp.exception.ViewLoadException;
import hr.algebra.mangaapp.model.User;
import hr.algebra.mangaapp.repository.UserRepository;
import hr.algebra.mangaapp.repository.sql.SqlUserRepository;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Optional;

public class LoginController {

    @FXML
    public VBox root;

    @FXML
    private TextField usernameTextField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    private final UserRepository userRepository = new SqlUserRepository();

    @FXML
    private void initialize() {
        usernameTextField.requestFocus();
    }

    @FXML
    private void handleLogin() {
        String username = usernameTextField.getText();
        String password = passwordField.getText();

        if (username == null || username.isBlank()
                || password == null || password.isBlank()) {
            messageLabel.setText("Username and password are required.");
            return;
        }

        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isEmpty()) {
            messageLabel.setText("Invalid username or password.");
            return;
        }

        User user = userOptional.get();

        // For now passwordHash is plain text during development.
        if (!user.getPasswordHash().equals(password)) {
            messageLabel.setText("Invalid username or password.");
            return;
        }

        openMainWindow(user);
    }

    private void openMainWindow(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/hr/algebra/mangaapp/view/main.fxml")
            );

            Scene scene = new Scene(loader.load(), 900, 600);

            MainController mainController = loader.getController();
            mainController.setCurrentUser(user);

            Stage stage = (Stage) usernameTextField.getScene().getWindow();
            stage.setTitle("MangaApp");
            stage.setScene(scene);
            stage.setResizable(true);
            stage.centerOnScreen();

        } catch (Exception e) {
            throw new ViewLoadException("Error while opening main window", e);
        }
    }
}