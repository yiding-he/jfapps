package com.hyd.elasticjobclient;

import static com.hyd.fx.builders.IconBuilder.icon;
import static com.hyd.fx.builders.MenuBuilder.menuItem;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Optional;
import com.hyd.fx.builders.ButtonBuilder;
import com.hyd.fx.builders.MenuBuilder;
import com.hyd.fx.dialog.AlertDialog;
import com.hyd.fx.utils.Str;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import io.elasticjob.lite.lifecycle.api.JobOperateAPI;
import io.elasticjob.lite.lifecycle.internal.operate.JobOperateAPIImpl;
import io.elasticjob.lite.reg.zookeeper.ZookeeperRegistryCenter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("Guava")
public class ElasticJobPane extends VBox {

    private static final Logger log = LoggerFactory.getLogger(ElasticJobPane.class);

    private final ZookeeperRegistryCenter registryCenter;

    private final JobOperateAPI jobOperateAPI;

    private final TableView<Job> tableView;

    private final Tab parentTab;

    private final ContextMenu contextMenu;

    private final FilteredList<Job> jobFilteredList;

    private final ObservableList<Job> jobList;

    private TableRow<Job> currentRow;

    @SuppressWarnings("unchecked")
    public ElasticJobPane(ZookeeperRegistryCenter registryCenter, Tab parentTab) {
        this.setPadding(new Insets(10));
        this.registryCenter = registryCenter;
        this.jobOperateAPI = new JobOperateAPIImpl(registryCenter);
        this.parentTab = parentTab;
        this.contextMenu = MenuBuilder.contextMenu(
            menuItem("修改任务属性...", icon(FontAwesomeIcon.EDIT, "#000000"), this::modifyTask),
            menuItem("触发任务执行", icon(FontAwesomeIcon.LIGHTBULB_ALT, "#000000"), this::triggerTask),
            new SeparatorMenuItem(),
            menuItem("任务生效", icon(FontAwesomeIcon.CHECK_CIRCLE, "#000000"), this::enableTask),
            menuItem("任务失效", icon(FontAwesomeIcon.CIRCLE_ALT, "#000000"), this::disableTask),
            new SeparatorMenuItem(),
            menuItem("删除任务", icon(FontAwesomeIcon.TRASH, "#000000"), this::deleteTask)
        );

        //////////////////////////////////////////////////////////////

        this.jobList = FXCollections.observableArrayList();
        this.jobFilteredList = new FilteredList<>(this.jobList);

        this.tableView = new TableView<>();

        // FilteredList 是无法排序的，所以要包装成 SortedList，同时要绑定
        // comparatorProperty 属性，才能实现点击表头排序
        final SortedList<Job> sortedList = new SortedList<>(this.jobFilteredList);
        sortedList.comparatorProperty().bind(this.tableView.comparatorProperty());
        this.tableView.setItems(sortedList);

        this.tableView.setRowFactory(tv -> {
            TableRow<Job> row = new TableRow<>();
            row.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                if (contextMenu.isShowing()) {
                    contextMenu.hide();
                }

                TableRow<Job> _row = (TableRow<Job>) event.getSource();
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

        TableColumn<Job, String> jobCronCol = new TableColumn<>("执行计划");
        jobCronCol.setCellValueFactory(new PropertyValueFactory<>("cron"));

        TableColumn<Job, String> jobInstCol = new TableColumn<>("实例数");
        jobInstCol.setCellValueFactory(new PropertyValueFactory<>("instanceCount"));

        TableColumn<Job, String> jobShardCol = new TableColumn<>("分片数");
        jobShardCol.setCellValueFactory(new PropertyValueFactory<>("shardingTotalCount"));

        this.tableView.getColumns().addAll(
            jobNameCol, jobDescCol, jobCronCol, jobInstCol, jobShardCol
        );

        this.getChildren().add(tableView);
        VBox.setVgrow(tableView, Priority.ALWAYS);

        //////////////////////////////////////////////////////////////

        HBox buttons = new HBox();
        buttons.setAlignment(Pos.BASELINE_RIGHT);
        buttons.setPadding(new Insets(10, 0, 0, 0));
        buttons.setSpacing(10);
        buttons.getChildren().addAll(
            searchText(),
            ButtonBuilder.button("刷新", this::refreshJobs),
            ButtonBuilder.button("关闭", () -> {
                this.registryCenter.close();
                this.parentTab.getTabPane().getTabs().remove(this.parentTab);
            })
        );

        this.getChildren().add(buttons);
    }

    private void enableTask() {
        if (currentRow == null) {
            return;
        }

        Job job = currentRow.getItem();
        this.jobOperateAPI.enable(Optional.of(job.getJobName()), Optional.absent());
        AlertDialog.info("启用成功", "任务 '" + job.getJobName() + "' 已启用。");
    }

    private void disableTask() {
        if (currentRow == null) {
            return;
        }

        Job job = currentRow.getItem();
        this.jobOperateAPI.disable(Optional.of(job.getJobName()), Optional.absent());
        AlertDialog.info("禁用成功", "任务 '" + job.getJobName() + "' 已禁用。");
    }

    private TextField searchText() {
        TextField searchText = new TextField();
        searchText.textProperty().addListener((observable, oldValue, newValue) -> {
            if (Str.isBlank(newValue)) {
                this.jobFilteredList.setPredicate(null);
            } else {
                this.jobFilteredList.setPredicate(job ->
                    job.getJobName().toLowerCase().contains(newValue.toLowerCase()) ||
                        job.getDescription().toLowerCase().contains(newValue.toLowerCase())
                );
            }
        });
        return searchText;
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
        refreshJobs();
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
            jsonObject.put("jobParameter", job.getJobParameter());
            jsonObject.put("shardingTotalCount", job.getShardingTotalCount());

            registryCenter.persist("/" + job.getKey() + "/config", JSON.toJSONString(jsonObject));
        }
    }

    public void init() {
        refreshJobs();
    }

    private void refreshJobs() {
        List<? extends Job> jobs = registryCenter.getChildrenKeys("/").stream()
            .map(key -> {
                String configKey = "/" + key + "/config";
                String instancesKey = "/" + key + "/instances";
                String config = registryCenter.get(configKey);

                if (config == null) {
                    return null;
                } else {
                    // log.info(config);
                }

                Job job = JSON.parseObject(config, Job.class);
                job.setKey(key);
                job.setInstanceCount(registryCenter.getNumChildren(instancesKey));

                return job;
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

        this.jobList.setAll(jobs);
    }
}
