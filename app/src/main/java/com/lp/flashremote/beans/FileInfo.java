package com.lp.flashremote.beans;

import java.io.File;

public class FileInfo {
    private String path;
    private String name;
    private boolean type;

    @Override
    public String toString() {
        return path + File.separator + name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isType() {
        return type;
    }

    public void setType(boolean type) {
        this.type = type;
    }
}
