package com.comp2042.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class MenuController {

    @FXML private Button btnStart;
    @FXML private Button btnSettings;
    @FXML private Button btnExit;

    private SceneManager sceneManager;

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    @FXML
    private void initialize() {
        btnStart.setOnAction(e -> sceneManager.showGame());
        btnSettings.setOnAction(e -> sceneManager.showSettings());
        btnExit.setOnAction(e -> sceneManager.exitGame());
    }
}
