package com.nvlad.tinypng.ui.dialogs;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.CheckedTreeNode;
import com.nvlad.tinypng.services.TinyPNGErrorInfo;
import com.nvlad.tinypng.util.ZipSignUtil;

import java.io.IOException;

public class FileTreeNode extends CheckedTreeNode {
    private byte[] compressedImage;
    private TinyPNGErrorInfo error;
    private int zipCount = -1;

    public FileTreeNode() {

    }

    public FileTreeNode(VirtualFile file) {
        super(file);
    }

    public VirtualFile getVirtualFile() {
        return (VirtualFile) getUserObject();
    }

    public byte[] getImageBuffer() {
        return compressedImage;
    }

    public void setImageBuffer(byte[] compressedImage) {
        this.compressedImage = compressedImage;
    }

    public boolean hasError() {
        return error != null;
    }

    public TinyPNGErrorInfo getError() {
        return error;
    }

    public void setError(TinyPNGErrorInfo error) {
        this.error = error;
    }

    public int getZipCount(){
        if (zipCount >= 0) return zipCount;
        VirtualFile file = getVirtualFile();
        if (file == null || file.isDirectory()) {
            zipCount = -1;
            return zipCount;
        }
        try {
            zipCount = ZipSignUtil.getZipCount(getVirtualFile().contentsToByteArray());
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return zipCount;
    }

    public void initZipCount(){
        zipCount = -1;
    }
}
