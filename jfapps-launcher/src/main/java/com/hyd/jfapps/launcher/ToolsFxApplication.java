package com.hyd.jfapps.launcher;

import com.hyd.jfapps.appbase.JfappsApp;
import com.hyd.jfapps.launcher.appmanager.AppContainer;
import com.hyd.jfapps.launcher.appmanager.AppManager;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;

@Slf4j
public class ToolsFxApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Icons.setStageIcon(primaryStage);
        AppManager.GLOBAL_CONTEXT.put("primaryStage", primaryStage);

        TabPane root = new TabPane();
        root.getTabs().add(mainTab(root));

        primaryStage.setTitle("Hydrogen Tools Fx");
        primaryStage.setScene(new Scene(root, 400, 300));
        primaryStage.show();
    }

    private Tab mainTab(TabPane tabPane) {

        FlowPane flowPane = new FlowPane();
        flowPane.setPadding(new Insets(10));
        flowPane.setHgap(10);
        flowPane.setVgap(10);

        List<AppContainer> containers = AppManager.getAppContainers();
        for (AppContainer container : containers) {
            Button button = createAppButton(tabPane, container);
            flowPane.getChildren().add(button);
        }

        Tab tab = new Tab();
        tab.setClosable(false);
        tab.setGraphic(Icons.iconView("/logo.png", 16));
        tab.setContent(flowPane);
        return tab;
    }

    private Button createAppButton(TabPane tabPane, AppContainer container) {
        Button button = new Button(container.getAppName());
        button.setUserData(container);
        button.setOnAction(e -> {
            Button b = (Button) e.getSource();
            chooseOrCreateTab(tabPane, container, b);
        });
        Image appIcon = container.getIcon();
        if (appIcon != null) {
            button.setGraphic(Icons.iconView(appIcon, 32));
        }
        return button;
    }

    private void chooseOrCreateTab(TabPane tabPane, AppContainer container, Button b) {
        for (Tab tab : tabPane.getTabs()) {
            if (Objects.equals(b.getUserData(), tab.getUserData())) {
                tabPane.getSelectionModel().select(tab);
                return;
            }
        }

        Tab appTab = createAppTab(container);
        tabPane.getTabs().add(appTab);
        tabPane.getSelectionModel().select(appTab);
    }

    private Tab createAppTab(AppContainer appContainer) {
        Parent root = null;
        JfappsApp app = appContainer.getAppInstance();

        try {
            root = app.getRoot();
        } catch (Exception e) {
            log.error("", e);
            new Alert(Alert.AlertType.ERROR, e.toString(), ButtonType.OK).showAndWait();
        }

        Tab tab = new Tab();
        tab.setUserData(appContainer);
        tab.setGraphic(Icons.iconView(appContainer.getIcon(), 16));
        tab.setText(appContainer.getAppName());
        tab.setContent(root);
        return tab;
    }

}
