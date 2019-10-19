package com.hyd.jfapps.zkclient;

import javafx.collections.ObservableList;
import javafx.scene.Node;

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
}
