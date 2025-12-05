package com.comp2042.view;

import com.comp2042.controller.GameController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Manages scene transitions and global settings for the Tetris game.
 * <p>
 * Handles switching between Menu, Settings, and Game screens.
 * Manages fullscreen state and ensures it persists across scene changes.
 * NEVER replaces the Scene - only replaces the root node to preserve fullscreen.
 * </p>
 *
 * @author TetrisJFX Team
 * @version 1.0
 */
public class SceneManager {

    private static SceneManager instance;
    private final Stage stage;
    private GlobalSettings settings;
    private boolean isFullscreen = false; // Track fullscreen state

    private SceneManager(Stage stage) {
        this.stage = stage;
        this.settings = GlobalSettings.loadSettings();
        
        // Initialize fullscreen state
        isFullscreen = stage.isFullScreen();
        
        // Disable fullscreen exit hint (no popup)
        stage.setFullScreenExitHint("");
        
        // Listen to fullscreen changes to keep our state in sync
        stage.fullScreenProperty().addListener((obs, oldVal, newVal) -> {
            // Only update if change was intentional (not from our code)
            if (newVal != isFullscreen) {
                isFullscreen = newVal;
            }
        });
    }

    public static void initialize(Stage stage) {
        instance = new SceneManager(stage);
    }

    public static SceneManager getInstance() {
        return instance;
    }

    /**
     * Gets the current global settings.
     *
     * @return the GlobalSettings instance
     */
    public GlobalSettings getSettings() {
        if (settings == null) {
            settings = GlobalSettings.loadSettings();
        }
        return settings;
    }

    /**
     * Toggles fullscreen mode. Only method that should change fullscreen state.
     *
     * @return true if fullscreen is now enabled, false otherwise
     */
    public boolean toggleFullscreen() {
        isFullscreen = !isFullscreen;
        stage.setFullScreen(isFullscreen);
        stage.setFullScreenExitHint("");
        return isFullscreen;
    }

    /**
     * Gets the current fullscreen state.
     *
     * @return true if in fullscreen, false otherwise
     */
    public boolean isFullscreen() {
        return isFullscreen;
    }

    /**
     * Sets fullscreen state (only used internally).
     *
     * @param fullscreen true to enable fullscreen, false to disable
     */
    private void setFullscreen(boolean fullscreen) {
        if (isFullscreen != fullscreen) {
            isFullscreen = fullscreen;
            stage.setFullScreen(fullscreen);
            stage.setFullScreenExitHint("");
        }
    }

    public static void showMenu() {
        try {
            // Preserve fullscreen state BEFORE loading
            boolean wasFullscreen = instance.isFullscreen;
            
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource("/menu.fxml"));
            Parent root = loader.load();
            MenuController controller = loader.getController();
            
            // Set scene manager reference for centering
            if (controller != null) {
                controller.setSceneManager(instance);
            }
            
            // CRITICAL: Replace root node, NOT the scene
            Scene currentScene = instance.stage.getScene();
            if (currentScene == null) {
                // Fallback: create scene if it doesn't exist (shouldn't happen)
                currentScene = new Scene(root, 750, 600);
                instance.stage.setScene(currentScene);
            } else {
                // Replace root node - this preserves fullscreen state
                currentScene.setRoot(root);
            }
            
            // Update fullscreen state
            instance.isFullscreen = instance.stage.isFullScreen();
            
            // Ensure fullscreen is maintained
            if (wasFullscreen) {
                instance.setFullscreen(true);
            }
            
            // Center menu after root is set and scene is rendered
            javafx.application.Platform.runLater(() -> {
                javafx.application.Platform.runLater(() -> {
                    if (controller != null) {
                        controller.centerMenu();
                    }
                });
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showSettings() {
        try {
            // Preserve fullscreen state BEFORE loading
            boolean wasFullscreen = instance.isFullscreen;
            
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource("/settings.fxml"));
            Parent root = loader.load();
            SettingsController controller = loader.getController();
            
            // Set scene manager reference
            if (controller != null) {
                controller.setSceneManager(instance);
            }
            
            // CRITICAL: Replace root node, NOT the scene
            Scene currentScene = instance.stage.getScene();
            if (currentScene == null) {
                // Fallback: create scene if it doesn't exist (shouldn't happen)
                currentScene = new Scene(root, 750, 600);
                instance.stage.setScene(currentScene);
            } else {
                // Replace root node - this preserves fullscreen state
                currentScene.setRoot(root);
            }
            
            // Update fullscreen state
            instance.isFullscreen = instance.stage.isFullScreen();
            
            // Ensure fullscreen is maintained
            if (wasFullscreen) {
                instance.setFullscreen(true);
            }
            
            // Center settings after root is set and scene is rendered
            javafx.application.Platform.runLater(() -> {
                javafx.application.Platform.runLater(() -> {
                    if (controller != null) {
                        controller.centerSettings();
                    }
                });
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showGame() {
        try {
            // Preserve fullscreen state BEFORE loading
            boolean wasFullscreen = instance.isFullscreen;
            
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource("/gameLayout.fxml"));
            Parent root = loader.load();
            GuiController gui = loader.getController();
            
            // Set the scene manager reference so GuiController can navigate back to menu
            if (gui != null) {
                gui.setSceneManager(instance);
                // Apply settings to GuiController before creating GameController
                gui.applySettings(instance.getSettings());
            }
            
            new GameController(gui);

            // CRITICAL: Replace root node, NOT the scene
            Scene currentScene = instance.stage.getScene();
            if (currentScene == null) {
                // Fallback: create scene if it doesn't exist (shouldn't happen)
                currentScene = new Scene(root, 750, 600);
                instance.stage.setScene(currentScene);
            } else {
                // Replace root node - this preserves fullscreen state
                currentScene.setRoot(root);
            }
            
            // Update fullscreen state
            instance.isFullscreen = instance.stage.isFullScreen();
            
            // Ensure fullscreen is maintained
            if (wasFullscreen) {
                instance.setFullscreen(true);
            }
            
            // Setup centering after root is set
            if (gui != null) {
                // Setup fullscreen centering first (this sets up listeners)
                gui.setupFullscreenCentering(instance.stage);
                
                // Re-center after fullscreen is restored
                javafx.application.Platform.runLater(() -> {
                    if (wasFullscreen) {
                        instance.setFullscreen(true);
                    }
                    gui.forceRecenter(instance.stage);
                });
            } else {
                // Restore fullscreen if gui is null
                if (wasFullscreen) {
                    javafx.application.Platform.runLater(() -> {
                        instance.setFullscreen(true);
                    });
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void exitGame() {
        instance.stage.close();
        System.exit(0);
    }
    
    /**
     * Gets the Stage instance (for controllers that need to access it).
     *
     * @return the Stage instance
     */
    public Stage getStage() {
        return stage;
    }
}
