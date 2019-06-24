package com.hyd.jfapps.ftpserver;

import com.hyd.jfapps.appbase.AppInfo;
import com.hyd.jfapps.appbase.JfappsApp;
import com.hyd.jfapps.appbase.JfappsAppLauncher;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

/**
 * 简易 FTP 服务器，源码来自 https://github.com/864381832/xJavaFxTool
 */
@AppInfo(name = "FTP 服务器")
public class FtpServerApp extends JfappsApp {

    private FtpServerController controller;

    public static void main(String[] args) {
        JfappsAppLauncher.launchApp(FtpServerApp.class);
    }

    @Override
    public void initialize() {
        super.initialize();
    }

    @Override
    public Parent getRoot() throws Exception {
        FXMLLoader fxmlLoader = fxmlLoader("/FtpServer.fxml");
        Parent parent = fxmlLoader.load();
        this.controller = fxmlLoader.getController();
        return parent;
    }

    @Override
    public void onCloseRequest() {
        this.controller.ensureServiceClosed();
    }
}
