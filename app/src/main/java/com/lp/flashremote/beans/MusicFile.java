package com.lp.flashremote.beans;

import java.io.Serializable;

/**
 * Created by LZL on 2017/8/18.
 */

public class MusicFile extends BaseFile implements Serializable {
    String title;
    String artist;
    Long duration;

    @Override
    public String toString() {
        return "MusicFile{" +
                "filePath='" + filePath + '\'' +
                ", fileName='" + fileName + '\'' +
                ", fileSize=" + fileSize +
                ", title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", duration=" + duration +
                '}';
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public MusicFile(String filePath, String fileName, Long fileSize, String title, String artist, Long duration,Long date) {
        super(filePath,fileSize,fileName,date);
        this.title = title;
        this.artist = artist;
        this.duration = duration;
    }
}
