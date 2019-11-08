package com.hyd.elasticjobclient;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;

public class LayoutContext<E extends Node> {

    protected E node;

    public E getNode() {
        return node;
    }

    //////////////////////////////////////////////////////////////

    public static HBoxContext hbox(Pos alignment) {
        return new HBoxContext(alignment);
    }

    public static class HBoxContext extends LayoutContext<HBox> {

        public HBoxContext(Pos alignment) {
            node = new HBox();
            node.setAlignment(alignment);
        }

        public HBoxContext spacing(double spacing) {
            node.setSpacing(spacing);
            return this;
        }
    }
}
