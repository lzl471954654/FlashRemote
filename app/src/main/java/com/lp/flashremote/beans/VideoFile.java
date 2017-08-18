package com.lp.flashremote.beans;

import java.io.Serializable;

/**
 * Created by LZL on 2017/8/18.
 */

public class VideoFile extends BaseFile implements Serializable {
    String title;
    Long duration;

    public VideoFile(String filePath, Long fileSize, String fileName, String title, Long duration) {
        super(filePath, fileSize, fileName);
        this.title = title;
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "VideoFile{" +
                "title='" + title + '\'' +
                ", duration=" + duration +
                '}';
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }
}
