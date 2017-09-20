package com.lp.flashremote.beans;


public class DiskInfo {
    private String path;
    private String drive;//盘符
    private String useInfo;//使用情况

    public String getDrive() {
        return drive;
    }

    public void setDrive(String drive) {
        this.drive = drive;
    }

    public String getUseInfo() {
        return useInfo;
    }

    public void setUseInfo(String useInfo) {
        this.useInfo = useInfo;
    }

    @Override
    public String toString() {
        return drive+useInfo;
    }


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
