package com.hyd.jfapps.zkclient;

import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * 提示/警告/错误对话框
 *
 * @author yiding_he
 */
public class AlertDialog {

    private static final Logger LOG = LoggerFactory.getLogger(AlertDialog.class);

    public static void error(String message) {
        alert(Alert.AlertType.ERROR, "错误", message);
    }

    public static void error(String title, String message) {
        alert(Alert.AlertType.ERROR, title, message);
    }

    public static void error(String title, Throwable throwable) {
        boolean noMessage = StringUtils.isBlank(throwable.getMessage());
        String message = noMessage ? throwable.toString() : throwable.getMessage();
        error(title, message, ExceptionUtils.getStackTrace(throwable));
    }

    public static void info(String title, String message) {
        alert(Alert.AlertType.INFORMATION, title, message);
    }

    public static void warn(String title, String message) {
        alert(Alert.AlertType.WARNING, title, message);
    }

    public static void alert(Alert.AlertType alertType, String title, String message) {
        Platform.runLater(() -> {
            try {
                Alert alert = new Alert(alertType, message, ButtonType.OK);
                alert.setTitle(title);
                alert.setHeaderText(null);

                Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();

                alert.showAndWait();
            } catch (Exception e) {
                LOG.error("打开对话框失败", e);
            }
        });
    }

    // 打开一个展示了详细错误信息的错误对话框
    public static void error(String title, String message, String details) {
        Platform.runLater(() -> error0(title, message, details));
    }

    private static void error0(String title, String message, String details) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message.trim());

        TextArea textArea = new TextArea(details);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(textArea, 0, 0);

        alert.getDialogPane().setExpandableContent(expContent);

        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();

        alert.showAndWait();
    }

    //////////////////////////////////////////////////////////////

    public static boolean confirmOkCancel(String title, String message) {
        return confirm(Alert.AlertType.CONFIRMATION, title, message, ButtonType.OK, ButtonType.CANCEL) == ButtonType.OK;
    }

    public static boolean confirmYesNo(String title, String message) {
        return confirm(Alert.AlertType.WARNING, title, message, ButtonType.YES, ButtonType.NO) == ButtonType.YES;
    }

    public static ButtonType confirmYesNoCancel(String title, String message) {
        return confirm(Alert.AlertType.WARNING, title, message, ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
    }

    public static ButtonType confirm(Alert.AlertType alertType, String title, String message, ButtonType... buttonTypes) {
        try {
            Alert alert = new Alert(alertType, message, buttonTypes);
            alert.setTitle(title);
            alert.setHeaderText(null);

            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();

            Optional<ButtonType> result = alert.showAndWait();
            return result.orElse(ButtonType.CANCEL);
        } catch (Exception e) {
            LOG.error("打开对话框失败", e);
            return ButtonType.CANCEL;
        }
    }

    public static String input(String message) {
        TextField textField = new TextField();
        Alert alert = new Alert(
                Alert.AlertType.NONE, null,
                new ButtonType("取消", ButtonBar.ButtonData.NO),
                new ButtonType("确定", ButtonBar.ButtonData.YES)
        );
        alert.setTitle(message);
        alert.setGraphic(textField);
        alert.setWidth(200.0D);

        Optional<ButtonType> _buttonType = alert.showAndWait();

        if (_buttonType.isPresent() &&
                _buttonType.get().getButtonData().equals(ButtonBar.ButtonData.YES)) {
            return textField.getText();
        } else {
            return null;
        }
    }


}
