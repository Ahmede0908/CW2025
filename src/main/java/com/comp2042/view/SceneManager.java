package com.comp2042.view;

import com.comp2042.controller.GameController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager {

    private final Stage stage;

    public SceneManager(Stage stage) {
        this.stage = stage;
    }

    /** Shows the main menu screen */
    public void showMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/menu.fxml"));
            Parent root = loader.load();

            MenuController menuController = loader.getController();
            menuController.setSceneManager(this);

            Scene menuScene = new Scene(root, 400, 500);
            stage.setScene(menuScene);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Shows the Tetris game screen */
    public void showGame() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gameLayout.fxml"));
            Parent root = loader.load();

            GuiController controller = loader.getController();

            Scene scene = new Scene(root, 300, 510);
            stage.setScene(scene);

            // Your fullscreen centering logic
            stage.fullScreenProperty().addListener((obs, old, isFull) -> {
                if (isFull) controller.setupFullscreenCentering(stage);
            });

            // Start the game
            new GameController(controller);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Stage getStage() {
        return stage;
    }

    /** Closes the game */
    public void exitGame() {
        stage.close();
    }
}
