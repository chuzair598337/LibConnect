package com.libconnect.model;

public abstract class User {

    // ── Attributes ──────────────────────────────────────────────
    private String userId;
    private String username;
    private String email;
    private String password;

    // ── Constructor ──────────────────────────────────────────────
    public User(String userId, String username, String email, String password) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    // ── Core Methods ─────────────────────────────────────────────

    /**
     * Authenticates the user by comparing provided credentials.
     * Null inputs, or a user with unset email/password, always yield {@code false}.
     */
    public boolean login(String email, String password) {
        if (email == null || password == null) {
            return false;
        }
        if (this.email == null || this.password == null) {
            return false;
        }
        return this.email.equals(email) && this.password.equals(password);
    }

    /**
     * Each role implements its own dashboard view.
     */
    public abstract void viewDashboard();

    // ── Getters & Setters ────────────────────────────────────────
    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // ── toString ─────────────────────────────────────────────────
    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}