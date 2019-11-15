package com.hyd.elasticjobclient;

import com.hyd.fx.dialog.form.FormDialog;
import com.hyd.fx.dialog.form.TextFormField;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

public class JobInfoDialog extends FormDialog {

    private final TextFormField jobNameField = new TextFormField("任务名称", "");
    private final TextFormField jobDescField = new TextFormField("任务描述", "");
    private final TextFormField jobCronField = new TextFormField("Cron 表达式", "");

    private Job job;

    public JobInfoDialog(Stage owner, Job job) {
        super(owner);
        setTitle("修改任务 '" + job.getJobName() + "' 属性");
        setWidth(500);
        this.job = job;

        jobNameField.getTextField().setText(job.getJobName());
        jobNameField.getTextField().setEditable(false);
        jobDescField.getTextField().setText(job.getDescription());
        jobCronField.getTextField().setText(job.getCron());

        addField(jobNameField);
        addField(jobDescField);
        addField(jobCronField);
    }


    @Override
    protected void okButtonClicked(ActionEvent event) {
        this.job.setDescription(jobDescField.getText());
        this.job.setCron(jobCronField.getText());
        closeOK();
    }
}
