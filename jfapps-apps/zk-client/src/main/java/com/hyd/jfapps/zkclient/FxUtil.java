package com.hyd.jfapps.zkclient;

import de.jensd.fx.glyphs.GlyphIcons;
import de.jensd.fx.glyphs.GlyphsDude;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
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
        Label label = new Label("", icon(icon, size, color));
        label.setContentDisplay(ContentDisplay.CENTER);
        return label;
    }

    public static Button iconButton(GlyphIcons icon, String color, String buttonText) {
        return new Button(buttonText, icon(icon, color));
    }

    public static Hyperlink iconLink(GlyphIcons icon, String color, Runnable onAction) {
        Hyperlink hyperlink = new Hyperlink("", icon(icon, color));
        hyperlink.setOnAction(event -> onAction.run());
        return hyperlink;
    }

    public static Text icon(GlyphIcons icon, String color) {
        return icon(icon, "16px", color);
    }

    public static Text icon(GlyphIcons icon, String size, String color) {
        Text text = GlyphsDude.createIcon(icon, size);
        text.setFill(Color.web(color));
        return text;
    }
}
