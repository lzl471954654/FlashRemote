package com.lp.flashremote.beans;





public class WifiInfo {
    private String name;
    private String pwd;
    private String ip;

    public WifiInfo(String i){
        this.setName(PropertiesUtil.WIFINAME);
        this.setPwd(PropertiesUtil.WIFIPWD);
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
}
