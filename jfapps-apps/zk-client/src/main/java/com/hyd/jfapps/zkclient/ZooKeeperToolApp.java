package com.hyd.jfapps.zkclient;

import com.hyd.jfapps.appbase.AppCategory;
import com.hyd.jfapps.appbase.AppInfo;
import com.hyd.jfapps.appbase.JfappsApp;
import com.hyd.jfapps.appbase.JfappsAppLauncher;
import javafx.scene.Parent;
import javafx.stage.Stage;

@AppInfo(
    name = "Zookeeper 工具",
    author = "xJavaFxTool",
    url = "https://github.com/864381832/xJavaFxTool",
    category = AppCategory.DATABASE
)
public class ZooKeeperToolApp extends JfappsApp {

    private static Stage primaryStage;

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        JfappsAppLauncher.launchApp(ZooKeeperToolApp.class);
    }

    @Override
    public Parent getRoot() throws Exception {
        ZooKeeperToolApp.primaryStage = this.appContext.getPrimaryStage();
        return fxmlLoader("/ZookeeperTool.fxml").load();
    }
}
