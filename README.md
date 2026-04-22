# LibConnect — Library Management System

> A Java & JavaFX desktop application for managing books, users, and library transactions across three role-based dashboards.

---

## Table of Contents

- [Project Overview](#project-overview)
- [Tech Stack](#tech-stack)
- [System Requirements](#system-requirements)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
  - [Running the App](#running-the-app)
  - [Running Tests](#running-tests)
- [Project Structure](#project-structure)
- [Architecture Overview](#architecture-overview)
- [Class Breakdown](#class-breakdown)
  - [Model Layer](#model-layer)
  - [Utility Layer](#utility-layer)
  - [GUI Layer](#gui-layer)
  - [FXML — Screen to Controller Wiring](#fxml--screen-to-controller-wiring)
- [OOP Concepts Applied](#oop-concepts-applied)
- [Unit Testing](#unit-testing)
- [Default Login Credentials](#default-login-credentials)
- [Seed Data](#seed-data)
- [User Roles & Permissions](#user-roles--permissions)
- [System Workflow](#system-workflow)
- [Git Workflow](#git-workflow)
- [Roadmap](#roadmap)

---

## Project Overview

**LibConnect** is a Java-based Library Management System built with JavaFX for its graphical user interface, designed to digitize and streamline the day-to-day operations of a library environment. The system supports three distinct user roles — Librarian, Member, and Guest — each with a tailored dashboard and a specific set of permissions, ensuring that functionality is both secure and intuitive.

Built on core Object-Oriented Programming principles including inheritance, encapsulation, polymorphism, and abstraction, LibConnect organizes its codebase into well-defined layers: a model layer for entities, a utility layer housing centralized data storage, and a GUI layer powered by JavaFX FXML controllers.

---

## Tech Stack

| Technology | Purpose |
|---|---|
| **Java 21 (LTS)** | Core programming language |
| **JavaFX 21** | GUI framework for desktop interface |
| **FXML** | Declarative UI layout files |
| **Maven** | Build tool & dependency management |
| **JUnit 5** | Unit tests (Jupiter; `junit-jupiter` / `maven-surefire-plugin`) |
| **Cursor IDE** | Development environment |
| **Git** | Version control |

---

## System Requirements

| Requirement | Minimum Version |
|---|---|
| Java (JDK) | 21 LTS |
| Maven | 3.8+ |
| macOS | Ventura 13+ (or any OS with JDK 21) |
| Cursor IDE | Latest |

---

## Getting Started

### Prerequisites

Install the following tools on macOS using Homebrew:

**1. Install Homebrew**
```bash
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
```

**2. Install Java 21**
```bash
brew install openjdk@21
sudo ln -sfn /opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk-21.jdk
echo 'export JAVA_HOME=/opt/homebrew/opt/openjdk@21' >> ~/.zshrc
echo 'export PATH="$JAVA_HOME/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc
java -version
```

**3. Install Maven**
```bash
brew install maven
mvn -version
```

**4. Install Git**
```bash
brew install git
git --version
```

**5. Install Cursor IDE**
```bash
brew install --cask cursor
```
Once Cursor is open, install Java extensions via `Cmd + Shift + X`:
- Extension Pack for Java → `vscjava.vscode-java-pack`
- Language Support for Java → `redhat.java`

---

### Installation

```bash
# Clone the repository
git clone https://github.com/your-username/LibConnect.git

# Navigate into the project
cd LibConnect

# Download dependencies and compile
mvn clean compile
```

### Running the App

```bash
mvn javafx:run
```

### Running Tests

```bash
mvn test
```

This runs the JUnit 5 suite (Surefire) for model and `DataStore` code. The JavaFX UI is not covered by these tests. See [Unit Testing](#unit-testing) for a list of test classes and scope.

---

## Project Structure

```
LibConnect/
├── pom.xml                          ← Maven build & dependency config
├── .gitignore
├── README.md
└── src/
    ├── main/
    │   ├── java/
    │   │   ├── module-info.java      ← Java module descriptor
    │   │   └── com/libconnect/
    │   │       ├── Main.java          ← Application entry point
    │   │       │
    │   │       ├── model/             ← Entity & role classes
    │   │       │   ├── User.java          (Abstract base class)
    │   │       │   ├── Librarian.java     (extends User)
    │   │       │   ├── Member.java        (extends User)
    │   │       │   ├── Guest.java         (extends User)
    │   │       │   └── Book.java          (Book entity)
    │   │       │
    │   │       ├── gui/               ← JavaFX controllers
    │   │       │   ├── BaseController.java
    │   │       │   ├── GUIManager.java
    │   │       │   ├── LoginController.java
    │   │       │   ├── LibrarianDashboardController.java
    │   │       │   ├── MemberDashboardController.java
    │   │       │   └── GuestDashboardController.java
    │   │       │
    │   │       ├── util/              ← Shared utilities
    │   │       │   └── DataStore.java     (Singleton data storage)
    │   │       │
    │   │       └── data/              ← Reserved for future persistence
    │   │
    │   └── resources/
    │       ├── fxml/                  ← UI layout files
    │       │   ├── Login.fxml
    │       │   ├── LibrarianDashboard.fxml
    │       │   ├── MemberDashboard.fxml
    │       │   └── GuestDashboard.fxml
    │       ├── css/
    │       │   └── styles.css         ← Global stylesheet
    │       └── images/                ← App assets & icons
    │
    └── test/
        └── java/
            ├── com/libconnect/
            │   ├── model/             ← JUnit 5: User, Book, Librarian, Member, Guest
            │   │   ├── UserTest.java
            │   │   ├── BookTest.java
            │   │   ├── LibrarianTest.java
            │   │   ├── MemberTest.java
            │   │   └── GuestTest.java
            │   └── util/
            │       └── DataStoreTest.java
```

---

## Architecture Overview

```
┌─────────────────────────────────────────────────┐
│                   GUI Layer                      │
│  LoginController  │  LibrarianDashboard          │
│  MemberDashboard  │  GuestDashboard              │
│  BaseController — GUIManager calls initData()   │
│    to inject the authenticated User + GUIManager │
│           GUIManager (screen router)             │
└────────────────────┬────────────────────────────┘
                     │ interacts with
┌────────────────────▼────────────────────────────┐
│                 Model Layer                      │
│   User (abstract)                               │
│     ├── Librarian   addBook / removeBook         │
│     ├── Member      borrowBook / returnBook      │
│     └── Guest       viewBookDetails              │
│   Book              displayDetails / updateCopies│
└────────────────────┬────────────────────────────┘
                     │ reads & writes to
┌────────────────────▼────────────────────────────┐
│              Utility Layer                       │
│   DataStore (Singleton)                          │
│     ├── List<Book>   — book catalog              │
│     └── List<User>   — all registered users      │
└─────────────────────────────────────────────────┘
```

---

## Class Breakdown

### Model Layer

#### `User.java` — Abstract Base Class
**Package:** `com.libconnect.model`

The foundation for all user roles. Defines shared attributes and enforces role-specific dashboard implementation via an abstract method.

| Attribute | Type | Description |
|---|---|---|
| `userId` | String | Unique user identifier |
| `username` | String | Display name |
| `email` | String | Login email |
| `password` | String | Login password |

| Method | Description |
|---|---|
| `login(email, password)` | Validates credentials, returns boolean |
| `viewDashboard()` | Abstract — each subclass implements its own |

---

#### `Book.java` — Book Entity
**Package:** `com.libconnect.model`

Represents a single book in the library catalog.

| Attribute | Type | Description |
|---|---|---|
| `bookId` | String | Unique book identifier |
| `title` | String | Book title |
| `author` | String | Author name |
| `genre` | String | Genre/category |
| `copiesAvailable` | int | Current number of copies |

| Method | Description |
|---|---|
| `displayBookDetails()` | Prints all book info to console |
| `updateCopies(int change)` | Pass `-1` to borrow, `+1` to return |
| `isAvailable()` | Returns true if copies > 0 |

---

#### `Librarian.java` — Extends User
**Package:** `com.libconnect.model`

Has full administrative privileges over books and members.

| Privilege | Description |
|---|---|
| `addBook(...)` | Creates a new Book and adds it to DataStore |
| `removeBook(bookId)` | Deletes a book from DataStore by ID |
| `manageMembers(memberId, action)` | Supports `"ban"` and `"update"` actions |

---

#### `Member.java` — Extends User
**Package:** `com.libconnect.model`

Registered library members who can interact with the book catalog.

| Method | Description |
|---|---|
| `borrowBook(title)` | Borrows a book, decreases available copies |
| `returnBook(title)` | Returns a book, restores available copies |
| `viewBookDetails(title)` | Displays details of any book |

Maintains a personal `borrowedBooks` list (ArrayList of titles).

---

#### `Guest.java` — Extends User
**Package:** `com.libconnect.model`

Read-only access, no account required. Auto-initialized with a default guest identity.

| Method | Description |
|---|---|
| `viewBookDetails(title)` | Displays book details — read only |

---

### Utility Layer

#### `DataStore.java` — Singleton
**Package:** `com.libconnect.util`

The single source of truth for all application data. Implemented using the **Singleton design pattern** to ensure one shared instance exists throughout the application's lifecycle.

| Method | Description |
|---|---|
| `getInstance()` | Returns the one shared DataStore instance |
| `addBook(book)` | Adds book to catalog |
| `removeBook(bookId)` | Removes book by ID, returns boolean |
| `getBookByTitle(title)` | Case-insensitive title lookup |
| `getBookById(id)` | Exact ID lookup |
| `searchBooks(keyword)` | Searches title + author fields |
| `getBooksByGenre(genre)` | Filters by genre |
| `getAllBooks()` | Returns full book list |
| `addUser(user)` | Registers a new user |
| `removeUser(userId)` | Deletes user by ID |
| `getUserById(id)` | Finds user by ID |
| `getUserByEmail(email)` | Finds user by email |
| `authenticate(email, password)` | Login validation, returns User or null |
| `getAllUsers()` | Returns full user list |
| `getAllMembers()` | Filters and returns only Member instances |
| `seedData()` | Pre-loads default users and books on startup |
| `printAllData()` | Debug utility — dumps all records to console |

---

### GUI Layer

> All four FXML views are complete and mapped to the controllers below. Key `fx:id` bindings are listed in **FXML — Screen to Controller Wiring**; the global `styles.css` theme applies to every screen. [Unit tests](#unit-testing) cover the model and `DataStore` layers; JavaFX UI automation is not included.

| File | Responsibility |
|---|---|
| `GUIManager.java` | Central screen and scene manager. Configures the primary `Stage`, loads FXML via a private `loadScene()` method, injects the authenticated `User` into every controller by calling `BaseController.initData(User, GUIManager)` after each load, applies the global CSS stylesheet, routes users to the correct dashboard in `showDashboard(User)`, provides shared alert dialogs (`showSuccess`, `showWarning`, `showError`), displays book details with `displayBookDetails(Book)`, and handles `logout()` back to the login screen. |
| `BaseController.java` | Abstract base class for all FXML controllers. Holds shared references to `currentUser` and `guiManager`. Defines the `initData(User, GUIManager)` lifecycle method invoked by `GUIManager` after every scene load, the abstract `onInit()` for subclasses, and a shared `handleLogout()` for navigation. |
| `LoginController.java` | Login screen. `handleLogin()` validates email, authenticates with `DataStore.authenticate()`, and calls `guiManager.showDashboard(user)`; `handleGuestAccess()` opens the Guest dashboard without login; `handleReset()` clears the form. |
| `LibrarianDashboardController.java` | UI for book/member management |
| `MemberDashboardController.java` | UI for borrowing and returning books |
| `GuestDashboardController.java` | UI for browsing and searching books |

### FXML — Screen to Controller Wiring

Each FXML file sets `fx:controller` to its screen class. The table below lists primary `fx:id` fields injected into the controller.

| FXML File | Controller | Key `fx:id` fields |
|---|---|---|
| `Login.fxml` | `LoginController` | `emailField`, `passwordField`, `errorLabel` |
| `LibrarianDashboard.fxml` | `LibrarianDashboardController` | `bookTable`, `memberTable`, and all form fields |
| `MemberDashboard.fxml` | `MemberDashboardController` | `catalogTable`, `borrowedListView`, borrow/return fields |
| `GuestDashboard.fxml` | `GuestDashboardController` | `catalogTable`, `searchField`, `genreFilterCombo` |

---

## OOP Concepts Applied

| Concept | Where Applied |
|---|---|
| **Abstraction** | `User` is abstract — forces subclasses to implement `viewDashboard()` |
| **Inheritance** | `Librarian`, `Member`, `Guest` all extend `User` |
| **Polymorphism** | `viewDashboard()` behaves differently per role; `authenticate()` returns any User subtype |
| **Encapsulation** | All attributes are `private` with controlled getters/setters |
| **`this` keyword** | Used in all constructors to assign instance attributes |
| **`super` keyword** | Used in subclass constructors to call `User(...)` |
| **`final` keyword** | `librarianPrivileges` array is `final` in Librarian |
| **Singleton Pattern** | `DataStore` uses a static instance for centralized access |
| **Collections** | `ArrayList` used for books, users, and borrowed books list |
| **Stream API** | Used in DataStore for filtering and searching collections |

---

## Unit Testing

| Item | Description |
|---|---|
| **Framework** | JUnit 5 (Jupiter), `junit-jupiter` **5.12.x** (test scope) |
| **Runner** | `maven-surefire-plugin` with `useModulePath` disabled so tests run on the classpath alongside the modular `com.libconnect` app |
| **Command** | `mvn test` (see [Running Tests](#running-tests)) |
| **Scope** | Pure Java only — `User` / `Book` / role models, and `DataStore` (no TestFX, no headless scene tests) |
| **Isolation** | `DataStore.resetForTests()` clears the singleton so each test (or `BeforeEach` hook) can depend on a fresh `seedData()`; do **not** call it from application code |

### Test classes

| Test class | Package | What is exercised |
|---|---|---|
| `UserTest` | `com.libconnect.model` | Abstract `User` via a `TestUser` double: constructor, getters/setters, `login` (incl. null-safety), `toString` |
| `BookTest` | `com.libconnect.model` | Construction, `isAvailable`, `updateCopies` (incl. no negative stock), `toString`, `displayBookDetails` (stdout) |
| `LibrarianTest` | `com.libconnect.model` | `Librarian` + `DataStore`: privileges, `addBook` / `removeBook`, `manageMembers` (actions / stdout) |
| `MemberTest` | `com.libconnect.model` | `Member` + `DataStore`: `borrowBook`, `returnBook`, `viewBookDetails` |
| `GuestTest` | `com.libconnect.model` | Default guest identity, `login`, `viewBookDetails` |
| `DataStoreTest` | `com.libconnect.util` | Singleton / `resetForTests`, seeded counts, all book and user query/mutation methods, `authenticate`, `printAllData` |

---

## Default Login Credentials

These accounts are pre-loaded by `DataStore.seedData()` on every startup:

| Role | Email | Password |
|---|---|---|
| Librarian | `admin@libconnect.com` | `admin123` |
| Member | `alice@libconnect.com` | `alice123` |
| Member | `bob@libconnect.com` | `bob123` |
| Guest | *(no login required)* | — |

---

## Seed Data

The following books are pre-loaded into the catalog on startup:

| Book ID | Title | Author | Genre | Copies |
|---|---|---|---|---|
| BK-001 | Clean Code | Robert C. Martin | Technology | 5 |
| BK-002 | The Pragmatic Programmer | David Thomas | Technology | 3 |
| BK-003 | To Kill a Mockingbird | Harper Lee | Fiction | 4 |
| BK-004 | 1984 | George Orwell | Dystopian | 6 |
| BK-005 | The Great Gatsby | F. Scott Fitzgerald | Classic | 2 |
| BK-006 | Sapiens | Yuval Noah Harari | History | 7 |
| BK-007 | Atomic Habits | James Clear | Self-Help | 8 |
| BK-008 | The Alchemist | Paulo Coelho | Fiction | 5 |

---

## User Roles & Permissions

| Feature | Librarian | Member | Guest |
|---|---|---|---|
| Login | ✅ | ✅ | ❌ |
| View Dashboard | ✅ | ✅ | ✅ |
| View Book Details | ✅ | ✅ | ✅ |
| Search Books | ✅ | ✅ | ✅ |
| Borrow Book | ❌ | ✅ | ❌ |
| Return Book | ❌ | ✅ | ❌ |
| Add Book | ✅ | ❌ | ❌ |
| Remove Book | ✅ | ❌ | ❌ |
| Manage Members | ✅ | ❌ | ❌ |

---

## System Workflow

```
App Start
    │
    ▼
Login Screen (GUIManager)
    │
    ├──► Librarian Login ──► Librarian Dashboard
    │         └── Add Book / Remove Book / Manage Members
    │
    ├──► Member Login ──► Member Dashboard
    │         └── Borrow Book / Return Book / View Details
    │
    └──► Guest (no login) ──► Guest Dashboard
              └── Search Books / View Book Details
```

---

## Git Workflow

```bash
# Check current status
git status

# Stage changes
git add .

# Commit with a meaningful message
git commit -m "feat: add DataStore singleton with seed data"

# Push to remote
git push origin main
```

**Suggested commit message prefixes:**

| Prefix | Use For |
|---|---|
| `feat:` | New feature or class |
| `fix:` | Bug fix |
| `chore:` | Setup, config, tooling |
| `docs:` | README or documentation update |
| `style:` | UI/CSS changes |
| `refactor:` | Code restructuring |

---

## Roadmap

- [x] Project scaffold & Maven setup
- [x] Git initialization & `.gitignore`
- [x] `User.java` — Abstract base class
- [x] `Book.java` — Book entity
- [x] `Librarian.java` — Admin role
- [x] `Member.java` — Member role
- [x] `Guest.java` — Guest role
- [x] `DataStore.java` — Singleton centralized storage
- [x] `Main.java` — Application entry point
- [x] `GUIManager.java` — Screen manager
- [x] `BaseController.java` — Shared controller base
- [x] `LoginController.java` — Login screen controller
- [x] `Login.fxml` — Login screen UI
- [x] `LibrarianDashboard.fxml` — Librarian UI
- [x] `MemberDashboard.fxml` — Member UI
- [x] `GuestDashboard.fxml` — Guest UI
- [x] `styles.css` — Global styling
- [x] Full JavaFX controller wiring
- [x] Unit tests — JUnit 5 (`UserTest`, `BookTest`, `LibrarianTest`, `MemberTest`, `GuestTest`, `DataStoreTest`); `mvn test`

---

> **LibConnect** — Built with Java 21 & JavaFX | Academic Project
