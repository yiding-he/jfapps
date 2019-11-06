package com.hyd.jfapps.zkclient;

import static com.hyd.fx.app.AppThread.runUIThread;
import static com.hyd.jfapps.zkclient.FxUtil.iconLabel;
import static com.hyd.jfapps.zkclient.FxUtil.iconLink;

import com.hyd.fx.NodeUtils;
import com.hyd.fx.dialog.AlertDialog;
import com.hyd.jfapps.zkclient.config.Config;
import com.hyd.jfapps.zkclient.config.UserPreferences;
import com.hyd.jfapps.zkclient.dialog.NewNodeDialog;
import com.hyd.jfapps.zkclient.event.*;
import com.hyd.jfapps.zkclient.node.ZkNodePane;
import com.hyd.jfapps.zkclient.zk.ZkService;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.data.Stat;

@Getter
@Setter
@Slf4j
public class ZookeeperToolController {

    private static final ThreadLocal<SimpleDateFormat> DATE_FORMAT = ThreadLocal.withInitial(
        () -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    );

    private static String format(Date date) {
        return DATE_FORMAT.get().format(date);
    }

    private static String format(long timestamp) {
        return DATE_FORMAT.get().format(new Date(timestamp));
    }

    public ComboBox<String> comboServerAddr;

    public ComboBox<Integer> comboConnTimeout;

    public Button btnConnect;

    public SplitPane mainPane;

    public TextArea txtNodeData;

    public Label lblStatus;

    public FlowPane fpLocation;

    public FlowPane fpChildNodes;

    public TabPane nodeDataPane;

    public TextField txtSearch;

    public ScrollPane spNodesPane;

    public TextField txtAversion;

    public TextField txtCtime;

    public TextField txtCversion;

    public TextField txtCzxid;

    public TextField txtDataLength;

    public TextField txtEphemeralOwner;

    public TextField txtMtime;

    public TextField txtMzxid;

    public TextField txtNumChildren;

    public TextField txtVersion;

    private ZkService service = new ZkService();

    private ZkNodePane currentSelectedNode;

