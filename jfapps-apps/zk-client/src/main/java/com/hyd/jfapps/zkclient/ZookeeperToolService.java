package com.hyd.jfapps.zkclient;

import static com.hyd.jfapps.zkclient.ZkUtils.concatPathWithSlash;
import static org.apache.commons.lang3.StringUtils.appendIfMissing;

import com.hyd.fx.components.LazyLoadingTreeItem;
import com.hyd.fx.concurrency.BackgroundTask;
import com.hyd.fx.dialog.AlertDialog;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javafx.application.Platform;
import javafx.scene.control.TreeItem;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.zookeeper.ZooDefs.Perms;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;

/**
 * @ClassName: ZookeeperToolService
 * @Description: Zookeeper工具
 * @author: xufeng
 * @date: 2019/4/8 17:00
 */

@Getter
@Setter
@Slf4j
public class ZookeeperToolService {

    public ZookeeperToolController controller;

    private ZkClient zkClient = null;

    private Map<String, IZkChildListener> childListeners = new HashMap<>();

    private Map<String, IZkDataListener> dataListeners = new HashMap<>();

    public ZookeeperToolService(ZookeeperToolController controller) {
        this.controller = controller;
    }

    public void initZkClient(String serverAddress, int timeoutMillis) {
        zkClient = new ZkClient(serverAddress, 3600000, timeoutMillis, new MyZkSerializer());
    }

    private void addNodeTree(String path, TreeItem<String> parent) {
        List<String> list = getZkChildren(getNodePath(parent));
        for (String name : list) {
            TreeItem<String> child = new TreeItem<>(name);
            Platform.runLater(() -> parent.getChildren().add(child));
            addNodeTree(appendIfMissing(path, "/", "/") + name, child);
        }
    }

    private List<String> getZkChildren(String path) {
        try {
            List<String> children = zkClient.getChildren(path);
            Collections.sort(children);
            return children;
        } catch (Exception e) {
            log.error("", e);
            return Collections.emptyList();
        }
    }

    private String getNodePath(TreeItem<String> selectedItem) {
        StringBuilder stringBuffer = new StringBuilder();
        stringBuffer.append(selectedItem.getValue());
        TreeItem<String> indexItem = selectedItem.getParent();
        while (indexItem != null) {
            stringBuffer.insert(0, appendIfMissing(indexItem.getValue(), "/", "/"));
            indexItem = indexItem.getParent();
        }
        return stringBuffer.toString();
    }

    public void nodeSelectionChanged(TreeItem<String> selectedItem) {
        String nodePath = this.getNodePath(selectedItem);
        if (!zkClient.exists(nodePath)) {
            AlertDialog.error("节点不存在", "节点 '" + nodePath + "' 不存在");
            return;
        }
        controller.getNodeDataValueTextArea().setText(zkClient.readData(nodePath));
        Map.Entry<List<ACL>, Stat> aclsEntry = zkClient.getAcl(nodePath);
        Stat stat = aclsEntry.getValue();
        controller.getA_VERSIONTextField().setText("" + stat.getAversion());
        controller.getC_TIMETextField().setText(DateFormatUtils.format(stat.getCtime(), "yyyy-MM-dd'T'HH:mm:ss.SSS z"));
        controller.getC_VERSIONTextField().setText("" + stat.getCversion());
        controller.getCZXIDTextField().setText("0x" + Long.toHexString(stat.getCzxid()));
        controller.getDATA_LENGTHTextField().setText("" + stat.getDataLength());
        controller.getEPHEMERAL_OWNERTextField().setText("0x" + Long.toHexString(stat.getEphemeralOwner()));
        controller.getM_TIMETextField().setText(DateFormatUtils.format(stat.getMtime(), "yyyy-MM-dd'T'HH:mm:ss.SSS z"));
        controller.getMZXIDTextField().setText("0x" + Long.toHexString(stat.getMzxid()));
        controller.getNUM_CHILDRENTextField().setText("" + stat.getNumChildren());
        controller.getPZXIDTextField().setText("0x" + Long.toHexString(stat.getPzxid()));
        controller.getVERSIONTextField().setText("" + stat.getVersion());

        List<ACL> acls = aclsEntry.getKey();
        for (ACL acl : acls) {
            Map<String, String> aclMap = new LinkedHashMap<String, String>();
            controller.getAclSchemeTextField().setText(acl.getId().getScheme());
            controller.getAclIdTextField().setText(acl.getId().getId());
            StringBuilder sb = new StringBuilder();
            int perms = acl.getPerms();
            if ((perms & Perms.READ) == Perms.READ) {
                sb.append("Read");
            }
            if ((perms & Perms.WRITE) == Perms.WRITE) {
                sb.append(", Write");
            }
            if ((perms & Perms.CREATE) == Perms.CREATE) {
                sb.append(", Create");
            }
            if ((perms & Perms.DELETE) == Perms.DELETE) {
                sb.append(", Delete");
            }
            if ((perms & Perms.ADMIN) == Perms.ADMIN) {
                sb.append(", Admin");
            }
            controller.getAclPermissionsTextField().setText(sb.toString());
        }
    }

    public void disconnectOnAction() {
        if (zkClient != null) {
            zkClient.close();
            zkClient = null;
        }
        controller.getNodeTreeView().getRoot().getChildren().clear();
    }

