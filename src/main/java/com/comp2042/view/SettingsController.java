package com.comp2042.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class SettingsController {

    @FXML private Button btnBack;

    @FXML
    private void initialize() {
        btnBack.setOnAction(e -> SceneManager.showMenu());
    }
}
