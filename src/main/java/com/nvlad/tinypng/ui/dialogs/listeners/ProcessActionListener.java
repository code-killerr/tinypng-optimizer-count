package com.nvlad.tinypng.ui.dialogs.listeners;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.ui.Messages;
import com.nvlad.tinypng.Constants;
import com.nvlad.tinypng.services.TinyPNG;
import com.nvlad.tinypng.services.TinyPNGErrorInfo;
import com.nvlad.tinypng.ui.dialogs.FileTreeNode;
import com.nvlad.tinypng.ui.dialogs.ProcessImageDialog;
import com.nvlad.tinypng.util.StringFormatUtil;
import com.tinify.Exception;
import org.apache.commons.lang3.StringUtils;

import javax.swing.tree.DefaultTreeModel;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.List;

public class ProcessActionListener extends ActionListenerBase {
    public ProcessActionListener(ProcessImageDialog dialog) {
        super(dialog);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!TinyPNG.setupApiKey(dialog.getProject())) {
            return;
        }
        boolean isSkipCompress = dialog.getRbSkipCompress().isSelected();
        int skipCount = 0;
        String temp = dialog.getTfSkipCount().getText();
        if (StringUtils.isNumeric(temp))
            skipCount = Integer.parseInt(temp);

        dialog.setTitle("[0%]");
        dialog.setCompressInProgress(true);
        dialog.getButtonProcess().setEnabled(false);
        dialog.getButtonCancel().setText("Stop");
        final List<FileTreeNode> nodes = getCheckedNodes((FileTreeNode) dialog.getTree().getModel().getRoot());
        for (FileTreeNode node : nodes) {
            node.setImageBuffer(null);
            ((DefaultTreeModel) dialog.getTree().getModel()).nodeChanged(node);
        }

        int finalSkipCount = skipCount;
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            int index = 0;
            for (FileTreeNode node : nodes) {
                if (isSkipCompress && node.getZipCount() >= finalSkipCount)
                    continue;
                try {
                    node.setImageBuffer(TinyPNG.process(node.getVirtualFile()));
                } catch (Exception tinifyException) {
                    TinyPNGErrorInfo error = TinyPNGErrorInfo.parse(tinifyException.getMessage());
                    if (error != null && error.code == 415) {
                        node.setError(error);
                    } else {
                        ApplicationManager.getApplication().invokeLater(() -> {
                            dialog.setCompressInProgress(false);
                            dialog.clearTitle();
                            dialog.getRootPane().setDefaultButton(dialog.getButtonProcess());
                            dialog.getButtonProcess().setEnabled(true);
                            dialog.getButtonSave().setEnabled(true);
                            dialog.getButtonCancel().setText("Close(关闭)");
                            Messages.showErrorDialog(tinifyException.getMessage(), Constants.TITLE);
                        }, ModalityState.any());

                        return;
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

                final float finalIndex = index;
                ApplicationManager.getApplication().invokeLater(() -> {
                    ((DefaultTreeModel) dialog.getTree().getModel()).nodeChanged(node);
                    dialog.setTitle(String.format("[%.0f%%]", finalIndex / nodes.size() * 100));
                });

                if (!dialog.getCompressInProgress()) {
                    break;
                }

                index++;
            }

            ApplicationManager.getApplication().invokeLater(() -> {
                dialog.clearTitle();
                dialog.setCompressInProgress(false);
                dialog.getRootPane().setDefaultButton(dialog.getButtonSave());
                dialog.getButtonSave().setEnabled(true);
                dialog.getButtonProcess().setEnabled(true);
                dialog.getButtonCancel().setText("Close(关闭)");

                long totalBytes = 0;
                long totalSavedBytes = 0;
                for (FileTreeNode node : nodes) {
                    totalBytes += node.getVirtualFile().getLength();
                    if (node.getVirtualFile() != null && node.getImageBuffer() != null) {
                        totalSavedBytes += node.getVirtualFile().getLength() - node.getImageBuffer().length;
                    }
                }

                float compress = (((float) totalSavedBytes) * 100 / ((float) totalBytes));
                String saved = StringFormatUtil.humanReadableByteCount(totalSavedBytes);
                dialog.getTree().clearSelection();
                dialog.getTotalDetails().setText(String.format("压缩率: %.1f%% / 节省: %s", compress, saved));
            });

        });
    }
}
