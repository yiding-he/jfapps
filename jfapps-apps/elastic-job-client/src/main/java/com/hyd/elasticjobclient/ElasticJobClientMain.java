package com.hyd.elasticjobclient;

import static com.hyd.elasticjobclient.Icons.icon;
import static com.hyd.fx.builders.MenuBuilder.menu;
import static com.hyd.fx.builders.MenuBuilder.menuItem;
import static de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon.BOOK;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class ElasticJobClientMain extends Application {

    public static void main(String[] args) {
        Application.launch(ElasticJobClientMain.class);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Elastic Job 客户端");
        primaryStage.setScene(new Scene(root(), 400, 300));
        primaryStage.show();
    }

    private BorderPane root() {
        return new BorderPane(registryTabs(), menuBar(), null, null, null);
    }

    private TabPane registryTabs() {
        return new TabPane();
    }

    private MenuBar menuBar() {
        return new MenuBar(
            menu("注册中心",
                menuItem("打开注册中心...", icon(BOOK), this::openRegistry)
            )
        );
    }

    private void openRegistry() {
        new RegistryDialog().showAndWait();
    }
}
