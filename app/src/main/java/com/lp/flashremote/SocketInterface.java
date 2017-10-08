package com.lp.flashremote;

import java.io.InputStream;

/**
 * Created by LZL on 2017/10/8.
 */

public interface SocketInterface {

    public void addMessage(String s);

    public void addBytes(byte[] bytes);

    public String readLine();

    public InputStream getInputStream();
}
