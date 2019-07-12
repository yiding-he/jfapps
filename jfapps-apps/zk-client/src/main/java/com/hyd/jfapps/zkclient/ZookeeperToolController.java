package com.hyd.jfapps.zkclient;

import static com.hyd.fx.builders.MenuBuilder.contextMenu;
import static com.hyd.fx.builders.MenuBuilder.menuItem;

import com.hyd.fx.concurrency.BackgroundTask;
import com.hyd.fx.dialog.AlertDialog;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.input.MouseButton;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@Slf4j
public class ZookeeperToolController extends ZookeeperToolView {

    private ZookeeperToolService zookeeperToolService = new ZookeeperToolService(this);

    private BackgroundTask openConnectionTask = BackgroundTask
        .runTask(() -> zookeeperToolService.initZkClient(
            zkServersTextField.getText(),
            cmbConnTimeout.getValue() * 1000
        ))
        .whenBeforeStart(() -> {
            connectButton.setDisable(true);
            lblStatus.setText("连接中...");
        })
        .whenTaskSuccess(() -> {
            lblStatus.setText("已连接");
            connectButton.setText("关闭连接");
            zookeeperToolService.setupTree();
        })
        .whenTaskFail(e -> {
            AlertDialog.error("连接失败", e);
            lblStatus.setText("连接失败");
            connectButton.setText("连接服务器");
        })
        .whenTaskFinish(() ->
            connectButton.setDisable(false)
        );

    private ContextMenu treeContextMenu = contextMenu(
        menuItem("添加子节点", zookeeperToolService::addNodeOnAction),
        menuItem("复制节点", zookeeperToolService::copyNodeOnAction),
        menuItem("删除", zookeeperToolService::deleteNodeOnAction),
        menuItem("添加节点修改通知", zookeeperToolService::addNodeNotify),
        menuItem("移除节点修改通知", zookeeperToolService::removeNodeNotify)
    );

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();
    }

    private void initView() {
        Optional.ofNullable(System.getProperty("server"))
            .ifPresent(server -> zkServersTextField.setText(server));

        zookeeperToolService.setOnConnected(() -> {
            mainPane.setDisable(false);
        });

        zookeeperToolService.setOnDisconnected(() -> {
            mainPane.setDisable(true);
        });
    }

    private void initEvent() {
        nodeTreeView.getSelectionModel().selectedItemProperty()
            .addListener((_ob, _old, _new) -> zookeeperToolService.nodeSelectionChanged(_new));

        nodeTreeView.setOnMouseClicked(event -> {
            if (nodeTreeView.getSelectionModel().getSelectedItem() == null) {
                return;
            }
            if (event.getButton() == MouseButton.SECONDARY) {
                nodeTreeView.setContextMenu(treeContextMenu);
            }
        });
    }

    @FXML
    private void connectButtonClicked() {
        if (zookeeperToolService.isConnected()) {
            zookeeperToolService.disconnect();
            connectButton.setText("连接服务器");
        } else {
            openConnectionTask.start();
        }
    }

    @FXML
    private void nodeDataSaveOnAction() {
        zookeeperToolService.nodeDataSaveOnAction();
    }


}