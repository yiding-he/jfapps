package com.hyd.elasticjobclient;

import com.hyd.fx.dialog.form.FormDialog;
import com.hyd.fx.dialog.form.IntegerSpinnerFormField;
import com.hyd.fx.dialog.form.TextAreaFormField;
import com.hyd.fx.dialog.form.TextFormField;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

public class JobInfoDialog extends FormDialog {

    private final TextFormField jobDescField = new TextFormField("任务描述", "");

    private final TextFormField jobCronField = new TextFormField("Cron 表达式", "");

    private final IntegerSpinnerFormField jobShardField = new IntegerSpinnerFormField("任务分片数", 1, 1000, 1, true);

    private final TextAreaFormField jobParamField = new TextAreaFormField("触发参数", "", 5, true);

    private final Job job;

    public JobInfoDialog(Stage owner, Job job) {
        super(owner);
        setTitle("修改任务 '" + job.getJobName() + "' 属性");
        setWidth(500);
        this.job = job;

        TextFormField jobNameField = new TextFormField("任务名称", "");
        jobNameField.getTextField().setEditable(false);

        jobNameField.getTextField().setText(job.getJobName());
        jobDescField.getTextField().setText(job.getDescription());
        jobCronField.getTextField().setText(job.getCron());
        jobShardField.setValue(job.getShardingTotalCount());
        jobParamField.getTextArea().setText(job.getJobParameter());

        addField(jobNameField);
        addField(jobDescField);
        addField(jobCronField);
        addField(jobShardField);
        addField(jobParamField);
    }


    @Override
    protected void okButtonClicked(ActionEvent event) {
        this.job.setDescription(jobDescField.getText());
        this.job.setCron(jobCronField.getText());
        this.job.setJobParameter(jobParamField.getText());
        closeOK();
    }
}
