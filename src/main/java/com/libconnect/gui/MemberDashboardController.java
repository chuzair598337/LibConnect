package com.libconnect.gui;

import com.libconnect.model.Book;
import com.libconnect.model.Member;
import com.libconnect.util.DataStore;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class MemberDashboardController extends BaseController {

    // ── Welcome ───────────────────────────────────────────────────
    @FXML
    private Label welcomeLabel;
    @FXML
    private Label borrowedCountLabel;

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

    // ── Borrowed Books List ───────────────────────────────────────
    @FXML
    private ListView<String> borrowedListView;

    // ── Borrow / Return Fields ────────────────────────────────────
    @FXML
    private TextField borrowTitleField;
    @FXML
    private TextField returnTitleField;

    // ── Search ────────────────────────────────────────────────────
    @FXML
    private TextField searchField;

    // ── Internal State ────────────────────────────────────────────
    private Member member;
    private final ObservableList<Book> catalogData = FXCollections.observableArrayList();
    private final ObservableList<String> borrowedData = FXCollections.observableArrayList();

    // ── BaseController Hook ───────────────────────────────────────
    @Override
    protected void onInit() {
        member = (Member) currentUser;
        welcomeLabel.setText("Welcome, " + member.getUsername() + "!");

        setupCatalogTable();
        setupBorrowedList();
        refreshCatalog();
        refreshBorrowedList();
    }

    // ════════════════════════════════════════════════════════════
    // TABLE & LIST SETUP
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

    private void setupBorrowedList() {
        borrowedListView.setItems(borrowedData);
        borrowedListView.setPlaceholder(new Label("You have no borrowed books."));
    }

    // ════════════════════════════════════════════════════════════
    // BORROW & RETURN ACTIONS
    // ════════════════════════════════════════════════════════════

    /**
     * Borrows the book matching the entered title.
     * Decreases available copies in the catalog.
     */
    @FXML
    private void handleBorrowBook() {
        String title = borrowTitleField.getText().trim();

        if (title.isEmpty()) {
            guiManager.showWarning("Missing Title",
                    "Please enter the title of the book you want to borrow.");
            return;
        }

        Book book = DataStore.getInstance().getBookByTitle(title);
        if (book == null) {
            guiManager.showWarning("Not Found",
                    "No book found with title: \"" + title + "\"");
            return;
        }

        if (!book.isAvailable()) {
            guiManager.showWarning("Unavailable",
                    "\"" + title + "\" has no available copies right now.");
            return;
        }

        if (member.getBorrowedBooks().contains(title)) {
            guiManager.showWarning("Already Borrowed",
                    "You have already borrowed \"" + title + "\".");
            return;
        }

        member.borrowBook(title);
        borrowTitleField.clear();
        refreshCatalog();
        refreshBorrowedList();
        guiManager.showSuccess("Book Borrowed",
                "You have successfully borrowed \"" + title + "\".");
    }

    /**
     * Returns the book matching the entered title back to the library.
     * Increases available copies in the catalog.
     */
    @FXML
    private void handleReturnBook() {
        String title = returnTitleField.getText().trim();

        if (title.isEmpty()) {
            guiManager.showWarning("Missing Title",
                    "Please enter the title of the book you want to return.");
            return;
        }

        if (!member.getBorrowedBooks().contains(title)) {
            guiManager.showWarning("Not Borrowed",
                    "You have not borrowed a book titled: \"" + title + "\"");
            return;
        }

        member.returnBook(title);
        returnTitleField.clear();
        refreshCatalog();
        refreshBorrowedList();
        guiManager.showSuccess("Book Returned",
                "You have successfully returned \"" + title + "\". Thank you!");
    }

    // ════════════════════════════════════════════════════════════
    // SEARCH & VIEW ACTIONS
    // ════════════════════════════════════════════════════════════

    /**
     * Filters catalog table by title or author keyword.
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
     * Clears the search field and reloads the full catalog.
     */
    @FXML
    private void handleClearSearch() {
        searchField.clear();
        refreshCatalog();
    }

    /**
     * Opens a detail dialog for the selected book in the catalog table.
     */
    @FXML
    private void handleViewBookDetails() {
        Book selected = catalogTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            guiManager.showWarning("No Selection",
                    "Please select a book from the catalog to view its details.");
            return;
        }
        guiManager.displayBookDetails(selected);
    }

    /**
     * Auto-fills the borrow field from the selected catalog row.
     */
    @FXML
    private void handleSelectForBorrow() {
        Book selected = catalogTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            borrowTitleField.setText(selected.getTitle());
        }
    }

    /**
     * Auto-fills the return field from the selected borrowed list item.
     */
    @FXML
    private void handleSelectForReturn() {
        String selected = borrowedListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            returnTitleField.setText(selected);
        }
    }

    // ════════════════════════════════════════════════════════════
    // NAVIGATION
    // ════════════════════════════════════════════════════════════

    /**
     * Logs out and returns to the Login screen.
     */
    @FXML
    private void handleLogoutButton() {
        handleLogout();
    }

    // ════════════════════════════════════════════════════════════
    // PRIVATE REFRESH HELPERS
    // ════════════════════════════════════════════════════════════

    private void refreshCatalog() {
        catalogData.setAll(DataStore.getInstance().getAllBooks());
    }

    private void refreshBorrowedList() {
        borrowedData.setAll(member.getBorrowedBooks());
        borrowedCountLabel.setText(
                "Borrowed Books: " + member.getBorrowedBooks().size());
    }
}