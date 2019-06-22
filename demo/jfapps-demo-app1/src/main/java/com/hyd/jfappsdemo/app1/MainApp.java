package com.hyd.jfappsdemo.app1;

import com.hyd.jfapps.appbase.JfappsApp;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

public class MainApp implements JfappsApp {

    @Override
    public Parent getRoot() {
        return new BorderPane(new Label("Hello, this is demo 1"));
    }
}
