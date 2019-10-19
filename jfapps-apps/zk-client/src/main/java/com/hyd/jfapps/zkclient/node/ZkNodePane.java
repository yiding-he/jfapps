package com.hyd.jfapps.zkclient.node;

import com.hyd.jfapps.zkclient.zk.ZkNode;
import java.util.List;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class ZkNodePane extends VBox {

    private static final ContextMenu CONTEXT_MENU = new ContextMenu();

    static {
        CONTEXT_MENU.getItems().add(new MenuItem("复制名称"));
    }

    private List<String> path;

    private ZkNode zkNode;

    public ZkNodePane(List<String> path, ZkNode zkNode) {
        this.path = path;
        this.zkNode = zkNode;

        Label label = new Label(zkNode.getName());
        label.setWrapText(true);
        label.setFont(Font.font("DialogInput"));

        this.getChildren().add(label);
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
