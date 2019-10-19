package com.hyd.jfapps.textprocess;

import com.hyd.fx.dialog.AlertDialog;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import javafx.scene.control.TextArea;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MainController {

    public TextArea txtOriginText;

    private static String stringContentOf(Dragboard dragboard) {
        Object content = dragboard.getContent(DataFormat.PLAIN_TEXT);
        if (content == null) {
            content = dragboard.getContent(DataFormat.HTML);
        }
        if (content == null) {
            content = dragboard.getContent(DataFormat.URL);
        }
        return content.toString();
    }

    public void initialize() {
        txtOriginText.setOnDragOver(event -> {
            TextArea textArea = (TextArea) event.getSource();
            textArea.getScene().getWindow().requestFocus();

            Dragboard dragboard = event.getDragboard();
            if (
                dragboard.hasContent(DataFormat.PLAIN_TEXT) ||
                    dragboard.hasContent(DataFormat.HTML) ||
                    dragboard.hasContent(DataFormat.URL)
            ) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                event.consume();
            }
        });

        txtOriginText.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            TextArea textArea = (TextArea) event.getSource();

            if (db.getContentTypes().contains(DataFormat.FILES)) {
                List<File> files = (List<File>) db.getContent(DataFormat.FILES);
                if (files.size() > 1) {
                    AlertDialog.error("错误", "一次只能拖放一个文件");
                } else {
                    textArea.setText(
                        String.join("\n", readFileContent(files.get(0)))
                    );
                }
            } else {
                textArea.setText(stringContentOf(db));
            }

        });
    }

    private List<String> readFileContent(File file) {
        try {
            return Files.readAllLines(file.toPath());
        } catch (IOException e) {
            log.error("读取文件失败", e);
            AlertDialog.error("错误", "文件格式不支持");
            return Collections.emptyList();
        }
    }
}
