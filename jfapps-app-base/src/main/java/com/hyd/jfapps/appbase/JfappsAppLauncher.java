package com.hyd.jfapps.appbase;


import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * 用于帮助 JfappsApp 类调试运行的启动类
 */
public class JfappsAppLauncher extends Application {

    private static Class<? extends JfappsApp> appClass;

    public static void launchApp(Class<? extends JfappsApp> appClass) {
        JfappsAppLauncher.appClass = appClass;
        launch(JfappsAppLauncher.class);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        JfappsApp app = appClass.newInstance();
        app.setAppContext(new AppContextStub());
        app.setClassLoader(JfappsAppLauncher.class.getClassLoader());
        app.setGlobalContext(new GlobalContext());

        app.initialize();

        InputStream logo = JfappsAppLauncher.class.getResourceAsStream("/logo.png");
        if (logo != null) {
            primaryStage.getIcons().add(new Image(logo));
        }

        primaryStage.setTitle(app.getAppName());
        primaryStage.setScene(new Scene(app.getRoot()));
        primaryStage.show();
    }

    //////////////////////////////////////////////////////////////

    private static class AppContextStub implements AppContext {

        private Map<String, String> config = new HashMap<>();

        @Override
        public Image getIcon() {
            return null;
        }

        @Override
        public void setConfiguration(String configName, String configValue) {
            config.put(configName, configValue);
        }

        @Override
        public String getConfiguration(String configName) {
            return config.get(configName);
        }
    }
}
