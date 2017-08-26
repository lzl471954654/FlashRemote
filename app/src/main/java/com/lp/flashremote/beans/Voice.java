package com.lp.flashremote.beans;

import java.util.ArrayList;

/**
 * Created by PUJW on 2017/8/26.
 *
 */

public class Voice {
    public ArrayList<WSBean> ws;
    public class WSBean{
        public ArrayList<CWBean> cw;
    }
    public class CWBean{
        public String w;
    }
}