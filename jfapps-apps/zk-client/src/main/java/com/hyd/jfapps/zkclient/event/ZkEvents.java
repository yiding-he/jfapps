package com.hyd.jfapps.zkclient.event;

import com.hyd.jfapps.zkclient.node.ZkNodePane;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class ZkEvents {

    @Data
    public static class ZkNodeUnselectedEvent {

    }

    @RequiredArgsConstructor
    public static class ZkNodeSelectedEvent {

        @Getter
        private final ZkNodePane zkNodePane;

    }

    public static class ZkDisconnectedEvent {

    }

    @Data
    @RequiredArgsConstructor
    public static class ZkConnectingEvent {

        private final String address;
    }

    public static class ZkConnectedEvent {

    }

    public static class ZkConnectFailedEvent {

    }
}
