package com.nvlad.tinypng.ui.dialogs.listeners;

import com.intellij.openapi.application.ApplicationManager;
import com.nvlad.tinypng.ui.dialogs.FileTreeNode;
import com.nvlad.tinypng.ui.dialogs.ProcessImageDialog;
import com.nvlad.tinypng.util.ZipSignUtil;

import javax.swing.tree.DefaultTreeModel;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public abstract class BaseFileOperatorActionListener extends ActionListenerBase {

    public BaseFileOperatorActionListener(ProcessImageDialog dialog) {
        super(dialog);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        dialog.getButtonSave().setEnabled(false);

        List<FileTreeNode> nodes = getCheckedNodes((FileTreeNode) dialog.getTree().getModel().getRoot());
        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            @Override
            public void run() {
                for (FileTreeNode node : nodes) {
                    dialog.getButtonCancel().setEnabled(false);
                    dialog.getButtonProcess().setEnabled(false);
                    dialog.getButtonAddTag().setEnabled(false);
                    dialog.getButtonDeleteTag().setEnabled(false);
                    try {
                        byte[] operatorData = operatorData(node);
                        if (operatorData == null) continue;
                        OutputStream stream = node.getVirtualFile().getOutputStream(this);
                        stream.write(operatorData);
                        stream.close();
                        node.initZipCount();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                ApplicationManager.getApplication().invokeLater(() -> {
                    for (FileTreeNode node : nodes) {
                        ((DefaultTreeModel) dialog.getTree().getModel()).nodeChanged(node);
                    }
                    dialog.getButtonCancel().setEnabled(true);
                    dialog.getButtonProcess().setEnabled(true);
                    dialog.getButtonAddTag().setEnabled(true);
                    dialog.getButtonDeleteTag().setEnabled(true);
                });
            }
        });
    }

    abstract protected byte[] operatorData(FileTreeNode node) throws IOException;
}
