package com.comp2042.view;

import java.io.*;
import java.util.Properties;

/**
 * Manages global game settings with persistence to settings.config file.
 * <p>
 * This class handles loading and saving of game settings including:
 * - Ghost piece visibility
 * - Hard drop functionality
 * - Game theme
 * - Difficulty level
 * </p>
 * <p>
 * Settings are persisted to a settings.config file in the user's directory.
 * If the file doesn't exist, default values are used and a new file is created.
 * </p>
 *
 * @author TetrisJFX Team
 * @version 1.0
 */
public class GlobalSettings {

    // Default values
    private static final boolean DEFAULT_GHOST_PIECE_ENABLED = true;
    private static final boolean DEFAULT_HARD_DROP_ENABLED = true;
    private static final String DEFAULT_THEME = "classic"; // Always use classic theme
    private static final String DEFAULT_DIFFICULTY = "NORMAL";

    // Allowed values
    private static final String[] ALLOWED_THEMES = {"neon", "classic", "gameboy"};
    private static final String[] ALLOWED_DIFFICULTIES = {"EASY", "NORMAL", "HARD"};

    // Settings file path
    private static final String SETTINGS_FILE = "settings.config";

    // Settings fields
    private boolean ghostPieceEnabled = DEFAULT_GHOST_PIECE_ENABLED;
    private boolean hardDropEnabled = DEFAULT_HARD_DROP_ENABLED;
    private String theme = DEFAULT_THEME;
    private String difficulty = DEFAULT_DIFFICULTY;

    // Singleton instance
    private static GlobalSettings instance;

    /**
     * Private constructor for singleton pattern.
     */
    private GlobalSettings() {
        loadFromFile();
    }

    /**
     * Gets the singleton instance of GlobalSettings.
     *
     * @return the GlobalSettings instance
     */
    public static GlobalSettings getInstance() {
        if (instance == null) {
            instance = new GlobalSettings();
        }
        return instance;
    }

    /**
     * Loads settings from settings.config file.
     * <p>
     * If the file doesn't exist, uses default values and creates a new file.
     * </p>
     */
    private void loadFromFile() {
        File settingsFile = new File(SETTINGS_FILE);
        
        if (!settingsFile.exists()) {
            // File doesn't exist - use defaults and create file
            saveSettings();
            return;
        }

        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(settingsFile)) {
            props.load(fis);

            // Load ghost piece setting
            String ghostPieceStr = props.getProperty("ghostPieceEnabled", 
                    String.valueOf(DEFAULT_GHOST_PIECE_ENABLED));
            ghostPieceEnabled = Boolean.parseBoolean(ghostPieceStr);

            // Load hard drop setting
            String hardDropStr = props.getProperty("hardDropEnabled", 
                    String.valueOf(DEFAULT_HARD_DROP_ENABLED));
            hardDropEnabled = Boolean.parseBoolean(hardDropStr);

            // Load theme (always use classic, ignore saved theme)
            theme = DEFAULT_THEME; // Always classic theme

            // Load difficulty
            String difficultyValue = props.getProperty("difficulty", DEFAULT_DIFFICULTY);
            if (isValidDifficulty(difficultyValue)) {
                difficulty = difficultyValue;
            } else {
                difficulty = DEFAULT_DIFFICULTY;
            }

        } catch (IOException e) {
            System.err.println("Error loading settings: " + e.getMessage());
            // Use defaults on error
        }
    }

    /**
     * Saves current settings to settings.config file.
     * <p>
     * Creates the file if it doesn't exist.
     * </p>
     */
    public void saveSettings() {
        Properties props = new Properties();
        props.setProperty("ghostPieceEnabled", String.valueOf(ghostPieceEnabled));
        props.setProperty("hardDropEnabled", String.valueOf(hardDropEnabled));
        props.setProperty("theme", DEFAULT_THEME); // Always save classic theme
        props.setProperty("difficulty", difficulty);

        try (FileOutputStream fos = new FileOutputStream(SETTINGS_FILE)) {
            props.store(fos, "TetrisFX Game Settings");
        } catch (IOException e) {
            System.err.println("Error saving settings: " + e.getMessage());
        }
    }

    /**
     * Static method to load settings and return instance.
     * Convenience method for getting settings with auto-load.
     *
     * @return the GlobalSettings instance with loaded settings
     */
    public static GlobalSettings loadSettings() {
        return getInstance();
    }

    /**
     * Validates if a theme name is allowed.
     *
     * @param themeName the theme name to validate
     * @return true if theme is valid, false otherwise
     */
    private boolean isValidTheme(String themeName) {
        for (String allowed : ALLOWED_THEMES) {
            if (allowed.equalsIgnoreCase(themeName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Validates if a difficulty level is allowed.
     *
     * @param difficultyLevel the difficulty level to validate
     * @return true if difficulty is valid, false otherwise
     */
    private boolean isValidDifficulty(String difficultyLevel) {
        for (String allowed : ALLOWED_DIFFICULTIES) {
            if (allowed.equalsIgnoreCase(difficultyLevel)) {
                return true;
            }
        }
        return false;
    }

    // Getters
    public boolean isGhostPieceEnabled() {
        return ghostPieceEnabled;
    }

    public boolean isHardDropEnabled() {
        return hardDropEnabled;
    }

    public String getTheme() {
        return theme;
    }

    public String getDifficulty() {
        return difficulty;
    }

    // Setters
    public void setGhostPieceEnabled(boolean ghostPieceEnabled) {
        this.ghostPieceEnabled = ghostPieceEnabled;
    }

    public void setHardDropEnabled(boolean hardDropEnabled) {
        this.hardDropEnabled = hardDropEnabled;
    }

    public void setTheme(String theme) {
        if (isValidTheme(theme)) {
            this.theme = theme;
        }
    }

    public void setDifficulty(String difficulty) {
        if (isValidDifficulty(difficulty)) {
            this.difficulty = difficulty;
        }
    }

    /**
     * Gets the fall speed in milliseconds based on difficulty.
     *
     * @return the fall speed in milliseconds
     */
    public long getFallSpeedMillis() {
        switch (difficulty.toUpperCase()) {
            case "EASY":
                return 550;
            case "HARD":
                return 250;
            case "NORMAL":
            default:
                return 400;
        }
    }
}
