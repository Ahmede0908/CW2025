package com.comp2042;

import com.comp2042.view.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle("TetrisJFX");
        primaryStage.setResizable(true);
        primaryStage.setFullScreenExitHint("");
        primaryStage.setFullScreenExitKeyCombination(null); // optional

        SceneManager sceneManager = new SceneManager(primaryStage);

        sceneManager.showMenu();

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
