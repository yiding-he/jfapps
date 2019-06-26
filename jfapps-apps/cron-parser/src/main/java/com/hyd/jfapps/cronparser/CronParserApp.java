package com.hyd.jfapps.cronparser;

import com.hyd.jfapps.appbase.AppCategory;
import com.hyd.jfapps.appbase.AppInfo;
import com.hyd.jfapps.appbase.JfappsApp;
import com.hyd.jfapps.appbase.JfappsAppLauncher;
import javafx.scene.Parent;

@AppInfo(
        name = "Cron表达式生成",
        author = "xJavaFxTool",
        url = "https://github.com/864381832/xJavaFxTool",
        category = AppCategory.SYSTEM
)
public class CronParserApp extends JfappsApp {

    public static void main(String[] args) {
        JfappsAppLauncher.launchApp(CronParserApp.class);
    }

    @Override
    public Parent getRoot() throws Exception {
        return fxmlLoader("/CronExpBuilder.fxml").load();
    }
}
