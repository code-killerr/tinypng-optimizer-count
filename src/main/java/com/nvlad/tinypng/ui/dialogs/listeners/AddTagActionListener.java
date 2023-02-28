package com.nvlad.tinypng.ui.dialogs.listeners;

import com.nvlad.tinypng.ui.dialogs.FileTreeNode;
import com.nvlad.tinypng.ui.dialogs.ProcessImageDialog;
import com.nvlad.tinypng.util.ZipSignUtil;

import java.io.IOException;

public class AddTagActionListener extends BaseFileOperatorActionListener {

    public AddTagActionListener(ProcessImageDialog dialog) {
        super(dialog);
    }

    @Override
    protected byte[] operatorData(FileTreeNode node) throws IOException {
        if (dialog.getRadioSkipSignFile().isSelected())
            return ZipSignUtil.addSourceSign(node.getVirtualFile().contentsToByteArray(), true);
        else
            return ZipSignUtil.addSourceSign(node.getVirtualFile().contentsToByteArray(), false);
    }

}
