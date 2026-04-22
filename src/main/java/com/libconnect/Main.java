package com.libconnect;

import com.libconnect.gui.GUIManager;
import com.libconnect.util.DataStore;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    // ── JavaFX Entry Point ───────────────────────────────────────
    @Override
    public void start(Stage primaryStage) {
        // Initialize DataStore (seeds default users & books)
        DataStore.getInstance();

        // Hand off control to GUIManager
        GUIManager guiManager = new GUIManager(primaryStage);
        guiManager.showLoginScreen();
    }

    // ── Application Main ─────────────────────────────────────────
    public static void main(String[] args) {
        launch(args);
    }
}