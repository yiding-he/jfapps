package com.hyd.jfapps.regextester;

import com.hyd.jfapps.appbase.AppInfo;
import com.hyd.jfapps.appbase.JfappsApp;
import com.hyd.jfapps.appbase.JfappsAppLauncher;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

/**
 * 正则表达式测试工具，源码来自 https://github.com/864381832/xJavaFxTool
 */
@AppInfo(name = "正则表达式测试工具")
public class RegexTesterApp extends JfappsApp {

    public static void main(String[] args) {
        JfappsAppLauncher.launchApp(RegexTesterApp.class);
    }

    @Override
    public Parent getRoot() throws Exception {
        FXMLLoader fxmlLoader = fxmlLoader("/RegexTester.fxml");
        return fxmlLoader.load();
    }
}
