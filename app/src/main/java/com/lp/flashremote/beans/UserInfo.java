package com.lp.flashremote.beans;

/**
 * Created by PUJW on 2017/9/12.
 */

public class UserInfo {
    private static String username="";
    private static String password="";

    public static String getPassword() {
        return password;
    }

    public static String getUsername() {
        return username;
    }

    public static void setPassword(String password) {
        UserInfo.password = password;
    }

    public static void setUsername(String username) {
        UserInfo.username = username;
    }
}
