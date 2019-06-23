package com.hyd.jfappsdemo.app1;

import com.hyd.jfapps.appbase.AppInfo;
import com.hyd.jfapps.appbase.JfappsApp;
import javafx.scene.Parent;

@AppInfo(name = "第一个 App 示例")
public class MainApp extends JfappsApp {

    @Override
    public Parent getRoot() throws Exception {
        return fxmlLoader("/app.fxml").load();
    }
}
