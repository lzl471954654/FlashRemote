package com.lp.flashremote.beans;

/**
 * Created by PUJW on 2017/9/15.
 * 发送命令 beans
 */

public class Command {
    private String type;
    private String describe;
    private boolean isback=false;

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getType() {
        return type;
    }

    public void setIsback(boolean isback) {
        this.isback = isback;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean getIsBack(){
        return isback;
    }

}
