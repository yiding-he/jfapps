package com.hyd.elasticjobclient;

import static com.hyd.fx.builders.IconBuilder.icon;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Optional;
import com.hyd.fx.builders.ButtonBuilder;
import com.hyd.fx.builders.MenuBuilder;
import com.hyd.fx.dialog.AlertDialog;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import io.elasticjob.lite.lifecycle.api.JobOperateAPI;
import io.elasticjob.lite.lifecycle.internal.operate.JobOperateAPIImpl;
import io.elasticjob.lite.reg.zookeeper.ZookeeperRegistryCenter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

public class ElasticJobPane extends VBox {

    private String regName;

    private ZookeeperRegistryCenter registryCenter;

    private JobOperateAPI jobOperateAPI;

    private TableView<Job> tableView;

    private Tab parentTab;

    private ContextMenu contextMenu;

    private TableRow<Job> currentRow;

    @SuppressWarnings("unchecked")
    public ElasticJobPane(String regName, ZookeeperRegistryCenter registryCenter, Tab parentTab) {
        this.setPadding(new Insets(10));
        this.regName = regName;
        this.registryCenter = registryCenter;
        this.jobOperateAPI = new JobOperateAPIImpl(registryCenter);
        this.parentTab = parentTab;
        this.contextMenu = MenuBuilder.contextMenu(
            MenuBuilder.menuItem("修改任务属性...", icon(FontAwesomeIcon.EDIT, "#44AA44"), this::modifyTask),
            MenuBuilder.menuItem("触发任务执行", icon(FontAwesomeIcon.LIGHTBULB_ALT, "4444AA"), this::triggerTask),
            new SeparatorMenuItem(),
            MenuBuilder.menuItem("删除任务", icon(FontAwesomeIcon.TRASH, "#AA4444"), this::deleteTask)
        );

        //////////////////////////////////////////////////////////////

        this.tableView = new TableView<>();
        this.tableView.setRowFactory(tv -> {
            TableRow<Job> row = new TableRow<>();
            row.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                if (contextMenu.isShowing()) {
                    contextMenu.hide();
                }

                TableRow _row = (TableRow) event.getSource();
                if (_row.isEmpty()) {
                    return;
                }

                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    currentRow = _row;
                    modifyTask();
                }

                if (event.getButton() == MouseButton.SECONDARY && event.getClickCount() == 1) {
                    currentRow = _row;
                    contextMenu.show(_row, event.getScreenX(), event.getScreenY());
                }
            });
            return row;
        });

        TableColumn<Job, String> jobNameCol = new TableColumn<>("任务名称");
        jobNameCol.setCellValueFactory(new PropertyValueFactory<>("jobName"));

        TableColumn<Job, String> jobDescCol = new TableColumn<>("任务描述");
        jobDescCol.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<Job, String> jobTypeCol = new TableColumn<>("任务类名");
        jobTypeCol.setCellValueFactory(new PropertyValueFactory<>("jobClass"));

        TableColumn<Job, String> jobCronCol = new TableColumn<>("执行计划");
        jobCronCol.setCellValueFactory(new PropertyValueFactory<>("cron"));

        TableColumn<Job, String> jobInstCol = new TableColumn<>("实例数");
        jobInstCol.setCellValueFactory(new PropertyValueFactory<>("instanceCount"));

        this.tableView.getColumns().addAll(
            jobNameCol, jobDescCol, jobTypeCol, jobCronCol, jobInstCol
        );

        this.getChildren().add(tableView);
        VBox.setVgrow(tableView, Priority.ALWAYS);

        //////////////////////////////////////////////////////////////

        HBox buttons = new HBox();
        buttons.setAlignment(Pos.BASELINE_RIGHT);
        buttons.setPadding(new Insets(10, 0, 0, 0));
        buttons.setSpacing(10);
        buttons.getChildren().add(ButtonBuilder.button("关闭", () -> {
            this.registryCenter.close();
            this.parentTab.getTabPane().getTabs().remove(this.parentTab);
        }));

        this.getChildren().add(buttons);
    }

    private void triggerTask() {
        if (currentRow == null) {
            return;
        }

        Job job = currentRow.getItem();
        this.jobOperateAPI.trigger(Optional.of(job.getJobName()), Optional.absent());
        AlertDialog.info("触发成功", "任务 '" + job.getJobName() + "' 已触发。");
    }

    private void deleteTask() {
        if (currentRow == null) {
            return;
        }

        Job job = currentRow.getItem();
        if (!AlertDialog.confirmOkCancel("删除任务", "确定要删除任务 '" + job.getJobName() + "' 吗？")) {
            return;
        }

        registryCenter.remove("/" + job.getKey());
        tableView.getItems().remove(job);
    }

    private void modifyTask() {
        if (currentRow == null) {
            return;
        }

        JobInfoDialog jobInfoDialog = new JobInfoDialog(
            ElasticJobClientMain.getPrimaryStage(), currentRow.getItem()
        );

        jobInfoDialog.showAndWait();
        if (jobInfoDialog.isOk()) {
            tableView.refresh();

            Job job = currentRow.getItem();
            String configJson = registryCenter.get("/" + job.getKey() + "/config");
            JSONObject jsonObject = JSON.parseObject(configJson);
            jsonObject.put("jobName", job.getJobName());
            jsonObject.put("description", job.getDescription());
            jsonObject.put("cron", job.getCron());

            registryCenter.persist("/" + job.getKey() + "/config", JSON.toJSONString(jsonObject));
        }
    }

    public void init() {
        List<Job> jobs = registryCenter.getChildrenKeys("/").stream()
            .map(key -> {
                String configKey = "/" + key + "/config";
                String instancesKey = "/" + key + "/instances";
                String config = registryCenter.get(configKey);

                if (config == null) {
                    return null;
                }

                Job job = JSON.parseObject(config, Job.class);
                job.setKey(key);
                job.setInstanceCount(registryCenter.getNumChildren(instancesKey));

                return job;
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

        this.tableView.getItems().addAll(jobs);
    }
}
