package com.hyd.jfapps.zkclient.event;

import lombok.AllArgsConstructor;
import lombok.Data;

public class NodeEvents {

    @Data
    @AllArgsConstructor
    public static class NodeDataChangedEvent {

        private String fullPath;

        private Object data;
    }

    @Data
    @AllArgsConstructor
    public static class DeleteNodeRequest {

        private String nodePath;
    }

    public static class ChildrenChangedEvent {

    }

    @Data
    public static class AddNodeRequest {

        private final String parent;

        public AddNodeRequest(String parent) {
            this.parent = parent;
        }
    }
}
