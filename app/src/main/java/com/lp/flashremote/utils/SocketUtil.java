package com.lp.flashremote.utils;

import com.lp.flashremote.beans.ServerProtocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketUtil {

    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;
    private String username;
    private String password;
    private static SocketUtil mSocketUtil;

    private SocketUtil(String u, String pwd) {
        this.username=u;
        this.password=pwd;
    }

    public static SocketUtil getInstance(String u, String p) {
        if (mSocketUtil == null) {
            mSocketUtil = new SocketUtil(u, p);
        }
        return mSocketUtil;
    }

    public boolean connPc() {
        boolean conn_ok = false;
        try {
            socket = new Socket(ServerProtocol.SERVER_IP, 10086);
            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();
            writer = new PrintWriter(new OutputStreamWriter(outputStream));
            reader = new BufferedReader(new InputStreamReader(inputStream));
            writer.println(StringUtil.stringAddUnderline(ServerProtocol.CONNECTED_TO_USER,
                    username,password,ServerProtocol.END_FLAG));
            writer.flush();
            String result = StringUtil.readLine(reader);

            if (StringUtil.startAndEnd(result)) {
                String conn = StringUtil.readLine(reader);
                if (StringUtil.isBind(conn)) {//绑定成功

                    conn_ok = true;
                } else if (conn.equals("|CONNECTED@FAILED|")) {//绑定失败
                    conn_ok = false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return conn_ok;
    }
    private void sendmessage(String m) {
        writer.write(m);
        writer.flush();
    }
}
