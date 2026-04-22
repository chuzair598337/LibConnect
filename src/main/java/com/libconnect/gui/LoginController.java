package com.libconnect.gui;

import com.libconnect.model.Guest;
import com.libconnect.model.User;
import com.libconnect.util.DataStore;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController extends BaseController {

    // ── FXML Bindings ────────────────────────────────────────────
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label errorLabel;
    @FXML
    private Button loginButton;
    @FXML
    private Button guestButton;

    // ── Lifecycle ─────────────────────────────────────────────────
    @Override
    protected void onInit() {
        // Clear any previous error messages on screen load
        errorLabel.setText("");
    }

    // ════════════════════════════════════════════════════════════
    // FXML ACTIONS
    // ════════════════════════════════════════════════════════════

    /**
     * Triggered when the Login button is clicked.
     * Validates input, authenticates against DataStore,
     * then routes to the correct dashboard.
     */
    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        // ── Input Validation ─────────────────────────────────────
        if (email.isEmpty() || password.isEmpty()) {
            showError("Please enter both email and password.");
            return;
        }

        // ── Authentication ───────────────────────────────────────
        User user = DataStore.getInstance().authenticate(email, password);

        if (user == null) {
            showError("Invalid email or password. Please try again.");
            passwordField.clear();
            return;
        }

        // ── Route to Role Dashboard ──────────────────────────────
        clearError();
        guiManager.showDashboard(user);
    }

    /**
     * Triggered when Continue as Guest is clicked.
     * Skips authentication and loads the Guest dashboard.
     */
    @FXML
    private void handleGuestAccess() {
        guiManager.showDashboard(new Guest());
    }

    // ════════════════════════════════════════════════════════════
    // HELPERS
    // ════════════════════════════════════════════════════════════

    private void showError(String message) {
        errorLabel.setStyle("-fx-text-fill: #e74c3c;");
        errorLabel.setText(message);
    }

    private void clearError() {
        errorLabel.setText("");
    }
}