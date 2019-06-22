package com.hyd.jfapps.appbase;

import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Icons {

    public static void setStageIcon(Stage stage) {
        setStageIcon(stage, "/logo.png");
    }

    public static void setStageIcon(Stage stage, String iconPath) {
        stage.getIcons().add(new Image(Icons.class.getResourceAsStream(iconPath)));
    }
}
