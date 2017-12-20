package com.lp.flashremote.beans;


import com.lp.flashremote.utils.WifiHostBiz;

public class WifiInfo {
    private String name;
    private String pwd;
    private String ip;

    public WifiInfo(String i){
        this.setName(WifiHostBiz.wifiName);
        this.setPwd(WifiHostBiz.wifiPass);
        this.setIp(i);
    }






    public String getName() {
        return name;
    }

    public String getPwd() {
        return pwd;
    }

    private void setName(String name) {
        this.name = name;
    }

    private void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public String toString() {
        return "WifiInfo{" +
                "name='" + name + '\'' +
                ", pwd='" + pwd + '\'' +
                ", ip='" + ip + '\'' +
                '}';
    }
}
