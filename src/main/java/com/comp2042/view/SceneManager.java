package com.comp2042.view;

import com.comp2042.controller.GameController;
import javafx.animation.FadeTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

/**
 * Central scene manager that switches between:
 *  - Main menu (menu.fxml)
 *  - Game screen (gameLayout.fxml)
 *  - Settings screen (settings.fxml)
 *
 * It also applies simple fade-in transitions when switching scenes
 * to make the UI feel smoother and more polished.
 */
public class SceneManager {

    private final Stage stage;

    public SceneManager(Stage stage) {
        this.stage = stage;
        this.stage.setTitle("TetrisJFX");
    }

    // ---------- FXML LOADER HELPER ----------

    private FXMLLoader load(String fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(fxml));
        if (loader.getLocation() == null) {
            throw new IOException("Cannot load FXML: " + fxml);
        }
        return loader;
    }

    // ---------- FADE TRANSITION HELPER ----------

    /**
     * Applies a simple fade-in animation to the given root node.
     * This is called after setting the scene on the stage.
     */
    private void playFadeIn(Parent root) {
        FadeTransition ft = new FadeTransition(Duration.millis(300), root);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();
    }

    // ---------- MENU SCENE ----------

    public void showMenu() {
        try {
            FXMLLoader loader = load("menu.fxml");
            Parent root = loader.load();

            MenuController controller = loader.getController();
            controller.setSceneManager(this);

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();

            // Apply transition
            playFadeIn(root);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ---------- SETTINGS SCENE ----------

    public void showSettings() {
        try {
            FXMLLoader loader = load("settings.fxml");
            Parent root = loader.load();

            SettingsController controller = loader.getController();
            controller.setSceneManager(this);

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();

            // Apply transition
            playFadeIn(root);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ---------- GAME SCENE ----------

    public void showGame() {
        try {
            FXMLLoader loader = load("gameLayout.fxml");
            Parent root = loader.load();

            GuiController gui = loader.getController();
            gui.setSceneManager(this);

            // Same size as your original game window
            Scene scene = new Scene(root, 300, 510);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();

            // Keep your fullscreen centering logic
            gui.setupFullscreenCentering(stage);

            // Wire MVC
            new GameController(gui);

            // Apply transition
            playFadeIn(root);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ---------- EXIT ----------

    public void exitGame() {
        stage.close();
    }
}
