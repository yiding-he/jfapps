package com.hyd.jfapps.zkclient.zk;

import com.hyd.jfapps.zkclient.event.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;

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

    public void watch() {
        this.zkClient.unsubscribeAll();
        this.zkClient.subscribeChildChanges(getCurrentLocationString(), (parentPath, currentChildren) -> {
            Listeners.publish(new ChildrenChangedEvent());
        });
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