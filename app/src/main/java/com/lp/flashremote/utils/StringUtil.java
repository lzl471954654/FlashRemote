package com.lp.flashremote.utils;



import com.lp.flashremote.beans.ServerProtocol;

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

    /**
     * 返回字符串是否以  ServerProtocol.ONLINE_SUCCESS 开始
     * 以   ServerProtocol.END_FLAG      结尾
     *
     * @param string
     * @return
     */
    public static boolean startAndEnd(String string) {
        return (string.startsWith(ServerProtocol.CONNECTED_SUCCESS) && string.endsWith(ServerProtocol.END_FLAG));
    }

    /**
     * 返回字符串是否以  ServerProtocol.CONNECTED_SUCCESS 开始
     * 以   ServerProtocol.END_FLAG      结尾
     *
     * @param s
     * @return
     */
    public static boolean isBind(String s) {
        return s.startsWith(ServerProtocol.CONNECTED_SUCCESS) && s.endsWith(ServerProtocol.END_FLAG);
    }

    /**
     * 发送命令的字符串拼接
     *
     * @param s
     * @param cmd
     * @return
     */
    public static String operateCmd(String s, String cmd) {
        return s + "_" + cmd;
    }

    /**
     * 字符串结尾加 end_flag结尾标志
     *
     * @param s
     * @return
     */
    public static String addEnd_flag2Str(String s) {
        return s + "_" + ServerProtocol.END_FLAG;
    }
}
