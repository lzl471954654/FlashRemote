package com.lp.flashremote.beans;

import java.io.Serializable;

/**
 * Created by LZL on 2017/8/18.
 */

public class BaseFile implements Serializable {
    String filePath;
    Long fileSize;
    String fileName;
    Long fileCreateDate;
    boolean isChoosed = false;

    @Override
    public String toString() {
        return "BaseFile{" +
                "filePath='" + filePath + '\'' +
                ", fileSize=" + fileSize +
                ", fileName='" + fileName + '\'' +
                ", fileCreateDate=" + fileCreateDate +
                ", isChoosed=" + isChoosed +
                '}';
    }

    public Long getFileCreateDate() {
        return fileCreateDate;
    }

    public void setFileCreateDate(Long fileCreateDate) {
        this.fileCreateDate = fileCreateDate;
    }

    public boolean isChoosed() {
        return isChoosed;
    }

    public void setChoosed(boolean choosed) {
        isChoosed = choosed;
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

    public BaseFile(String filePath, Long fileSize, String fileName,Long fileCreateDate) {
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.fileName = fileName;
        this.fileCreateDate = fileCreateDate;
    }
}
