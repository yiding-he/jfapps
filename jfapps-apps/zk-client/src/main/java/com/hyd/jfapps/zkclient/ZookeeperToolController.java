package com.hyd.jfapps.zkclient;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.input.MouseButton;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@Slf4j
public class ZookeeperToolController extends ZookeeperToolView {

    public Label lblStatus;

    private ZookeeperToolService zookeeperToolService = new ZookeeperToolService(this);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();
        initService();
    }

    private void initView() {
        JavaFxViewUtil.setSpinnerValueFactory(connectionTimeoutSpinner, 0, Integer.MAX_VALUE, 30000);
        zookeeperToolService.setupTree();

        Optional.ofNullable(System.getProperty("server"))
            .ifPresent(server -> zkServersTextField.setText(server));
    }

    private void initEvent() {
        nodeTreeView.setOnMouseClicked(event -> {
            TreeItem<String> selectedItem = nodeTreeView.getSelectionModel().getSelectedItem();
            if (selectedItem == null) {
                return;
            }
            if (event.getButton() == MouseButton.PRIMARY) {
                zookeeperToolService.nodeSelectionChanged(selectedItem);
            } else if (event.getButton() == MouseButton.SECONDARY) {
                MenuItem menu_UnfoldAll = new MenuItem("展开所有");
                menu_UnfoldAll.setOnAction(event1 -> {
                    nodeTreeView.getRoot().setExpanded(true);
                    nodeTreeView.getRoot().getChildren().forEach(stringTreeItem -> {
                        stringTreeItem.setExpanded(true);
                    });
                });
                MenuItem menu_FoldAll = new MenuItem("折叠所有");
                menu_FoldAll.setOnAction(event1 -> {
                    nodeTreeView.getRoot().setExpanded(false);
                    nodeTreeView.getRoot().getChildren().forEach(stringTreeItem -> {
                        stringTreeItem.setExpanded(false);
                    });
                });
                ContextMenu contextMenu = new ContextMenu(menu_UnfoldAll, menu_FoldAll);
                MenuItem menu_AddNode = new MenuItem("添加子节点");
                menu_AddNode.setOnAction(event1 -> {
                    zookeeperToolService.addNodeOnAction();
                });
                contextMenu.getItems().add(menu_AddNode);
                MenuItem menu_Rename = new MenuItem("重命名节点");
                menu_Rename.setOnAction(event1 -> {
                    zookeeperToolService.renameNodeOnAction(false);
                });
                contextMenu.getItems().add(menu_Rename);
                MenuItem menu_Copy = new MenuItem("复制节点");
                menu_Copy.setOnAction(event1 -> {
                    zookeeperToolService.renameNodeOnAction(true);
                });
                contextMenu.getItems().add(menu_Copy);
                MenuItem menu_RemoveNode = new MenuItem("删除");
                menu_RemoveNode.setOnAction(event1 -> {
                    zookeeperToolService.deleteNodeOnAction();
                });
                contextMenu.getItems().add(menu_RemoveNode);
                MenuItem menu_AddNodeNotify = new MenuItem("添加节点修改通知");
                menu_AddNodeNotify.setOnAction(event1 -> {
                    zookeeperToolService.addNodeNotify();
                });
                contextMenu.getItems().add(menu_AddNodeNotify);
                MenuItem menu_RemoveNodeNotify = new MenuItem("移除节点修改通知");
                menu_RemoveNodeNotify.setOnAction(event1 -> {
                    zookeeperToolService.removeNodeNotify();
                });
                contextMenu.getItems().add(menu_RemoveNodeNotify);
                nodeTreeView.setContextMenu(contextMenu);
            }
        });
    }

    private void initService() {
    }

    @FXML
    private void connectOnAction(ActionEvent event) {
        connectButton.setDisable(true);
        nodeTreeView.setDisable(true);

        zookeeperToolService.connect(
            zkServersTextField.getText().trim(),
            connectionTimeoutSpinner.getValue(),
            () -> zookeeperToolService.setupTree(),
            e -> AlertDialog.error("连接失败", e),
            () -> {
                connectButton.setDisable(false);
                nodeTreeView.setDisable(false);
            }
        );
    }

    @FXML
    private void disconnectOnAction(ActionEvent event) {
        zookeeperToolService.disconnectOnAction();
    }

    @FXML
    private void nodeDataSaveOnAction(ActionEvent event) {
        zookeeperToolService.nodeDataSaveOnAction();
    }


}