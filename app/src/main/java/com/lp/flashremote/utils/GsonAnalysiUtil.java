package com.lp.flashremote.utils;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lp.flashremote.beans.DiskInfo;
import com.lp.flashremote.beans.FileInfo;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by PUJW on 2017/9/15.
 *
 */

public class GsonAnalysiUtil {
    private static Gson gson=new Gson();
    public static List<DiskInfo> getDisklist(String diskinfo){
        List<DiskInfo> retList = gson.fromJson(diskinfo,
                new TypeToken<List<DiskInfo>>() {
                }.getType());
        return retList;
    }

    public static List<FileInfo> getFileList(String fileinfo){
        List<FileInfo> fileInfos=gson.fromJson(fileinfo,
                new TypeToken<List<FileInfo>>(){
                }.getType());
        return fileInfos;
    }
}
