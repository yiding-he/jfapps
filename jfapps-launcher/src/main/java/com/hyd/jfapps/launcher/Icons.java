package com.hyd.jfapps.launcher;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.InputStream;

public class Icons {

    public static void setStageIcon(Stage stage) {
        setStageIcon(stage, "/logo.png");
    }

    public static void setStageIcon(Stage stage, String iconPath) {
        stage.getIcons().add(icon(iconPath));
    }

    public static Image icon(String iconPath) {
        InputStream resource = Icons.class.getResourceAsStream(iconPath);
        return resource == null ? null : new Image(resource);
    }

    public static ImageView iconView(String iconPath, double iconSize) {
        Image icon = icon(iconPath);
        return iconView(icon, iconSize);
    }

    public static ImageView iconView(Image icon, double iconSize) {
        if (icon == null) {
            return null;
        }

        ImageView imageView = new ImageView(icon);
        imageView.setFitWidth(iconSize);
        imageView.setFitHeight(iconSize);
        return imageView;
    }

}
