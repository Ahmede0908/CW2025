package com.comp2042.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class MenuController {

    private SceneManager sceneManager;

    @FXML private Button btnStart;
    @FXML private Button btnExit;

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    @FXML
    private void initialize() {
        btnStart.setOnAction(e -> {
            sceneManager.showGame();
            sceneManager.getStage().setFullScreen(true);
        });

        btnExit.setOnAction(e -> sceneManager.exitGame());
    }
}
