package com.libconnect.gui;

import com.libconnect.model.Book;
import com.libconnect.model.Librarian;
import com.libconnect.model.Member;
import com.libconnect.model.User;
import com.libconnect.util.DataStore;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class LibrarianDashboardController extends BaseController {

    // ── Welcome ───────────────────────────────────────────────────
    @FXML
    private Label welcomeLabel;
    @FXML
    private Label totalBooksLabel;
    @FXML
    private Label totalMembersLabel;

    // ── Book Table ────────────────────────────────────────────────
    @FXML
    private TableView<Book> bookTable;
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

    // ── Add Book Form ─────────────────────────────────────────────
    @FXML
    private TextField addBookIdField;
    @FXML
    private TextField addTitleField;
    @FXML
    private TextField addAuthorField;
    @FXML
    private TextField addGenreField;
    @FXML
    private TextField addCopiesField;

    // ── Remove Book ───────────────────────────────────────────────
    @FXML
    private TextField removeBookIdField;

    // ── Member Table ──────────────────────────────────────────────
    @FXML
    private TableView<Member> memberTable;
    @FXML
    private TableColumn<Member, String> colMemberId;
    @FXML
    private TableColumn<Member, String> colMemberName;
    @FXML
    private TableColumn<Member, String> colMemberEmail;

    // ── Manage Member ─────────────────────────────────────────────
    @FXML
    private TextField manageMemberIdField;
    @FXML
    private ComboBox<String> manageActionCombo;

    // ── Search ────────────────────────────────────────────────────
    @FXML
    private TextField searchField;

    // ── Internal State ────────────────────────────────────────────
    private Librarian librarian;
    private final ObservableList<Book> bookData = FXCollections.observableArrayList();
    private final ObservableList<Member> memberData = FXCollections.observableArrayList();

    // ── BaseController Hook ───────────────────────────────────────
    @Override
    protected void onInit() {
        librarian = (Librarian) currentUser;
        welcomeLabel.setText("Welcome, " + librarian.getUsername() + "!");

        setupBookTable();
        setupMemberTable();
        setupManageCombo();
        refreshBookTable();
        refreshMemberTable();
        refreshStats();
    }

    // ════════════════════════════════════════════════════════════
    // TABLE SETUP
    // ════════════════════════════════════════════════════════════

    private void setupBookTable() {
        colBookId.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        colGenre.setCellValueFactory(new PropertyValueFactory<>("genre"));
        colCopies.setCellValueFactory(new PropertyValueFactory<>("copiesAvailable"));
        bookTable.setItems(bookData);
        bookTable.setPlaceholder(new Label("No books in catalog."));
    }

    private void setupMemberTable() {
        colMemberId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        colMemberName.setCellValueFactory(new PropertyValueFactory<>("username"));
        colMemberEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        memberTable.setItems(memberData);
        memberTable.setPlaceholder(new Label("No members registered."));
    }

    private void setupManageCombo() {
        manageActionCombo.setItems(FXCollections.observableArrayList("ban", "update"));
        manageActionCombo.getSelectionModel().selectFirst();
    }

    // ════════════════════════════════════════════════════════════
    // STATS REFRESH
    // ════════════════════════════════════════════════════════════

    /**
     * Updates the summary stat labels at the top of the dashboard.
     */
    private void refreshStats() {
        totalBooksLabel.setText(
                "Total Books: " + DataStore.getInstance().getAllBooks().size());
        totalMembersLabel.setText(
                "Total Members: " + DataStore.getInstance().getAllMembers().size());
    }

    // ════════════════════════════════════════════════════════════
    // BOOK ACTIONS
    // ════════════════════════════════════════════════════════════

    /**
     * Reads the Add Book form and adds a new book via the Librarian model.
     */
    @FXML
    private void handleAddBook() {
        String id = addBookIdField.getText().trim();
        String title = addTitleField.getText().trim();
        String author = addAuthorField.getText().trim();
        String genre = addGenreField.getText().trim();
        String copies = addCopiesField.getText().trim();

        if (id.isEmpty() || title.isEmpty() || author.isEmpty()
                || genre.isEmpty() || copies.isEmpty()) {
            guiManager.showWarning("Missing Fields", "Please fill in all book fields.");
            return;
        }

        int copiesInt;
        try {
            copiesInt = Integer.parseInt(copies);
            if (copiesInt < 1)
                throw new NumberFormatException();
        } catch (NumberFormatException e) {
            guiManager.showWarning("Invalid Copies", "Copies must be a positive whole number.");
            return;
        }

        if (DataStore.getInstance().getBookById(id) != null) {
            guiManager.showWarning("Duplicate ID",
                    "A book with ID \"" + id + "\" already exists.");
            return;
        }

        librarian.addBook(id, title, author, genre, copiesInt);
        refreshBookTable();
        refreshStats();
        clearAddBookForm();
        guiManager.showSuccess("Book Added",
                "\"" + title + "\" has been added to the catalog.");
    }

    /**
     * Removes a book from the catalog using its Book ID.
     */
    @FXML
    private void handleRemoveBook() {
        String id = removeBookIdField.getText().trim();

        if (id.isEmpty()) {
            guiManager.showWarning("Missing ID", "Please enter a Book ID to remove.");
            return;
        }

        Book book = DataStore.getInstance().getBookById(id);
        if (book == null) {
            guiManager.showWarning("Not Found", "No book found with ID: " + id);
            return;
        }

        librarian.removeBook(id);
        refreshBookTable();
        refreshStats();
        removeBookIdField.clear();
        guiManager.showSuccess("Book Removed",
                "\"" + book.getTitle() + "\" has been removed from the catalog.");
    }

    /**
     * Filters the book table by title or author keyword.
     */
    @FXML
    private void handleSearchBooks() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            refreshBookTable();
            return;
        }
        List<Book> results = DataStore.getInstance().searchBooks(keyword);
        bookData.setAll(results);

        if (results.isEmpty()) {
            guiManager.showWarning("No Results",
                    "No books found matching: \"" + keyword + "\"");
        }
    }

    /**
     * Clears the search field and reloads the full book list.
     */
    @FXML
    private void handleClearSearch() {
        searchField.clear();
        refreshBookTable();
    }

    /**
     * Opens a detail dialog for the selected book row.
     */
    @FXML
    private void handleViewBookDetails() {
        Book selected = bookTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            guiManager.showWarning("No Selection",
                    "Please select a book from the table first.");
            return;
        }
        guiManager.displayBookDetails(selected);
    }

    // ════════════════════════════════════════════════════════════
    // MEMBER ACTIONS
    // ════════════════════════════════════════════════════════════

    /**
     * Applies the selected action (ban / update) on a member by ID.
     */
    @FXML
    private void handleManageMember() {
        String memberId = manageMemberIdField.getText().trim();
        String action = manageActionCombo.getValue();

        if (memberId.isEmpty()) {
            guiManager.showWarning("Missing ID", "Please enter a Member ID.");
            return;
        }

        User member = DataStore.getInstance().getUserById(memberId);
        if (member == null) {
            guiManager.showWarning("Not Found",
                    "No member found with ID: " + memberId);
            return;
        }

        if (!(member instanceof Member)) {
            guiManager.showWarning("Invalid Target",
                    "The provided ID does not belong to a Member.");
            return;
        }

        librarian.manageMembers(memberId, action);
        manageMemberIdField.clear();
        guiManager.showSuccess("Action Applied",
                "Action \"" + action + "\" applied to: " + member.getUsername());
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

    private void refreshBookTable() {
        bookData.setAll(DataStore.getInstance().getAllBooks());
    }

    private void refreshMemberTable() {
        memberData.setAll(DataStore.getInstance().getAllMembers());
    }

    private void clearAddBookForm() {
        addBookIdField.clear();
        addTitleField.clear();
        addAuthorField.clear();
        addGenreField.clear();
        addCopiesField.clear();
    }
}