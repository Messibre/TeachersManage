package com.teachera.ui;

import com.teachera.model.User;
import com.teachera.service.AuthService;
import com.teachera.service.ServiceException;
import java.util.function.Consumer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Optional;

public class LoginView {

    private final AuthService authService;
    private final Consumer<User> onLoginSuccess;

    public LoginView(AuthService authService, Consumer<User> onLoginSuccess) {
        this.authService = authService;
        this.onLoginSuccess = onLoginSuccess;
    }

    public Node createContent() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(12);
        grid.setPadding(new Insets(16));
        grid.setAlignment(Pos.CENTER_LEFT);

        Label usernameLabel = new Label("Username");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter username");

        Label passwordLabel = new Label("Password");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");

        Button loginButton = new Button("Login");
        Button signUpButton = new Button("Sign up");

        Label messageLabel = new Label();
        messageLabel.getStyleClass().add("login-message");

        grid.add(usernameLabel, 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(passwordLabel, 0, 1);
        grid.add(passwordField, 1, 1);
        grid.add(loginButton, 1, 2);
        grid.add(signUpButton, 1, 4);
        grid.add(messageLabel, 1, 3);

        loginButton.setOnAction(e -> {
            messageLabel.setTextFill(Color.web("#b91c1c"));
            messageLabel.setText("");

            String username = usernameField.getText();
            String password = passwordField.getText();

            try {
                Optional<User> userOpt = authService.login(username, password);
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    messageLabel.setTextFill(Color.web("#059669"));
                    messageLabel.setText("Logged in as " + user.getUsername() + " (" + user.getRole() + ").");

                    try {
                        if (onLoginSuccess != null)
                            onLoginSuccess.accept(user);
                    } catch (RuntimeException ex) {
                        messageLabel.setText("Failed to open dashboard.");
                        ex.printStackTrace();
                    }
                } else {
                    messageLabel.setText("Invalid username or password.");
                }
            } catch (ServiceException ex) {
                messageLabel.setText(ex.getMessage());
            } catch (RuntimeException ex) {
                messageLabel.setText("Unexpected error during login.");
            }
        });

        signUpButton.setOnAction(e -> {
            try {
                Stage dialog = new Stage();
                SignupView signupView = new SignupView(authService, user -> {
                    // close dialog and reuse login success navigation
                    dialog.close();
                    if (onLoginSuccess != null)
                        onLoginSuccess.accept(user);
                }, () -> dialog.close());

                Parent root = (Parent) signupView.createContent();
                Scene scene = new Scene(root);
                dialog.setTitle("Sign up");
                dialog.setScene(scene);
                if (grid.getScene() != null) {
                    dialog.initOwner(grid.getScene().getWindow());
                }
                dialog.show();
            } catch (RuntimeException ex) {
                messageLabel.setText("Unable to open sign-up dialog.");
            }
        });

        return grid;
    }
}
