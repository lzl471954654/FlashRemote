package com.lp.flashremote.utils;


import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;


public class ReceiveUtil {
    /**
     * 接受文件
     * @param filesDir 文件路径
     * @param reader  socket 输入流
     * @return    是否成功
     */
    public static boolean receiveService(File filesDir,BufferedReader reader){
        //文件路径需要设置
        boolean receiveOK=false;
        BufferedWriter w=null;
        try {
           w=new BufferedWriter(new FileWriter(filesDir));
           String data=null;
            while((data=reader.readLine())!=null){
                w.write(data);
                w.flush();
            }
            receiveOK=true;
        } catch (FileNotFoundException e) {
            Log.e("ReceiveUtil","文件输入错误");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                w.close();

            } catch (IOException e) {
                Log.e("ReceiveUtil","文件关闭失败");
                e.printStackTrace();
            }
        }
        return receiveOK;
    }

}
