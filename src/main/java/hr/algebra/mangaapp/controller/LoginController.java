package hr.algebra.mangaapp.controller;

import hr.algebra.mangaapp.MangaApp;
import hr.algebra.mangaapp.exception.ViewLoadException;
import hr.algebra.mangaapp.model.User;
import hr.algebra.mangaapp.repository.RepositoryFactory;
import hr.algebra.mangaapp.repository.UserRepository;
import hr.algebra.mangaapp.util.PasswordUtils;
import hr.algebra.mangaapp.util.XmlConfigUtils;
import hr.algebra.mangaapp.xml.ActionXmlLogService;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private final UserRepository userRepository = RepositoryFactory.getUserRepository();

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

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
            ActionXmlLogService.log(username, null, "LOGIN_FAILED", "Missing username or password");
            return;
        }

        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isEmpty()) {
            messageLabel.setText("Invalid username or password.");
            log.warn("Failed login attempt for username: {}", username);
            ActionXmlLogService.log(username, null, "LOGIN_FAILED", "Unknown username");
            return;
        }

        User user = userOptional.get();

        log.info("Login attempt for username: {}", username);

        if (!PasswordUtils.matches(password, user.getPasswordHash())) {
            messageLabel.setText("Invalid username or password.");
            log.warn("Failed login attempt for username: {}", username);
            ActionXmlLogService.log(user, "LOGIN_FAILED", "Invalid password");
            return;
        }

        log.info("User logged in successfully: {}, role: {}", user.getUsername(), user.getRole());
        ActionXmlLogService.log(user, "LOGIN_SUCCESS", "User logged in");
        openMainWindow(user);
    }

    private void openMainWindow(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/hr/algebra/mangaapp/view/main.fxml")
            );

            Dimension2D bigScreen = XmlConfigUtils.getBigScreen();
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

        } catch (Exception e) {
            log.error("Unexpected error during login", e);
            throw new ViewLoadException("Error while opening main window", e);
        }
    }

    public void handleOpenRegister(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    MangaApp.class.getResource("/hr/algebra/mangaapp/view/register.fxml")
            );

            Dimension2D smallScreen = XmlConfigUtils.getSmallScreen();
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

        } catch (Exception e) {
            log.error("Unexpected error during login", e);
            throw new ViewLoadException("Error while opening main window", e);
        }
    }
}
