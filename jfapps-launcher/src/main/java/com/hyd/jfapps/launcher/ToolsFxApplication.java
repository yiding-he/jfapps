package com.hyd.jfapps.launcher;

import com.hyd.jfapps.appbase.JfappsApp;
import com.hyd.jfapps.launcher.appmanager.AppContainer;
import com.hyd.jfapps.launcher.appmanager.AppManager;
import com.sun.deploy.uitoolkit.impl.fx.HostServicesFactory;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;

@Slf4j
public class ToolsFxApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        Icons.setStageIcon(primaryStage);
        AppManager.GLOBAL_CONTEXT.put("primaryStage", primaryStage);

        TabPane root = new TabPane();
        root.getTabs().add(mainTab(root));

        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
        double width = Math.max(visualBounds.getWidth() - 200, 1000);
        double height = Math.max(visualBounds.getHeight() - 200, 600);

        primaryStage.setTitle("小工具集合");
        primaryStage.setScene(new Scene(root, width, height));
        primaryStage.setX(visualBounds.getMinX() + (visualBounds.getWidth() - width) / 2);
        primaryStage.setY(visualBounds.getMinY() + (visualBounds.getHeight() - height) / 2);

        primaryStage.show();
    }

    private Tab mainTab(TabPane tabPane) {

        FlowPane flowPane = new FlowPane();
        flowPane.setPadding(new Insets(40));
        flowPane.setHgap(40);
        flowPane.setVgap(40);

        List<AppContainer> containers = AppManager.getAppContainers();
        for (AppContainer container : containers) {
            Parent appPane = createAppPane(tabPane, container);
            flowPane.getChildren().add(appPane);
        }

        ScrollPane scrollPane = new ScrollPane(flowPane);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        Tab tab = new Tab();
        tab.setClosable(false);
        tab.setGraphic(Icons.iconView("/logo.png", 16));
        tab.setContent(scrollPane);
        return tab;
    }

    private Parent createAppPane(TabPane tabPane, AppContainer container) {
        VBox vBox = new VBox(5,
            new Label(container.getAppName()),
            createAppButton(tabPane, container)
        );
        vBox.setPadding(new Insets(5, 0, 0, 0));

        HBox hBox = new HBox(10,
            Icons.iconView(container.getIcon(), 64),
            vBox
        );

        return hBox;
    }

    private SplitMenuButton createAppButton(TabPane tabPane, AppContainer container) {
        SplitMenuButton button = new SplitMenuButton();
        button.setText("打开");
        button.setUserData(container);
        button.setOnAction(e -> {
            SplitMenuButton b = (SplitMenuButton) e.getSource();
            chooseOrCreateTab(tabPane, container, b);
        });

        if (container.getAppInfo().author().length() > 0) {
            MenuItem menuItem = new MenuItem();
            menuItem.setText("作者：" + container.getAppInfo().author());
            menuItem.setOnAction(event -> {
                String url = container.getAppInfo().url();
                HostServicesFactory.getInstance(this).showDocument(url);
            });
            button.getItems().add(menuItem);
        }

        return button;
    }

    private void chooseOrCreateTab(TabPane tabPane, AppContainer container, Control b) {
        for (Tab tab : tabPane.getTabs()) {
            if (Objects.equals(b.getUserData(), tab.getUserData())) {
                tabPane.getSelectionModel().select(tab);
                return;
            }
        }

        Tab appTab = createAppTab(container);
        if (appTab != null) {
            tabPane.getTabs().add(appTab);
            tabPane.getSelectionModel().select(appTab);
        }
    }

    private Tab createAppTab(AppContainer appContainer) {
        Parent root;

        try {
            JfappsApp app = appContainer.getOrCreateAppInstance();
            root = app.getRoot();
        } catch (Exception e) {
            log.error("", e);
            new Alert(Alert.AlertType.ERROR, e.toString(), ButtonType.OK).showAndWait();
            return null;
        }

        Tab tab = new Tab();
        tab.setUserData(appContainer);
        tab.setGraphic(Icons.iconView(appContainer.getIcon(), 16));
        tab.setText(appContainer.getAppName());
        tab.setContent(root);
        tab.setOnCloseRequest(event -> {
            JfappsApp appInstance = appContainer.getAppInstance();
            if (appInstance != null) {
                appInstance.onCloseRequest();
            }
        });
        tab.setOnClosed(event -> AppManager.appClosed(appContainer));
        return tab;
    }

}
