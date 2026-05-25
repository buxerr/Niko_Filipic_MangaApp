package hr.algebra.mangaapp.controller;

import hr.algebra.mangaapp.MangaApp;
import hr.algebra.mangaapp.exception.ViewLoadException;
import hr.algebra.mangaapp.model.User;
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
import java.util.Optional;

public class LoginController {

    @FXML
    private VBox root;

    @FXML
    private TextField usernameTextField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    private final UserRepository userRepository = RepositoryFactory.getUserRepository();

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

        if (!PasswordUtils.matches(password, user.getPasswordHash())) {
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

            Dimension2D bigScreen = ConfigUtils.getBigScreen();
            Scene scene = new Scene(
                    loader.load(),
                    bigScreen.getWidth(),
                    bigScreen.getHeight()
            );

            MainController mainController = loader.getController();
            mainController.setCurrentUser(user);

            Stage stage = (Stage) root.getScene().getWindow();
            stage.setTitle("MangaApp");
            stage.setScene(scene);
            stage.setResizable(true);
            stage.centerOnScreen();

        } catch (IOException e) {
            throw new ViewLoadException("Error while opening main window", e);
        }
    }

    public void handleOpenRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    MangaApp.class.getResource("/hr/algebra/mangaapp/view/register.fxml")
            );

            Dimension2D smallScreen = ConfigUtils.getSmallScreen();
            Scene scene = new Scene(
                    loader.load(),
                    smallScreen.getWidth(),
                    smallScreen.getHeight()
            );

            Stage stage = (Stage) root.getScene().getWindow();
            stage.setTitle("MangaApp - Register");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

        } catch (IOException e) {
            throw new ViewLoadException("Error while opening register view", e);
        }
    }
}
