package com.comp2042.view;

import com.comp2042.controller.GameController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager {

    private static SceneManager instance;
    private final Stage stage;

    private SceneManager(Stage stage) {
        this.stage = stage;
    }

    public static void initialize(Stage stage) {
        instance = new SceneManager(stage);
    }

    public static SceneManager getInstance() {
        return instance;
    }

    public static void showMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource("/menu.fxml"));
            Parent root = loader.load();
            instance.stage.setScene(new Scene(root, 750, 600));
            instance.stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showSettings() {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource("/settings.fxml"));
            Parent root = loader.load();
            instance.stage.setScene(new Scene(root, 750, 600));
            instance.stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showGame() {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource("/gameLayout.fxml"));
            Parent root = loader.load();
            GuiController gui = loader.getController();
            
            // Set the scene manager reference so GuiController can navigate back to menu
            if (gui != null) {
                gui.setSceneManager(instance);
            }
            
            new GameController(gui);

            instance.stage.setScene(new Scene(root, 750, 600));
            instance.stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void exitGame() {
        instance.stage.close();
        System.exit(0);
    }
}
