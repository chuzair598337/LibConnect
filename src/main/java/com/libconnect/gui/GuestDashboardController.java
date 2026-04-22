package com.libconnect.gui;

import com.libconnect.model.Book;
import com.libconnect.model.Guest;
import com.libconnect.util.DataStore;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class GuestDashboardController extends BaseController {

    // ── Welcome ───────────────────────────────────────────────────
    @FXML
    private Label welcomeLabel;
    @FXML
    private Label totalBooksLabel;

    // ── Book Catalog Table ────────────────────────────────────────
    @FXML
    private TableView<Book> catalogTable;
    @FXML
    private TableColumn<Book, String> colBookId;
    @FXML
    private TableColumn<Book, String> colTitle;
    @FXML
    private TableColumn<Book, String> colAuthor;
    @FXML
    private TableColumn<Book, String> colGenre;
    @FXML
    private TableColumn<Book, Integer> colCopies;

    // ── Search & Filter ───────────────────────────────────────────
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> genreFilterCombo;

    // ── Internal State ────────────────────────────────────────────
    private Guest guest;
    private final ObservableList<Book> catalogData = FXCollections.observableArrayList();

    // ── BaseController Hook ───────────────────────────────────────
    @Override
    protected void onInit() {
        guest = (Guest) currentUser;
        welcomeLabel.setText("Browsing as Guest — Login for full access.");

        setupCatalogTable();
        setupGenreFilter();
        refreshCatalog();
        refreshStats();
    }

    // ════════════════════════════════════════════════════════════
    // TABLE & FILTER SETUP
    // ════════════════════════════════════════════════════════════

    private void setupCatalogTable() {
        colBookId.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        colGenre.setCellValueFactory(new PropertyValueFactory<>("genre"));
        colCopies.setCellValueFactory(new PropertyValueFactory<>("copiesAvailable"));
        catalogTable.setItems(catalogData);
        catalogTable.setPlaceholder(new Label("No books available in the catalog."));
    }

    /**
     * Populates the genre filter dropdown from the live book catalog.
     */
    private void setupGenreFilter() {
        ObservableList<String> genres = FXCollections.observableArrayList();
        genres.add("All Genres");

        DataStore.getInstance().getAllBooks().stream()
                .map(Book::getGenre)
                .distinct()
                .sorted()
                .forEach(genres::add);

        genreFilterCombo.setItems(genres);
        genreFilterCombo.getSelectionModel().selectFirst();
    }

    // ════════════════════════════════════════════════════════════
    // SEARCH & FILTER ACTIONS
    // ════════════════════════════════════════════════════════════

    /**
     * Filters the catalog by title or author keyword.
     */
    @FXML
    private void handleSearchBooks() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            refreshCatalog();
            return;
        }

        List<Book> results = DataStore.getInstance().searchBooks(keyword);
        catalogData.setAll(results);

        if (results.isEmpty()) {
            guiManager.showWarning("No Results",
                    "No books found matching: \"" + keyword + "\"");
        }
    }

    /**
     * Clears the search field and reloads all books.
     */
    @FXML
    private void handleClearSearch() {
        searchField.clear();
        genreFilterCombo.getSelectionModel().selectFirst();
        refreshCatalog();
    }

    /**
     * Filters the catalog by the selected genre from the dropdown.
     */
    @FXML
    private void handleGenreFilter() {
        String selected = genreFilterCombo.getValue();
        if (selected == null || selected.equals("All Genres")) {
            refreshCatalog();
            return;
        }
        List<Book> results = DataStore.getInstance().getBooksByGenre(selected);
        catalogData.setAll(results);
    }

    /**
     * Opens a detail dialog for the selected book in the catalog.
     */
    @FXML
    private void handleViewBookDetails() {
        Book selected = catalogTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            guiManager.showWarning("No Selection",
                    "Please select a book from the catalog to view its details.");
            return;
        }
        guest.viewBookDetails(selected.getTitle());
        guiManager.displayBookDetails(selected);
    }

    // ════════════════════════════════════════════════════════════
    // NAVIGATION
    // ════════════════════════════════════════════════════════════

    /**
     * Returns to the Login screen so the guest can log in as a member.
     */
    @FXML
    private void handleLoginRedirect() {
        guiManager.showLoginScreen();
    }

    // ════════════════════════════════════════════════════════════
    // PRIVATE REFRESH HELPERS
    // ════════════════════════════════════════════════════════════

    private void refreshCatalog() {
        catalogData.setAll(DataStore.getInstance().getAllBooks());
    }

    private void refreshStats() {
        totalBooksLabel.setText(
                "Books Available: " + DataStore.getInstance().getAllBooks().size());
    }
}