package com.nvlad.tinypng.ui.dialogs.listeners;

import com.intellij.openapi.application.ApplicationManager;
import com.nvlad.tinypng.ui.dialogs.FileTreeNode;
import com.nvlad.tinypng.ui.dialogs.ProcessImageDialog;
import com.nvlad.tinypng.util.ZipSignUtil;

import javax.swing.tree.DefaultTreeModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class DeleteTagActionListener extends BaseFileOperatorActionListener {

    public DeleteTagActionListener(ProcessImageDialog dialog) {
        super(dialog);
    }

    @Override
    protected byte[] operatorData(FileTreeNode node) throws IOException {
        return ZipSignUtil.deleteSign(node.getVirtualFile().contentsToByteArray());
    }


}
