package com.lp.flashremote.utils;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by PUJW on 2017/8/17.
 * 二维码生成工具类
 */

public class QRcodeutil {

    public static boolean createQRcode(String QRcodeContent,
                                       int widthpix,int heightpix,String filepath){
        if (QRcodeContent==null || "".equals(QRcodeContent)){
            return false;
        }
        Map<EncodeHintType,Object> hints=new HashMap<EncodeHintType,Object>();
        hints.put(EncodeHintType.CHARACTER_SET,"UTF-8");
        hints.put(EncodeHintType.MARGIN,2);//设置白边
        BitMatrix bitMatrix=null;
        try {
           bitMatrix=new QRCodeWriter()
                    .encode(QRcodeContent, BarcodeFormat.QR_CODE,widthpix,heightpix,hints);
            int[] pixels=new int[widthpix * heightpix];
            for(int y=0;y<heightpix;y++){
                for(int x=0;x<widthpix;x++){
                    if (bitMatrix.get(x, y)) {
                        pixels[y * widthpix + x] = 0xff000000;
                    } else {
                        pixels[y * heightpix + x] = 0xffffffff;
                    }
                }
            }
            Bitmap bitmap=Bitmap.createBitmap(widthpix,heightpix, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels,0,widthpix,0,0,widthpix,heightpix);
            return bitmap!=null && bitmap.compress(Bitmap.CompressFormat.JPEG,100,
                    new FileOutputStream(filepath));

        } catch (WriterException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            Log.e("QRcodeutil：",e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}
