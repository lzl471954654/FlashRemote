package com.lp.flashremote.utils;

import com.google.gson.Gson;
import com.lp.flashremote.beans.Command;
import com.lp.flashremote.beans.MouseOpInfo;

import java.util.Map;

/**
 * Created by PUJW on 2017/9/15.
 */

public class Command2JsonUtil {
    public static String getJson(String type,String des,boolean isback){
        Command command=new Command();
        command.setType(type);
        command.setDescribe(des);
        return new Gson().toJson(command);
    }

    public static String getMouseJson(Map<Integer,Integer> map, boolean click, boolean single,
                                      boolean doubleClick, boolean right){
        MouseOpInfo mouse=new MouseOpInfo();
         mouse.setMap(map);
        mouse.setClick(click);
        mouse.setSingleClick(single);
        mouse.setDoubleClick(doubleClick);
        mouse.setRightClick(right);
        return new Gson().toJson(mouse);
    }
}
