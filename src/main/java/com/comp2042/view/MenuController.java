package com.comp2042.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MenuController {

    @FXML private Button btnStart;
    @FXML private Button btnSettings;
    @FXML private Button btnExit;
    @FXML private VBox menuContainer;
    
    private SceneManager sceneManager;

    @FXML
    public void initialize() {
        // Prevent buttons from stealing focus or exiting fullscreen
        btnStart.setFocusTraversable(false);
        btnSettings.setFocusTraversable(false);
        btnExit.setFocusTraversable(false);
        
        btnStart.setOnAction(e -> SceneManager.showGame());
        btnSettings.setOnAction(e -> SceneManager.showSettings());
        btnExit.setOnAction(e -> SceneManager.exitGame());
        
        // Start background music when menu loads
        startBackgroundMusic();
        
        // Add F11 key handler to toggle fullscreen
        if (menuContainer != null && menuContainer.getScene() != null) {
            menuContainer.getScene().setOnKeyPressed(this::handleKeyPress);
        }
        
        // Center menu when initialized
        javafx.application.Platform.runLater(() -> {
            if (menuContainer != null && menuContainer.getScene() != null) {
                // Add F11 handler after scene is ready
                menuContainer.getScene().setOnKeyPressed(this::handleKeyPress);
                centerMenu();
                
                // Add listeners to recenter on window resize and fullscreen changes
                Stage stage = (Stage) menuContainer.getScene().getWindow();
                if (stage != null) {
                    stage.widthProperty().addListener((obs, oldVal, newVal) -> centerMenu());
                    stage.heightProperty().addListener((obs, oldVal, newVal) -> centerMenu());
                    stage.fullScreenProperty().addListener((obs, oldVal, newVal) -> {
                        javafx.application.Platform.runLater(() -> centerMenu());
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
     * Sets the SceneManager reference.
     *
     * @param sceneManager the SceneManager instance
     */
    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }
    
    /**
     * Centers the menu container in the window.
     */
    public void centerMenu() {
        if (menuContainer == null || sceneManager == null) return;
        
        javafx.application.Platform.runLater(() -> {
            Stage stage = sceneManager.getStage();
            if (stage == null || stage.getScene() == null) return;
            
            // Force layout pass to get actual bounds
            menuContainer.applyCss();
            menuContainer.layout();
            
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
                Pane root = (Pane) stage.getScene().getRoot();
                if (root != null) {
                    windowWidth = root.getWidth();
                    windowHeight = root.getHeight();
                }
            }
            
            if (windowWidth > 0 && windowHeight > 0) {
                // Get actual rendered bounds of the container
                javafx.geometry.Bounds bounds = menuContainer.getBoundsInLocal();
                double containerWidth = bounds.getWidth();
                double containerHeight = bounds.getHeight();
                
                // If bounds are 0, use preferred size
                if (containerWidth <= 0) {
                    containerWidth = menuContainer.getPrefWidth();
                }
                if (containerHeight <= 0) {
                    containerHeight = menuContainer.getPrefHeight();
                }
                
                // Calculate center position
                double centerX = (windowWidth - containerWidth) / 2.0;
                double centerY = (windowHeight - containerHeight) / 2.0;
                
                menuContainer.setLayoutX(centerX);
                menuContainer.setLayoutY(centerY);
            }
        });
    }
    
    /**
     * Starts background music when the menu loads.
     * Music will continue playing when entering the game.
     */
    private void startBackgroundMusic() {
        MusicManager musicManager = MusicManager.getInstance();
        if (musicManager != null && !musicManager.isPlaying()) {
            // Get volume from settings
            GlobalSettings settings = GlobalSettings.loadSettings();
            musicManager.setVolume(settings.getMusicVolume());
            
            // Start music (loops continuously)
            musicManager.playMusic("music/game_music.wav", true);
        }
    }
}
