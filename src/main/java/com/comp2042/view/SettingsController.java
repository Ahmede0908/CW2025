package com.comp2042.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class SettingsController {

    @FXML private Button btnBack;
    private SceneManager sceneManager;

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    @FXML
    private void initialize() {
        btnBack.setOnAction(e -> sceneManager.showMenu());
    }
}
