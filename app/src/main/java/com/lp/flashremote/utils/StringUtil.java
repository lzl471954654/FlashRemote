package com.lp.flashremote.utils;



import com.lp.flashremote.beans.Content;
import com.lp.flashremote.beans.PropertiesUtil;

public class StringUtil {



    /**
     * 上线的下划线拼接
     *
     * @param str1
     * @param str2
     * @param str3
     * @param str4
     * @return
     */
    public static String stringAddUnderline(String str1, String str2, String str3, String str4) {
        return str1 + "_" + str2 + "_" + str3 + "_" + str4;
    }

    public static String stringAddUnderline(String str1, String str2, String str3) {
        return str1 + "_" + str2 + "_" + str3;
    }
    /**
     *
     * @param string
     * @return
     */
    public static boolean startAndEnd(String string) {
        return (string.startsWith(PropertiesUtil.CONNECTED_SUCCESS) && string.endsWith(PropertiesUtil.END_FLAG));
    }

    /**
     *
     * @param s
     * @return
     */
    public static boolean isBind(String s) {
        return s.startsWith(PropertiesUtil.CONNECTED_SUCCESS) && s.endsWith(PropertiesUtil.END_FLAG);
    }

    /**
     * 发送命令的字符串拼接
     *
     * @param cmd
     * @return
     */
    public static String operateCmd( String cmd) {
        return PropertiesUtil.COMMAND + "_" + cmd;
    }

    /**
     * 字符串结尾加 end_flag结尾标志
     *
     * @param s
     * @return
     */
    public static String addEnd_flag2Str(String s) {
        return s + "_" + PropertiesUtil.END_FLAG;
    }

    public static String rmEnd_flagstr(String s){
        int endflagindex=s.lastIndexOf("_");
        return s.substring(0,endflagindex);
    }

    public static Content getContent(String s){
        int head=s.indexOf("_");
        int tail=s.lastIndexOf("_");
        Content content=new Content();
        content.setHead(s.substring(0,head));
        content.setContent(s.substring(head+1,tail));
        content.setTail(s.substring(tail+1,s.length()));
        return content;
    }
}
