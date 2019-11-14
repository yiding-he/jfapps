package com.hyd.elasticjobclient;

import com.hyd.fx.app.AppThread;
import com.hyd.fx.builders.ButtonBuilder;
import com.hyd.fx.concurrency.BackgroundTask;
import com.hyd.fx.dialog.AlertDialog;
import com.hyd.jfapps.appbase.*;
import io.elasticjob.lite.reg.zookeeper.ZookeeperConfiguration;
import io.elasticjob.lite.reg.zookeeper.ZookeeperRegistryCenter;
import java.util.List;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;

@AppInfo(
    name = "Elastic Job 客户端",
    author = "yiding-he",
    url = "https://github.com/yiding-he/jfapps",
    category = AppCategory.RPC
)
public class ElasticJobClientMain extends JfappsApp {

    private static Stage primaryStage;

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    private ComboBox<String> addressesCombo;

    private TabPane registryTabs;

    private Button openRegButton;

    public static void main(String[] args) {
        JfappsAppLauncher.launchApp(ElasticJobClientMain.class);
    }

    @Override
    public Parent getRoot() throws Exception {
        primaryStage = appContext.getPrimaryStage();
        primaryStage.setWidth(1000);
        primaryStage.setHeight(600);
        return root();
    }

    private BorderPane root() {
        return new BorderPane(registryTabs(), addressBar(), null, null, null);
    }

    private Node addressBar() {
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.BASELINE_LEFT);
        hBox.setPadding(new Insets(10));
        hBox.setSpacing(10);

        hBox.getChildren().addAll(
            label(),
            addressesCombo = addresses(),
            openRegButton = ButtonBuilder.button("打开", this::openRegistry)
        );
        return hBox;
    }

    private Label label() {
        Label label = new Label("注册中心地址（ip:port/name）：");
        label.setMinWidth(Region.USE_PREF_SIZE);
        return label;
    }

    private void openRegistry() {
        String address = addressesCombo.getValue();
        if (address == null || !address.matches("^.+:\\d+/.+$")) {
            AlertDialog.error("地址格式不正确。");
            return;
        }

        String host = StringUtils.substringBefore(address, "/");
        String regName = StringUtils.substringAfter(address, "/");

        BackgroundTask.runTask(() -> {
            ZookeeperConfiguration conf = new ZookeeperConfiguration(host, regName);
            ZookeeperRegistryCenter registryCenter = new ZookeeperRegistryCenter(conf);
            registryCenter.init();

            List<String> addresses = UserPreferences.get(ConfigKey.ServerAddresses);
            if (!addresses.contains(address)) {
                addressesCombo.getItems().add(address);
                UserPreferences.append(ConfigKey.ServerAddresses, address);
            }

            AppThread.runUIThread(() -> {
                Tab tab = new Tab(regName);
                ElasticJobPane elasticJobPane = new ElasticJobPane(regName, registryCenter, tab);
                tab.setContent(elasticJobPane);
                tab.setClosable(false);
                registryTabs.getTabs().add(tab);

                elasticJobPane.init();
            });

        }).whenBeforeStart(() -> {
            openRegButton.setDisable(true);
        }).whenTaskFinish(() -> {
            openRegButton.setDisable(false);
        }).whenTaskFail(e -> {
            AlertDialog.error("连接注册中心失败", e);
        }).start();
    }

    private ComboBox<String> addresses() {
        ComboBox<String> c = new ComboBox<>();
        c.setEditable(true);
        c.setMaxWidth(Double.POSITIVE_INFINITY);

        List<String> addresses = UserPreferences.get(ConfigKey.ServerAddresses);
        c.getItems().addAll(addresses);

        HBox.setHgrow(c, Priority.ALWAYS);
        return c;
    }

    private TabPane registryTabs() {
        registryTabs = new TabPane();
        return registryTabs;
    }

}
