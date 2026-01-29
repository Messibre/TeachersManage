package com.teachera.ui;

import com.teachera.model.User;
import com.teachera.service.AuthService;
import com.teachera.service.ServiceException;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import java.util.function.Consumer;

public class SignupView {

    private final AuthService authService;
    private final Consumer<User> onSignupSuccess;
    private final Runnable onCancel;

    public SignupView(AuthService authService, Consumer<User> onSignupSuccess, Runnable onCancel) {
        this.authService = authService;
        this.onSignupSuccess = onSignupSuccess;
        this.onCancel = onCancel;
    }

    public Node createContent() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(12);
        grid.setPadding(new Insets(16));
        grid.setAlignment(Pos.CENTER_LEFT);

        Label usernameLabel = new Label("Username");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Choose username");

        Label passwordLabel = new Label("Password");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");

        Label confirmLabel = new Label("Confirm");
        PasswordField confirmField = new PasswordField();
        confirmField.setPromptText("Confirm password");

        Button signupButton = new Button("Sign up");
        Button cancelButton = new Button("Cancel");

        Label messageLabel = new Label();
        messageLabel.getStyleClass().add("login-message");

        grid.add(usernameLabel, 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(passwordLabel, 0, 1);
        grid.add(passwordField, 1, 1);
        grid.add(confirmLabel, 0, 2);
        grid.add(confirmField, 1, 2);
        grid.add(signupButton, 1, 3);
        grid.add(cancelButton, 0, 3);
        grid.add(messageLabel, 1, 4);

        signupButton.setOnAction(e -> {
            messageLabel.setTextFill(Color.web("#b91c1c"));
            messageLabel.setText("");

            String username = usernameField.getText();
            String password = passwordField.getText();
            String confirm = confirmField.getText();

            if (!password.equals(confirm)) {
                messageLabel.setText("Passwords do not match.");
                return;
            }

            try {
                java.util.Optional<User> created = authService.register(username, password);
                if (created.isPresent()) {
                    messageLabel.setTextFill(Color.web("#059669"));
                    messageLabel.setText("Registration successful.");
                    if (onSignupSuccess != null)
                        onSignupSuccess.accept(created.get());
                } else {
                    messageLabel.setText("Registration failed.");
                }
            } catch (ServiceException ex) {
                messageLabel.setText(ex.getMessage());
            } catch (RuntimeException ex) {
                messageLabel.setText("Unexpected error during registration.");
            }
        });

        cancelButton.setOnAction(e -> {
            if (onCancel != null)
                onCancel.run();
        });

        return grid;
    }
}
