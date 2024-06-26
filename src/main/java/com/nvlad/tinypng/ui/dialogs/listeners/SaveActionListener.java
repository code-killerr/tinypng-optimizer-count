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

public class SaveActionListener extends BaseFileOperatorActionListener {
    public SaveActionListener(ProcessImageDialog dialog) {
        super(dialog);
    }

    @Override
    protected byte[] operatorData(FileTreeNode node) throws IOException {
        if (node.getImageBuffer() == null) return null;
        return ZipSignUtil.addZipSign(node.getVirtualFile().contentsToByteArray(), node.getImageBuffer());
    }

}