    public void initialize() {
        comboServerAddr.getItems().addAll(UserPreferences.get(Config.ServerAddresses));

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            forEachNodePane(nodeDataPane -> {
                boolean match = nodeDataPane
                    .getZkNode().getName().toLowerCase()
                    .contains(txtSearch.getText().trim().toLowerCase());
                nodeDataPane.setVisible(match);
            });
        });

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
            refreshNodes();
        });

        Listeners.addListener(ZkNodeUnselectedEvent.class, event -> {
            nodeDataPane.setDisable(true);
        });

        Listeners.addListener(ZkNodeSelectedEvent.class, event -> {
            forEachNodePane(node -> {
                if (node == event.getZkNodePane()) {
                    FxUtil.switchClass(node, "zk-node-unselected", "zk-node-selected");
                } else {
                    FxUtil.switchClass(node, "zk-node-selected", "zk-node-unselected");
                }
            });
        });

        Listeners.addListener(ZkNodeSelectedEvent.class, event -> {
            currentSelectedNode = event.getZkNodePane();
            nodeDataPane.setDisable(false);

            String name = currentSelectedNode.getZkNode().getName();
            refreshNodeData(service.getNodeData(name));
            refreshNodeMata(service.getNodeMetadata(name));
        });

        Listeners.addListener(ZkNodeSelectedEvent.class, event -> {
            service.watchNode(event.getZkNodePane().getZkNode().getFullName());
        });

        Listeners.addListener(NodeDataChangedEvent.class, event -> {
            refreshNodeData(event.getData());
        });

        Listeners.addListener(ChildrenChangedEvent.class, event -> {
            runUIThread(this::refreshNodes);
        });

        Listeners.addListener(AddNodeRequest.class, event -> {
            newNode(event.getParent());
        });

        Listeners.addListener(DeleteNodeRequest.class, event -> {
            service.deleteNode(event.getNodePath());
        });
    }

    private void forEachNodePane(Consumer<ZkNodePane> consumer) {
        fpChildNodes.getChildren().forEach(node -> {
            if (node instanceof ZkNodePane) {
                consumer.accept((ZkNodePane) node);
            }
        });
    }

    private void refreshNodeData(Object nodeData) {
        runUIThread(() -> {
            txtNodeData.setText(nodeData == null ? null : String.valueOf(nodeData));
            txtNodeData.setPromptText(nodeData == null ? "(无数据)" : "");
            txtNodeData.setEditable(nodeData == null || nodeData instanceof CharSequence);
        });
    }

    private void refreshNodeMata(Stat nodeMetadata) {
        runUIThread(() -> {
            txtAversion.setText(String.valueOf(nodeMetadata.getVersion()));
            txtCtime.setText(format(nodeMetadata.getCtime()));
            txtCversion.setText(String.valueOf(nodeMetadata.getCversion()));
            txtCzxid.setText(String.valueOf(nodeMetadata.getCzxid()));
            txtDataLength.setText(String.valueOf(nodeMetadata.getDataLength()));
            txtEphemeralOwner.setText(String.valueOf(nodeMetadata.getEphemeralOwner()));
            txtMtime.setText(format(nodeMetadata.getMtime()));
            txtMzxid.setText(String.valueOf(nodeMetadata.getMzxid()));
            txtNumChildren.setText(String.valueOf(nodeMetadata.getNumChildren()));
            txtVersion.setText(String.valueOf(nodeMetadata.getVersion()));
        });
    }

    private void updateLocation() {
        fpLocation.getChildren().clear();
        fpLocation.getChildren().add(
            createLocationLink("/", Collections.emptyList())
        );

        List<String> path = new ArrayList<>();

        service.getCurrentLocation().forEach(item -> {
            path.add(item);
            fpLocation.getChildren().add(iconLabel(FontAwesomeIcon.PLAY, "8pt", "#AAAAAA"));
            fpLocation.getChildren().add(createLocationLink(item, new ArrayList<>(path)));
        });

        fpLocation.getChildren().add(iconLabel(FontAwesomeIcon.PLAY, "8pt", "#AAAAAA"));
        fpLocation.getChildren().add(iconLink(FontAwesomeIcon.PLUS_CIRCLE, "#55DD44", this::newNode));
    }

    private void newNode() {
        newNode(null);
    }

    private void newNode(String parent) {
        NewNodeDialog newNodeDialog = new NewNodeDialog(ZooKeeperToolApp.getPrimaryStage());
        newNodeDialog.showAndWait();

        if (newNodeDialog.isOk()) {
            String nodeName = newNodeDialog.getNodeName().trim();
            if (nodeName.isEmpty()) {
                AlertDialog.error("错误", "名字不能为空");
                return;
            } else if (nodeName.contains("/")) {
                AlertDialog.error("错误", "名字不能包含 '/'");
                return;
            }

            service.addNode(nodeName, parent, newNodeDialog.isPersistent(), newNodeDialog.isSequential());
        }
    }

    private Hyperlink createLocationLink(String text, List<String> location) {
        Hyperlink link = new Hyperlink(text);
        link.setOnAction(event -> service.setCurrentLocation(location));
        return link;
    }

    private void refreshNodes() {
        fpChildNodes.getChildren().clear();
        txtSearch.setText(null);

        service.listChildren().forEach(item -> {
            ZkNodePane zkNodePane = new ZkNodePane(service.getCurrentLocation(), item);
            zkNodePane.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
                ZkNodePane pane = (ZkNodePane) event.getSource();
                Listeners.publish(new ZkNodeSelectedEvent(pane));
                if (event.getClickCount() == 2 && pane.getZkNode().getChildrenCount() > 0) {
                    service.goInto(item.getName());
                }
            });
            NodeUtils.setManaged(zkNodePane);
            fpChildNodes.getChildren().add(zkNodePane);
        });

        Listeners.publish(new ZkNodeUnselectedEvent());
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
        if (currentSelectedNode != null) {
            String fullName = currentSelectedNode.getZkNode().getFullName();
            this.service.saveData(fullName, txtNodeData.getText());
        }
    }

    public void deleteNodeData() {
        if (currentSelectedNode == null) {
            return;
        }

        String currentNodeName = this.service.getCurrentNodeName();
        if (AlertDialog.confirmYesNo("删除节点数据", "确认要删除节点“" + currentNodeName + "”的数据吗？")) {
            String fullName = currentSelectedNode.getZkNode().getFullName();
            this.service.deleteNodeData(fullName);
        }
    }
}