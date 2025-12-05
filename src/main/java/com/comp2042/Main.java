package com.comp2042;

import com.comp2042.view.SceneManager;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Disable fullscreen exit hint (no popup)
        primaryStage.setFullScreenExitHint("");
        
        // Create initial empty scene - we'll replace the root node, never the scene
        Scene initialScene = new Scene(new Pane(), 750, 600);
        primaryStage.setScene(initialScene);
        primaryStage.show();
        
        SceneManager.initialize(primaryStage);
        SceneManager.showMenu();  // Start at main menu
    }

    public static void main(String[] args) {
        launch(args);
    }
}
