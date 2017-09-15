package com.lp.flashremote.utils;

import com.google.gson.Gson;
import com.lp.flashremote.beans.Command;

/**
 * Created by PUJW on 2017/9/15.
 */

public class Command2JsonUtil {
    public static String getJson(String type,String des,boolean isback){
        Command command=new Command();
        command.setType(type);
        command.setDescribe(des);
        command.setIsback(isback);
        return new Gson().toJson(command);
    }
}
