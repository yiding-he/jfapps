package com.hyd.jfapps.textprocess;

import com.hyd.jfapps.appbase.JfappsApp;
import com.hyd.jfapps.appbase.JfappsAppLauncher;
import javafx.scene.Parent;

public class JfappsTextProcessApp extends JfappsApp {

    public static void main(String[] args) {
        JfappsAppLauncher.launchApp(JfappsTextProcessApp.class);
    }

    @Override
    public Parent getRoot() throws Exception {
        return fxmlLoader("/main.fxml").load();
    }
}
