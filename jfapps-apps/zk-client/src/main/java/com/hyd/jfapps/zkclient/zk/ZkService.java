package com.hyd.jfapps.zkclient.zk;

import com.hyd.fx.utils.Nullable;
import com.hyd.jfapps.zkclient.event.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;
import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.CreateMode;

@Slf4j
public class ZkService {

    private ZkClient zkClient = null;

    private List<String> currentLocation = new ArrayList<>();

    private String appendCurrentLocation(String path) {
        List<String> location = new ArrayList<>(currentLocation);
        location.add(path);
        return "/" + String.join("/", location);
    }

    public List<String> getCurrentLocation() {
        return Collections.unmodifiableList(currentLocation);
    }

    public String getCurrentLocationString() {
        return "/" + String.join("/", currentLocation);
    }

    public List<ZkNode> listChildren() {

        String currentPath = getCurrentLocationString();
        List<String> children = zkClient.getChildren(currentPath);
        Collections.sort(children);

        return children.stream().map(name -> {
            ZkNode zkNode = new ZkNode();
            zkNode.setName(name);
            zkNode.setFullName(StringUtils.removeEnd(currentPath, "/") + "/" + name);
            zkNode.setChildrenCount(zkClient.countChildren(appendCurrentLocation(name)));
            return zkNode;
        }).collect(Collectors.toList());
    }

    public void connect(String serverAddress, int timeoutMillis) {
        zkClient = new ZkClient(serverAddress, 3600000, timeoutMillis, new MyZkSerializer());
        Listeners.publish(new ZkConnectedEvent());
    }

    public boolean isConnected() {
        return this.zkClient != null;
    }

    public void disconnect() {
        if (zkClient != null) {
            zkClient.close();
            zkClient = null;
            Listeners.publish(new ZkDisconnectedEvent());
        }
    }

    public void goInto(String path) {
        List<String> oldLocation = new ArrayList<>(this.currentLocation);
        currentLocation.add(path);
        Listeners.publish(new LocationChangedEvent(oldLocation, currentLocation));
    }

    public void setCurrentLocation(List<String> location) {
        List<String> oldLocation = this.currentLocation;
        this.currentLocation = new ArrayList<>(location);
        Listeners.publish(new LocationChangedEvent(oldLocation, location));
    }

    // 监视当前节点，当节点数据或子节点有变化时刷新界面
    public void watch() {
        this.zkClient.unsubscribeAll();
        this.zkClient.subscribeChildChanges(getCurrentLocationString(), (parentPath, currentChildren) -> {
            Listeners.publish(new ChildrenChangedEvent());
        });

        this.zkClient.subscribeDataChanges(getCurrentLocationString(), new IZkDataListener() {
            @Override
            public void handleDataChange(String dataPath, Object data) throws Exception {
                Listeners.publish(new NodeDataChangedEvent(data));
            }

            @Override
            public void handleDataDeleted(String dataPath) throws Exception {
                Listeners.publish(new NodeDataChangedEvent(null));
            }
        });
    }

    public Object getNodeData(String nodeName) {
        String path = appendCurrentLocation(nodeName);
        return this.zkClient.readData(path);
    }

    public void addNode(String nodeName, @Nullable String parent, boolean persistent, boolean sequential) {
        String path = parent == null ? appendCurrentLocation(nodeName) : (parent + "/" + nodeName);
        CreateMode createMode = persistent ?
            (sequential ? CreateMode.PERSISTENT_SEQUENTIAL : CreateMode.PERSISTENT) :
            (sequential ? CreateMode.EPHEMERAL_SEQUENTIAL : CreateMode.EPHEMERAL);
        this.zkClient.create(path, null, createMode);
        Listeners.publish(new LocationChangedEvent(currentLocation, currentLocation));
    }

    public void deleteNode(String nodePath) {
        this.zkClient.deleteRecursive(nodePath);
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