package com.comp2042.view;

import com.comp2042.controller.GameController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneManager {

    private final Stage stage;

    public SceneManager(Stage stage) {
        this.stage = stage;
        this.stage.setTitle("TetrisJFX");
    }

    private FXMLLoader load(String fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(fxml));
        if (loader.getLocation() == null) {
            throw new IOException("Cannot load FXML: " + fxml);
        }
        return loader;
    }

    // ---------------- MENU ----------------
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

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ---------------- SETTINGS ----------------
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

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ---------------- GAME ----------------
    public void showGame() {
        try {
            FXMLLoader loader = load("gameLayout.fxml");
            Parent root = loader.load();

            GuiController gui = loader.getController();
            gui.setSceneManager(this);

            Scene scene = new Scene(root, 300, 510);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();

            gui.setupFullscreenCentering(stage);
            new GameController(gui);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ---------------- EXIT ----------------
    public void exitGame() {
        stage.close();
    }
}
