package com.hyd.jfapps.zkclient;

import static com.hyd.fx.app.AppThread.runUIThread;
import static com.hyd.jfapps.zkclient.FxUtil.iconLabel;
import static com.hyd.jfapps.zkclient.FxUtil.iconLink;

import com.hyd.fx.NodeUtils;
import com.hyd.fx.concurrency.BackgroundTask;
import com.hyd.fx.dialog.AlertDialog;
import com.hyd.jfapps.zkclient.config.Config;
import com.hyd.jfapps.zkclient.config.UserPreferences;
import com.hyd.jfapps.zkclient.dialog.NewNodeDialog;
import com.hyd.jfapps.zkclient.event.AddNodeRequest;
import com.hyd.jfapps.zkclient.event.ChildrenChangedEvent;
import com.hyd.jfapps.zkclient.event.DeleteNodeRequest;
import com.hyd.jfapps.zkclient.event.Listeners;
import com.hyd.jfapps.zkclient.event.LocationChangedEvent;
import com.hyd.jfapps.zkclient.event.NodeDataChangedEvent;
import com.hyd.jfapps.zkclient.event.ZkConnectedEvent;
import com.hyd.jfapps.zkclient.event.ZkConnectingEvent;
import com.hyd.jfapps.zkclient.event.ZkDisconnectedEvent;
import com.hyd.jfapps.zkclient.event.ZkNodeSelectedEvent;
import com.hyd.jfapps.zkclient.event.ZkNodeUnselectedEvent;
import com.hyd.jfapps.zkclient.node.ZkNodePane;
import com.hyd.jfapps.zkclient.zk.ZkService;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
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

    private static String format(long timestamp) {
        return DATE_FORMAT.get().format(new Date(timestamp));
    }

    public ProgressBar prgProcessing;

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

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> forEachNodePane(nodeDataPane -> {
            boolean match = nodeDataPane
                .getZkNode().getName().toLowerCase()
                .contains(txtSearch.getText().trim().toLowerCase());
            nodeDataPane.setVisible(match);
        }));

        Listeners.addListener(ZkConnectedEvent.class, event -> runUIThread(() -> {
            lblStatus.setText("服务器已连接。");
            btnConnect.setText("断开连接");
            btnConnect.setDisable(false);
            prgProcessing.setVisible(false);
            mainPane.setDisable(false);
            service.setCurrentLocation(Collections.emptyList());
        }));

        Listeners.addListener(ZkDisconnectedEvent.class, event -> runUIThread(() -> {
            lblStatus.setText("尚未连接服务器。");
            btnConnect.setText("连接服务器");
            prgProcessing.setVisible(false);
            fpChildNodes.getChildren().clear();
            mainPane.setDisable(true);
        }));

        Listeners.addListener(ZkConnectingEvent.class, event -> runUIThread(() -> {
            lblStatus.setText("正在连接 " + event.getAddress() + "...");
            prgProcessing.setVisible(true);
            btnConnect.setDisable(true);
        }));

        Listeners.addListener(LocationChangedEvent.class, event -> {
            log.info("location -> {}", service.getCurrentLocation());
            service.watch();
            updateLocation();
            refreshNodes();
        });

        Listeners.addListener(ZkNodeUnselectedEvent.class, event -> nodeDataPane.setDisable(true));

        Listeners.addListener(ZkNodeSelectedEvent.class, event -> forEachNodePane(node -> {
            if (node == event.getZkNodePane()) {
                FxUtil.switchClass(node, "zk-node-unselected", "zk-node-selected");
            } else {
                FxUtil.switchClass(node, "zk-node-selected", "zk-node-unselected");
            }
        }));

        Listeners.addListener(ZkNodeSelectedEvent.class, event -> {
            currentSelectedNode = event.getZkNodePane();
            nodeDataPane.setDisable(false);

            String name = currentSelectedNode.getZkNode().getName();
            refreshNodeData(service.getNodeData(name));
            refreshNodeMata(service.getNodeMetadata(name));
        });

        Listeners.addListener(ZkNodeSelectedEvent.class,
            event -> service.watchNode(event.getZkNodePane().getZkNode().getFullName()));

        Listeners.addListener(NodeDataChangedEvent.class, event -> refreshNodeData(event.getData()));

        Listeners.addListener(ChildrenChangedEvent.class, event -> runUIThread(this::refreshNodes));

        Listeners.addListener(AddNodeRequest.class, event -> newNode(event.getParent()));

        Listeners.addListener(DeleteNodeRequest.class, event -> service.deleteNode(event.getNodePath()));
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
        Listeners.publish(new ZkNodeUnselectedEvent());

        txtSearch.setText(null);
        lblStatus.setText("正在查询节点...");
        prgProcessing.setVisible(true);
        AtomicInteger nodeCounter = new AtomicInteger();

        BackgroundTask.runTask(() -> {
            service.listChildren().forEach(item -> {
                nodeCounter.incrementAndGet();
                ZkNodePane zkNodePane = new ZkNodePane(service.getCurrentLocation(), item);
                zkNodePane.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
                    ZkNodePane pane = (ZkNodePane) event.getSource();
                    Listeners.publish(new ZkNodeSelectedEvent(pane));
                    if (event.getClickCount() == 2 && pane.getZkNode().getChildrenCount() > 0) {
                        service.goInto(item.getName());
                    }
                });
                runUIThread(() -> {
                    NodeUtils.setManaged(zkNodePane);
                    fpChildNodes.getChildren().add(zkNodePane);
                });
            });

        }).whenTaskFail(e -> {
            AlertDialog.error("打开节点失败", e);
        }).whenTaskFinish(() -> {
            prgProcessing.setVisible(false);
            lblStatus.setText("节点查询完毕，共 " + nodeCounter.get() + " 个节点。");
        }).start();

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

            BackgroundTask.runTask(
                () -> service.connect(
                    comboServerAddr.getValue(),
                    comboConnTimeout.getValue() * 1000
                )
            ).whenTaskFail(e -> {
                AlertDialog.error("连接失败", e);
                Listeners.publish(new ZkDisconnectedEvent());
            }).whenBeforeStart(
                () -> Listeners.publish(new ZkConnectingEvent(comboServerAddr.getValue()))
            ).whenTaskSuccess(
                () -> Listeners.publish(new ZkConnectedEvent())
            ).start();
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