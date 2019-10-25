package com.hyd.jfapps.zkclient;

import com.hyd.fx.dialog.AlertDialog;
import com.hyd.jfapps.zkclient.config.Config;
import com.hyd.jfapps.zkclient.config.UserPreferences;
import com.hyd.jfapps.zkclient.event.*;
import com.hyd.jfapps.zkclient.node.ZkNodePane;
import com.hyd.jfapps.zkclient.zk.ZkService;
import java.util.*;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
@Slf4j
public class ZookeeperToolController {

    public ComboBox<String> comboServerAddr;

    public ComboBox<Integer> comboConnTimeout;

    public Button btnConnect;

    public SplitPane mainPane;

    public TextArea txtNodeData;

    public Label lblStatus;

    public FlowPane fpLocation;

    public FlowPane fpChildNodes;

    private ZkService service = new ZkService();

    public void initialize() {
        comboServerAddr.getItems().addAll(UserPreferences.get(Config.ServerAddresses));

        Listeners.addListener(ZkConnectedEvent.class, event -> {
            lblStatus.setText("服务器已连接。");
            btnConnect.setText("断开连接");
            mainPane.setDisable(false);
            service.setCurrentLocation(Collections.emptyList());
        });

        Listeners.addListener(ZkDisconnectedEvent.class, event -> {
            lblStatus.setText("尚未连接服务器。");
            btnConnect.setText("连接服务器");
            mainPane.setDisable(true);
        });

        Listeners.addListener(LocationChangedEvent.class, event -> {
            log.info("location -> {}", service.getCurrentLocation());
            service.watch();
            updateLocation();
            showNodes();
        });

        Listeners.addListener(ZkNodeSelectedEvent.class, event -> {
            fpChildNodes.getChildren().forEach(node -> {
                if (node instanceof ZkNodePane) {
                    if (node == event.getZkNodePane()) {
                        FxUtil.switchClass(node, "zk-node-unselected", "zk-node-selected");
                    } else {
                        FxUtil.switchClass(node, "zk-node-selected", "zk-node-unselected");
                    }
                }
            });
        });

        Listeners.addListener(ZkNodeSelectedEvent.class, event -> {
            String name = event.getZkNodePane().getZkNode().getName();
            Object nodeData = service.getNodeData(name);
            txtNodeData.setText(String.valueOf(nodeData));
            txtNodeData.setEditable(nodeData instanceof CharSequence);
        });

        Listeners.addListener(ChildrenChangedEvent.class, event -> {
            showNodes();
        });
    }

    private void updateLocation() {
        fpLocation.getChildren().clear();
        fpLocation.getChildren().add(
            createLocationLink("/", Collections.emptyList())
        );

        List<String> path = new ArrayList<>();

        service.getCurrentLocation().forEach(item -> {
            fpLocation.getChildren().add(new Label(">"));

            path.add(item);
            Hyperlink link = createLocationLink(item, new ArrayList<>(path));

            fpLocation.getChildren().add(link);
        });
    }

    private Hyperlink createLocationLink(String text, List<String> location) {
        Hyperlink link = new Hyperlink(text);
        link.setOnAction(event -> service.setCurrentLocation(location));
        return link;
    }

    private void showNodes() {
        fpChildNodes.getChildren().clear();

        service.listChildren().forEach(item -> {
            ZkNodePane zkNodePane = new ZkNodePane(service.getCurrentLocation(), item);
            zkNodePane.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
                ZkNodePane pane = (ZkNodePane) event.getSource();
                Listeners.publish(new ZkNodeSelectedEvent(pane));
                if (event.getClickCount() == 2 && pane.getZkNode().getChildrenCount() > 0) {
                    service.goInto(item.getName());
                }
            });
            fpChildNodes.getChildren().add(zkNodePane);
        });
    }

    public void btnConnectClicked() {
        if (service.isConnected()) {
            service.disconnect();
        } else {
            if (StringUtils.isAnyBlank(comboServerAddr.getValue())) {
                AlertDialog.error("错误", "服务器地址不能为空");
                return;
            }

            UserPreferences.append(Config.ServerAddresses, comboServerAddr.getValue());

            service.connect(
                comboServerAddr.getValue(),
                comboConnTimeout.getValue() * 1000
            );
        }
    }

    public void saveNodeData() {
    }
}