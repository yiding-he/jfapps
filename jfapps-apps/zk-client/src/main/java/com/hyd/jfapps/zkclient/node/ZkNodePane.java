package com.hyd.jfapps.zkclient.node;

import static de.jensd.fx.glyphs.GlyphsDude.createIconLabel;

import com.hyd.jfapps.zkclient.zk.ZkNode;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import java.util.List;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;

public class ZkNodePane extends HBox {

    private static final ContextMenu CONTEXT_MENU = new ContextMenu();

    static {
        CONTEXT_MENU.getItems().add(new MenuItem("复制名称"));
    }

    private List<String> path;

    private ZkNode zkNode;

    public ZkNodePane(List<String> path, ZkNode zkNode) {
        this.setSpacing(5);
        this.path = path;
        this.zkNode = zkNode;

        Label label = new Label(zkNode.getName());
        label.setWrapText(true);
        label.setFont(Font.font("DialogInput"));

        Label iconLabel;
        if (zkNode.getChildrenCount() == 0) {
            iconLabel = createIconLabel(FontAwesomeIcon.TAG, null, "10pt", null, ContentDisplay.CENTER);
        } else {
            iconLabel = createIconLabel(FontAwesomeIcon.FOLDER_OPEN_ALT, null, "10pt", null, ContentDisplay.CENTER);
        }

        this.getChildren().addAll(iconLabel, label);
        this.getStyleClass().addAll("zk-node", "zk-node-unselected");
        this.setOnContextMenuRequested(this::showContextMenu);
    }

    private void showContextMenu(ContextMenuEvent event) {
        CONTEXT_MENU.show(this, event.getScreenX(), event.getScreenY());
    }

    public ZkNode getZkNode() {
        return zkNode;
    }
}
