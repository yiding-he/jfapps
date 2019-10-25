package com.hyd.jfapps.zkclient;

import de.jensd.fx.glyphs.GlyphIcons;
import de.jensd.fx.glyphs.GlyphsDude;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class FxUtil {

    public static void switchClass(Node node, String removeClass, String addClass) {
        ObservableList<String> styleClass = node.getStyleClass();
        if (removeClass != null) {
            styleClass.remove(removeClass);
        }
        if (addClass != null && !styleClass.contains(addClass)) {
            styleClass.add(addClass);
        }
    }

    public static Label iconLabel(GlyphIcons icon, String size, String color) {
        Text iconLabel = GlyphsDude.createIcon(icon, size);
        iconLabel.setFill(Color.web(color));
        Label label = new Label();
        label.setGraphic(iconLabel);
        label.setContentDisplay(ContentDisplay.CENTER);
        return label;
    }

    public static Button iconButton(GlyphIcons icon, String text) {
        Button button = new Button(text);
        button.setGraphic(GlyphsDude.createIcon(icon, "16px"));
        return button;
    }

    public static Hyperlink iconLink(GlyphIcons icon, String color, Runnable onAction) {
        Text text = GlyphsDude.createIcon(icon, "16px");
        text.setFill(Color.web(color));
        Hyperlink hyperlink = new Hyperlink("", text);
        hyperlink.setOnAction(event -> onAction.run());
        return hyperlink;
    }
}
