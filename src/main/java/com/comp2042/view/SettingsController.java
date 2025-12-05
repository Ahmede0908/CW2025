package com.comp2042.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

/**
 * Controller for the Settings screen.
 * <p>
 * Manages user settings including ghost piece, hard drop, and difficulty.
 * Loads settings from GlobalSettings and saves changes when user clicks Save & Apply.
 * </p>
 *
 * @author TetrisJFX Team
 * @version 1.0
 */
public class SettingsController {

    @FXML private CheckBox ghostToggle;
    @FXML private CheckBox hardDropToggle;
    @FXML private ComboBox<String> difficultyDropdown;
    @FXML private Button btnSave;
    @FXML private Button btnBack;
    @FXML private javafx.scene.layout.VBox settingsContainer;

    private GlobalSettings settings;
    private SceneManager sceneManager;

    /**
     * Initializes the settings controller.
     * <p>
     * Loads current settings from GlobalSettings and populates the UI components.
     * Sets up dropdown options for difficulty.
     * </p>
     */
    @FXML
    public void initialize() {
        // Prevent buttons from stealing focus or exiting fullscreen
        btnSave.setFocusTraversable(false);
        btnBack.setFocusTraversable(false);
        ghostToggle.setFocusTraversable(false);
        hardDropToggle.setFocusTraversable(false);
        difficultyDropdown.setFocusTraversable(false);
        
        // Get settings instance
        settings = GlobalSettings.loadSettings();
        
        // Get scene manager instance
        sceneManager = SceneManager.getInstance();
        
        // Load current settings into UI
        ghostToggle.setSelected(settings.isGhostPieceEnabled());
        hardDropToggle.setSelected(settings.isHardDropEnabled());
        
        // Setup difficulty dropdown
        ObservableList<String> difficultyOptions = FXCollections.observableArrayList(
            "EASY", "NORMAL", "HARD"
        );
        difficultyDropdown.setItems(difficultyOptions);
        difficultyDropdown.setValue(settings.getDifficulty());
        
        // Add F11 key handler to toggle fullscreen
        javafx.application.Platform.runLater(() -> {
            if (settingsContainer != null && settingsContainer.getScene() != null) {
                settingsContainer.getScene().setOnKeyPressed(this::handleKeyPress);
                centerSettings();
                
                // Add listeners to recenter on window resize and fullscreen changes
                Stage stage = (Stage) settingsContainer.getScene().getWindow();
                if (stage != null) {
                    stage.widthProperty().addListener((obs, oldVal, newVal) -> centerSettings());
                    stage.heightProperty().addListener((obs, oldVal, newVal) -> centerSettings());
                    stage.fullScreenProperty().addListener((obs, oldVal, newVal) -> {
                        javafx.application.Platform.runLater(() -> centerSettings());
                    });
                }
            }
        });
    }
    
    /**
     * Handles key presses, specifically F11 for fullscreen toggle.
     */
    private void handleKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.F11) {
            if (sceneManager != null) {
                sceneManager.toggleFullscreen();
            }
            event.consume();
        }
    }

    /**
     * Applies the current settings and saves them.
     * <p>
     * Updates GlobalSettings with current UI values, saves to file,
     * and returns to menu.
     * </p>
     */
    @FXML
    public void applySettings() {
        // Update settings from UI
        settings.setGhostPieceEnabled(ghostToggle.isSelected());
        settings.setHardDropEnabled(hardDropToggle.isSelected());
        
        if (difficultyDropdown.getValue() != null) {
            settings.setDifficulty(difficultyDropdown.getValue());
        }
        
        // Save to file
        settings.saveSettings();
        
        // Return to menu (fullscreen will be preserved by SceneManager)
        goBackToMenu();
    }

    /**
     * Returns to the main menu without saving changes.
     */
    @FXML
    public void goBackToMenu() {
        // Fullscreen will be preserved by SceneManager
        SceneManager.showMenu();
    }

    /**
     * Sets the SceneManager reference.
     *
     * @param sceneManager the SceneManager instance
     */
    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }
    
    /**
     * Centers the settings container in the window.
     */
    public void centerSettings() {
        if (settingsContainer == null || sceneManager == null) return;
        
        javafx.application.Platform.runLater(() -> {
            Stage stage = sceneManager.getStage();
            if (stage == null || stage.getScene() == null) return;
            
            // Force layout pass to get actual bounds
            settingsContainer.applyCss();
            settingsContainer.layout();
            
            // Get actual window dimensions
            double windowWidth = stage.getWidth();
            double windowHeight = stage.getHeight();
            
            // If stage dimensions are 0, use scene dimensions
            if (windowWidth <= 0 || windowHeight <= 0) {
                javafx.scene.Scene scene = stage.getScene();
                if (scene != null) {
                    windowWidth = scene.getWidth();
                    windowHeight = scene.getHeight();
                }
            }
            
            // If still 0, use root pane dimensions
            if (windowWidth <= 0 || windowHeight <= 0) {
                javafx.scene.layout.Pane root = (javafx.scene.layout.Pane) stage.getScene().getRoot();
                if (root != null) {
                    windowWidth = root.getWidth();
                    windowHeight = root.getHeight();
                }
            }
            
            if (windowWidth > 0 && windowHeight > 0) {
                // Get actual rendered bounds of the container
                javafx.geometry.Bounds bounds = settingsContainer.getBoundsInLocal();
                double containerWidth = bounds.getWidth();
                double containerHeight = bounds.getHeight();
                
                // If bounds are 0, use preferred size
                if (containerWidth <= 0) {
                    containerWidth = settingsContainer.getPrefWidth();
                }
                if (containerHeight <= 0) {
                    containerHeight = settingsContainer.getPrefHeight();
                }
                
                // Calculate center position
                double centerX = (windowWidth - containerWidth) / 2.0;
                double centerY = (windowHeight - containerHeight) / 2.0;
                
                settingsContainer.setLayoutX(centerX);
                settingsContainer.setLayoutY(centerY);
            }
        });
    }
}
