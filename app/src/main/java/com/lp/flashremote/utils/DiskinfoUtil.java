package com.lp.flashremote.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lp.flashremote.beans.DiskInfo;

import java.util.List;

/**
 * Created by PUJW on 2017/9/15.
 *
 */

public class DiskinfoUtil {
    public static List<DiskInfo> getDisklist(String diskinfo){
        Gson gson=new Gson();
        List<DiskInfo> retList = gson.fromJson(diskinfo,
                new TypeToken<List<DiskInfo>>() {
                }.getType());
        return retList;

    }
}
