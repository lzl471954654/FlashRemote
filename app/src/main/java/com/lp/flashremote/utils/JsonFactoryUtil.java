package com.lp.flashremote.utils;


import com.google.gson.Gson;
import com.lp.flashremote.beans.Command;

public class JsonFactoryUtil {

    private static final Gson gosn=new Gson();

    /**
     * make command's gson data
     * @param type
     * @param des
     * @return
     */
    public static String getCmd(String type,String des){
        Command command=new Command();
        command.setType(type);
        command.setDescribe(des);
        return gosn.toJson(command);
    }

   /* public static String getMouseJson(Map<Integer,Integer> map, boolean click, boolean single,
                                      boolean doubleClick, boolean right){
        MouseOpInfo mouse=new MouseOpInfo();
        mouse.setMap(map);
        mouse.setClick(click);
        mouse.setSingleClick(single);
        mouse.setDoubleClick(doubleClick);
        mouse.setRightClick(right);
        return new Gson().toJson(mouse);
    }*/
}
