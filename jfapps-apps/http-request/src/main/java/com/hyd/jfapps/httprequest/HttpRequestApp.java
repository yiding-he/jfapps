package com.hyd.jfapps.httprequest;

import com.hyd.jfapps.appbase.AppCategory;
import com.hyd.jfapps.appbase.AppInfo;
import com.hyd.jfapps.appbase.JfappsApp;
import com.hyd.jfapps.appbase.JfappsAppLauncher;
import javafx.application.HostServices;
import javafx.scene.Parent;

@AppInfo(name = "HTTP 调试工具",
    author = "xJavaFxTool",
    url = "https://github.com/864381832/xJavaFxTool",
    category = AppCategory.RPC
)
public class HttpRequestApp extends JfappsApp {

    private static HostServices hostServices;

    public static void openUrl(String url) {
        hostServices.showDocument(url);
    }

    public static void main(String[] args) {
        JfappsAppLauncher.launchApp(HttpRequestApp.class);
    }

    @Override
    public Parent getRoot() throws Exception {
        hostServices = appContext.getHostServices();
        return fxmlLoader("/HttpTool.fxml").load();
    }
}
