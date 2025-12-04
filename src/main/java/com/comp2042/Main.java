package com.comp2042;

import com.comp2042.view.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        SceneManager.initialize(primaryStage);
        SceneManager.showMenu();  // Start at main menu
    }

    public static void main(String[] args) {
        launch(args);
    }
}
