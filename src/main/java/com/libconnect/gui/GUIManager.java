package com.libconnect.gui;

import com.libconnect.model.Book;
import com.libconnect.model.Guest;
import com.libconnect.model.Librarian;
import com.libconnect.model.Member;
import com.libconnect.model.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import java.io.IOException;

public class GUIManager {

    // ── Constants ────────────────────────────────────────────────
    private static final String APP_TITLE = "LibConnect — Library Management System";
    private static final double MIN_WIDTH = 900;
    private static final double MIN_HEIGHT = 600;

    // ── Core References ──────────────────────────────────────────
    private final Stage primaryStage;
    private static GUIManager instance;

    // ── Constructor ──────────────────────────────────────────────
    public GUIManager(Stage primaryStage) {
        this.primaryStage = primaryStage;
        instance = this;
        configurePrimaryStage();
    }

    // ── Singleton Access ─────────────────────────────────────────
    public static GUIManager getInstance() {
        return instance;
    }

    // ════════════════════════════════════════════════════════════
    // STAGE CONFIGURATION
    // ════════════════════════════════════════════════════════════

    /**
     * Sets up the main application window properties.
     */
    private void configurePrimaryStage() {
        primaryStage.setTitle(APP_TITLE);
        primaryStage.setMinWidth(MIN_WIDTH);
        primaryStage.setMinHeight(MIN_HEIGHT);
        primaryStage.setResizable(true);
        primaryStage.centerOnScreen();
    }

    // ════════════════════════════════════════════════════════════
    // SCREEN NAVIGATION
    // ════════════════════════════════════════════════════════════

    /**
     * Displays the Login screen.
     * Entry point for all users.
     */
    public void showLoginScreen() {
        loadScene("/fxml/Login.fxml", "Login — LibConnect", MIN_WIDTH, MIN_HEIGHT, null);
        primaryStage.show();
    }

    /**
     * Displays the appropriate dashboard based on the user's role.
     * Routes to Librarian, Member, or Guest dashboard automatically.
     */
    public void showDashboard(User user) {
        if (user instanceof Librarian) {
            showLibrarianDashboard((Librarian) user);
        } else if (user instanceof Member) {
            showMemberDashboard((Member) user);
        } else if (user instanceof Guest) {
            showGuestDashboard();
        } else {
            showError("Unknown Role", "Cannot determine user role. Please contact support.");
        }
    }

    /**
     * Loads and displays the Librarian dashboard.
     */
    public void showLibrarianDashboard(Librarian librarian) {
        loadScene(
                "/fxml/LibrarianDashboard.fxml",
                "Librarian Dashboard — LibConnect",
                MIN_WIDTH, MIN_HEIGHT,
                librarian);
    }

    /**
     * Loads and displays the Member dashboard.
     */
    public void showMemberDashboard(Member member) {
        loadScene(
                "/fxml/MemberDashboard.fxml",
                "Member Dashboard — LibConnect",
                MIN_WIDTH, MIN_HEIGHT,
                member);
    }

    /**
     * Loads and displays the Guest dashboard.
     */
    public void showGuestDashboard() {
        loadScene(
                "/fxml/GuestDashboard.fxml",
                "Guest Dashboard — LibConnect",
                MIN_WIDTH, MIN_HEIGHT,
                new Guest());
    }

    /**
     * Navigates back to the Login screen.
     * Called on logout from any dashboard.
     */
    public void logout() {
        showLoginScreen();
    }

    // ════════════════════════════════════════════════════════════
    // BOOK DETAIL DIALOG
    // ════════════════════════════════════════════════════════════

    /**
     * Displays book details in a formatted info dialog.
     */
    public void displayBookDetails(Book book) {
        if (book == null) {
            showError("Book Not Found", "The requested book could not be found.");
            return;
        }

        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Book Details — LibConnect");
        alert.setHeaderText(book.getTitle());
        alert.setContentText(
                "Book ID   :  " + book.getBookId() + "\n" +
                        "Author    :  " + book.getAuthor() + "\n" +
                        "Genre     :  " + book.getGenre() + "\n" +
                        "Available :  " + book.getCopiesAvailable() + " copies");
        alert.showAndWait();
    }

    // ════════════════════════════════════════════════════════════
    // ALERT HELPERS
    // ════════════════════════════════════════════════════════════

    /**
     * Shows a success notification dialog.
     */
    public void showSuccess(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Shows a warning notification dialog.
     */
    public void showWarning(String title, String message) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Shows an error notification dialog.
     */
    public void showError(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ════════════════════════════════════════════════════════════
    // PRIVATE SCENE LOADER
    // ════════════════════════════════════════════════════════════

    /**
     * Core scene loader — loads an FXML file, injects the user into
     * its controller, applies the global stylesheet, and sets the scene.
     *
     * @param fxmlPath Path to the FXML file (relative to resources/)
     * @param title    Window title for this screen
     * @param width    Scene width
     * @param height   Scene height
     * @param user     The authenticated User object (null for login screen)
     */
    private void loadScene(String fxmlPath, String title,
            double width, double height, User user) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(fxmlPath));

            Parent root = loader.load();

            // ── Inject user into controller if it supports it ────
            Object controller = loader.getController();
            if (controller instanceof BaseController baseCtrl) {
                baseCtrl.initData(user, this);
            }

            // ── Apply global CSS stylesheet ──────────────────────
            Scene scene = new Scene(root, width, height);
            String css = getClass().getResource("/css/styles.css") != null
                    ? getClass().getResource("/css/styles.css").toExternalForm()
                    : null;
            if (css != null) {
                scene.getStylesheets().add(css);
            }

            // ── Set scene and update title ───────────────────────
            primaryStage.setTitle(title);
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();

        } catch (IOException e) {
            System.err.println("Failed to load FXML: " + fxmlPath);
            e.printStackTrace();
            showError("Navigation Error", "Could not load screen: " + fxmlPath);
        }
    }

    // ── Getters ──────────────────────────────────────────────────
    public Stage getPrimaryStage() {
        return primaryStage;
    }
}