package com.hyd.jfapps.zkclient.node;

import com.hyd.fx.dialog.AlertDialog;
import com.hyd.fx.system.ClipboardHelper;
import com.hyd.jfapps.zkclient.event.NavigationEvents;
import com.hyd.jfapps.zkclient.event.NodeEvents;
import com.hyd.jfapps.zkclient.zk.ZkNode;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;

import java.util.List;

import static com.hyd.fx.app.AppEvents.fireAppEvent;
import static com.hyd.fx.app.AppEvents.onAppEvent;
import static com.hyd.fx.builders.MenuBuilder.contextMenu;
import static com.hyd.fx.builders.MenuBuilder.menuItem;
import static com.hyd.jfapps.zkclient.FxUtil.icon;
import static de.jensd.fx.glyphs.GlyphsDude.createIconLabel;

public class ZkNodePane extends HBox {

    private static ContextMenu contextMenu;

    static {
        onAppEvent(NavigationEvents.LocationChangedEvent.class, event -> closeContextMenu());
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
        label.setMaxWidth(250);
        label.setMinWidth(250);

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

    public ZkNode getZkNode() {
        return zkNode;
    }

    private void showContextMenu(ContextMenuEvent event) {
        // 关闭当前正在打开的右键菜单
        closeContextMenu();

        // 在新的位置重新打开右键菜单
        contextMenu = createContextMenu();
        contextMenu.show(this, event.getScreenX(), event.getScreenY());
    }

    private static void closeContextMenu() {
        if (contextMenu != null && contextMenu.isShowing()) {
            contextMenu.hide();
            contextMenu = null;
        }
    }

    private ContextMenu createContextMenu() {
        if (this.zkNode.getChildrenCount() == 0) {
            return createNodeContextMenu();
        } else {
            return createFolderContextMenu();
        }
    }

    private ContextMenu createNodeContextMenu() {
        return contextMenu(
            menuItem("复制名称", icon(FontAwesomeIcon.COPY, "#3399CC"), this::copyName),
            menuItem("复制完整路径", icon(FontAwesomeIcon.COPY, "#3399CC"), this::copyFullName),
            menuItem("删除节点", icon(FontAwesomeIcon.TRASH, "#CC9933"), this::deleteNode),
            menuItem("添加子节点", icon(FontAwesomeIcon.PLUS_CIRCLE, "#33CC99"), this::addNewNode)
        );
    }

    private void copyName() {
        ClipboardHelper.putString(this.zkNode.getName());
    }

    private void copyFullName() {
        ClipboardHelper.putString(this.zkNode.getFullName());
    }

    private ContextMenu createFolderContextMenu() {
        return contextMenu(
            menuItem("复制名称", icon(FontAwesomeIcon.COPY, "#3399CC"), this::copyName),
            menuItem("复制完整路径", icon(FontAwesomeIcon.COPY, "#3399CC"), this::copyFullName),
            menuItem("删除节点及所有子节点", icon(FontAwesomeIcon.TRASH, "#CC9933"), this::deleteNode),
            menuItem("添加子节点", icon(FontAwesomeIcon.PLUS_CIRCLE, "#33CC99"), this::addNewNode)
        );
    }

    private void addNewNode() {
        fireAppEvent(new NodeEvents.AddNodeRequest(this.zkNode.getFullName()));
    }

    private void deleteNode() {
        String message = this.zkNode.getChildrenCount() == 0 ?
            ("确定要删除节点“" + this.zkNode.getName() + "”吗？") :
            ("确定要删除节点“" + this.zkNode.getName() + "”及所有子节点吗？");

        if (AlertDialog.confirmYesNo("删除节点", message)) {
            fireAppEvent(new NodeEvents.DeleteNodeRequest(this.zkNode.getFullName()));
        }
    }
}
