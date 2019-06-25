package com.hyd.jfapps.regextester;

import com.hyd.jfapps.appbase.AppCategory;
import com.hyd.jfapps.appbase.AppInfo;
import com.hyd.jfapps.appbase.JfappsApp;
import com.hyd.jfapps.appbase.JfappsAppLauncher;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

/**
 * 正则表达式测试工具，源码来自 https://github.com/864381832/xJavaFxTool
 */
@AppInfo(
    name = "测试正则表达式",
    author = "xJavaFxTool",
    url = "https://github.com/864381832/xJavaFxTool",
    category = AppCategory.TEXT
)
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
