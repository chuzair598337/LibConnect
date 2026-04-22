package com.libconnect.gui;

import com.libconnect.model.User;

/**
 * BaseController defines a shared contract for all FXML controllers.
 * GUIManager calls initData() on every controller after loading a scene,
 * passing in the authenticated User and the GUIManager reference.
 */
public abstract class BaseController {

    // ── Shared References ─────────────────────────────────────────
    protected User currentUser;
    protected GUIManager guiManager;

    // ── Lifecycle Hook ────────────────────────────────────────────

    /**
     * Called by GUIManager immediately after the FXML scene is loaded.
     * Subclasses must call super.initData() then run their own setup.
     *
     * @param user       The currently authenticated user (null for Guest)
     * @param guiManager The shared GUIManager instance
     */
    public void initData(User user, GUIManager guiManager) {
        this.currentUser = user;
        this.guiManager = guiManager;
        onInit(); // trigger subclass-specific setup
    }

    /**
     * Override in each controller to run setup after user is injected.
     * Example: populate tables, set welcome labels, load book lists.
     */
    protected abstract void onInit();

    // ── Shared Action ─────────────────────────────────────────────

    /**
     * Logs out the current user and returns to the Login screen.
     */
    protected void handleLogout() {
        guiManager.logout();
    }
}