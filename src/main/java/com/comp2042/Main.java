package com.comp2042;

import com.comp2042.view.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Entry point for TetrisJFX.
 * Starts the application by showing the main menu via SceneManager.
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Central place that manages switching between Menu, Game, and Settings
        SceneManager sceneManager = new SceneManager(primaryStage);

        // Start on the main menu
        sceneManager.showMenu();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
