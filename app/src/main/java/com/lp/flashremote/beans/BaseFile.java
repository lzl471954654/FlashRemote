package com.lp.flashremote.beans;

import java.io.Serializable;

/**
 * Created by LZL on 2017/8/18.
 */

public class BaseFile implements Serializable {
    String filePath;
    Long fileSize;
    String fileName;

    @Override
    public String toString() {
        return "BaseFile{" +
                "filePath='" + filePath + '\'' +
                ", fileSize=" + fileSize +
                ", fileName='" + fileName + '\'' +
                '}';
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public BaseFile(String filePath, Long fileSize, String fileName) {
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.fileName = fileName;
    }
}