    public void deleteNodeOnAction() {
        TreeItem<String> selectedItem = controller.getNodeTreeView().getSelectionModel().getSelectedItem();
        if (!AlertDialog.confirmOkCancel("删除节点",
            "确认要删除节点 '" + selectedItem.getValue() + "' 吗？该操作不可恢复！")) {
            return;
        }
        String nodePath = getNodePath(selectedItem);
        zkClient.deleteRecursive(nodePath);
        selectedItem.getParent().getChildren().remove(selectedItem);
    }

    public void addNodeOnAction() {
        String nodeName = AlertDialog.input("添加节点", "请输入节点名称：", false);
        if (StringUtils.isEmpty(nodeName)) {
            return;
        }
        TreeItem<String> selectedItem = controller.getNodeTreeView().getSelectionModel().getSelectedItem();
        String nodePath = this.getNodePath(selectedItem);
        zkClient.createPersistent(appendIfMissing(nodePath, "/", "/") + nodeName);

        ((LazyLoadingTreeItem<String>) selectedItem).addChild(nodeName);
    }

    public void copyNodeOnAction() {
        String nodeName = AlertDialog.input("复制节点", "请输入节点新名称：", false);

        if (nodeName == null) {
            return;
        } else if (StringUtils.isEmpty(nodeName)) {
            AlertDialog.error("错误", "名称不能为空");
            return;
        }

        TreeItem<String> selectedItem = controller.getNodeTreeView().getSelectionModel().getSelectedItem();
        if (selectedItem.getParent() == null) {
            AlertDialog.error("错误", "不能复制根节点。");
            return;
        }

        if (selectedItem.getParent().getChildren().stream().anyMatch(item -> item.getValue().equals(nodeName))) {
            AlertDialog.error("错误", "名为 '" + nodeName + "' 的节点已经存在。");
            return;
        }

        String nodeParent = getNodePath(selectedItem.getParent());
        copyNodeAsync(
            concatPathWithSlash(nodeParent, selectedItem.getValue()),
            concatPathWithSlash(nodeParent, nodeName),
            selectedItem.getParent()
        );
    }

    private void copyNodeAsync(String path, String newPath, TreeItem<String> parentNode) {
        String newPathName = newPath.substring(newPath.lastIndexOf("/") + 1);
        BackgroundTask
            .runTask(() -> copyNode(path, newPath))
            .whenBeforeStart(() -> controller.nodeTreeView.setDisable(true))
            .whenTaskSuccess(() -> {
                LazyLoadingTreeItem<String> parent = (LazyLoadingTreeItem<String>) parentNode;
                parent.addChild(newPathName);
                parent.getChildren().sort(Comparator.comparing(TreeItem::getValue));
            })
            .whenTaskFinish(() -> controller.nodeTreeView.setDisable(false))
            .start();
    }

    private void copyNode(String path, String newPath) {

        zkClient.createPersistent(
            newPath, zkClient.readData(path), zkClient.getAcl(path).getKey());

        for (String childPath : getZkChildren(path)) {
            copyNode(concatPathWithSlash(path, childPath), concatPathWithSlash(newPath, childPath));
        }
    }

    public void nodeDataSaveOnAction() {
        TreeItem<String> selectedItem = controller.getNodeTreeView().getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            AlertDialog.error("未选中节点");
            return;
        }
        String nodePath = this.getNodePath(selectedItem);
        zkClient.writeData(nodePath, controller.getNodeDataValueTextArea().getText());
    }

    public void addNodeNotify() {//添加节点通知
        TreeItem<String> selectedItem = controller.getNodeTreeView().getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            AlertDialog.error("未选中节点");
            return;
        }
        String nodePath = this.getNodePath(selectedItem);
        if (childListeners.containsKey(nodePath)) {
            AlertDialog.error("该节点已经添加通知！");
            return;
        }
        IZkChildListener childListener = (parentPath, currentChilds) ->
            AlertDialog.error("节点Child改变了", "Path:" + parentPath + "\r\n 子节点：" + currentChilds.toString());
        zkClient.subscribeChildChanges(nodePath, childListener);
        childListeners.put(nodePath, childListener);
        IZkDataListener dataListener = new IZkDataListener() {
            @Override
            public void handleDataChange(String dataPath, Object data) throws Exception {
                AlertDialog.error("节点Data改变了", "Path:" + dataPath + "\r\n 新数据：" + data.toString());
            }

            @Override
            public void handleDataDeleted(String dataPath) throws Exception {
                AlertDialog.error("节点删除了", "Path:" + dataPath);
            }
        };
        zkClient.subscribeDataChanges(nodePath, dataListener);
        dataListeners.put(nodePath, dataListener);
        AlertDialog.error("该节点添加通知成功！");
    }

    public void removeNodeNotify() {//移除节点通知
        TreeItem<String> selectedItem = controller.getNodeTreeView().getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            AlertDialog.error("未选中节点");
            return;
        }
        String nodePath = this.getNodePath(selectedItem);
        zkClient.unsubscribeChildChanges(nodePath, childListeners.remove(nodePath));
        zkClient.unsubscribeDataChanges(nodePath, dataListeners.remove(nodePath));
        AlertDialog.error("该节点通知成功移除！");
    }

    public void setupTree() {
        controller.nodeTreeView.setRoot(new LazyLoadingTreeItem<>(
            "/", parent -> getZkChildren(getNodePath(parent)), "加载中..."
        ));
    }

    private static class MyZkSerializer implements ZkSerializer {

        @Override
        public byte[] serialize(Object data) throws ZkMarshallingError {
            return String.valueOf(data).getBytes(StandardCharsets.UTF_8);
        }

        @Override
        public Object deserialize(byte[] bytes) throws ZkMarshallingError {
            return new String(bytes, StandardCharsets.UTF_8);
        }
    }
}