package com.comp2042.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class MenuController {

    @FXML private Button btnStart;
    @FXML private Button btnSettings;
    @FXML private Button btnExit;

    @FXML
    public void initialize() {
        btnStart.setOnAction(e -> SceneManager.showGame());
        btnSettings.setOnAction(e -> SceneManager.showSettings());
        btnExit.setOnAction(e -> SceneManager.exitGame());
    }
}
