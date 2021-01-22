package com.hyd.redisfx;

import com.hyd.jfapps.appbase.AppCategory;
import com.hyd.jfapps.appbase.AppInfo;
import com.hyd.jfapps.appbase.JfappsApp;
import com.hyd.jfapps.appbase.JfappsAppLauncher;
import com.hyd.redisfx.controllers.MainController;
import com.hyd.redisfx.jedis.JedisManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

@AppInfo(
    name = "Redis 客户端",
    author = "yiding-he",
    url = "https://github.com/yiding-he/redisfx",
    category = AppCategory.DATABASE
)
public class RedisFxApp extends JfappsApp {

    public static void main(String[] args) {
        JfappsAppLauncher.launchApp(RedisFxApp.class);
    }

    public RedisFxApp() {
        setOnCloseRequest(JedisManager::shutdown);
    }

    @Override
    public Parent getRoot() throws Exception {
        Fx.setFxmlLoaderSupplier(this::fxmlLoader);
        FXMLLoader fxmlLoader = Fx.getFxmlLoader("/fxml/Main.fxml");
        BorderPane mainPane = fxmlLoader.load();

        MainController mainController = fxmlLoader.getController();
        mainController.setPrimaryStage(appContext.getPrimaryStage());

        return mainPane;
    }

}
