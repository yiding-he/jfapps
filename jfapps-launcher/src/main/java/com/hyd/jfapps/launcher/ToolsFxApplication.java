package com.hyd.jfapps.launcher;

import com.hyd.jfapps.appbase.Icons;
import com.hyd.jfapps.appbase.JfappsApp;
import com.hyd.jfapps.launcher.appmanager.AppContainer;
import com.hyd.jfapps.launcher.appmanager.AppManager;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

import java.util.List;

public class ToolsFxApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Icons.setStageIcon(primaryStage);

        TabPane root = new TabPane();
        loadApps(root);

        primaryStage.setTitle("Hydrogen Tools Fx");
        primaryStage.setScene(new Scene(root, 400, 300));
        primaryStage.show();
    }

    private void loadApps(TabPane root) {
        List<AppContainer> containers = AppManager.getAppContainers();

        for (AppContainer container : containers) {
            JfappsApp app =  container.getAppInstance();
            root.getTabs().add(createAppTab(app));
        }
    }

    private Tab createAppTab(JfappsApp app) {
        Tab tab = new Tab();
        tab.setText(app.getClass().getSimpleName());
        tab.setContent(app.getRoot());
        tab.setClosable(false);
        return tab;
    }

}
