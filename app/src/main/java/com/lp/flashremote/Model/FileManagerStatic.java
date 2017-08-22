package com.lp.flashremote.Model;

import com.lp.flashremote.beans.BaseFile;
import com.lp.flashremote.beans.MusicFile;
import com.lp.flashremote.beans.VideoFile;

import java.util.ArrayList;

/**
 * Created by LZL on 2017/8/20.
 */

public class FileManagerStatic {
    public static ArrayList<MusicFile> musicList;
    public static ArrayList<VideoFile> videoList;
    public static ArrayList<BaseFile> picList;
    public static ArrayList<BaseFile> zipList;
    public static ArrayList<BaseFile> apkList;
    public static ArrayList<BaseFile> bluetoothList;
    public static ArrayList<BaseFile> docList;
    public static ArrayList<BaseFile> downloadList;
    public static boolean hasData = false;
}
