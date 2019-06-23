package com.hyd.jfappsdemo.app1;

import com.hyd.jfapps.appbase.AppContext;
import com.hyd.jfapps.appbase.AppInfo;
import com.hyd.jfapps.appbase.JfappsApp;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

@AppInfo(name = "第一个 App 示例")
public class MainApp extends JfappsApp {

    public static AppContext APP_CONTEXT;

    @Override
    public void initialize() {
        MainApp.APP_CONTEXT = this.appContext;
    }

    @Override
    public Parent getRoot() throws Exception {
        FXMLLoader fxmlLoader = fxmlLoader("/app.fxml");
        return fxmlLoader.load();
    }
}
