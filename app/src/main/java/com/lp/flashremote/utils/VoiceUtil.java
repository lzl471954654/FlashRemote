package com.lp.flashremote.utils;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.gson.Gson;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.lp.flashremote.beans.ServerProtocol;
import com.lp.flashremote.beans.Voice;

import java.util.ArrayList;

/**
 * Created by PUJW on 2017/8/26.
 * 语音识别和解析
 */

public class VoiceUtil {
    String result ="";
    private static final String appip="=59a1265e";

    private Context mcontext=null;
    private SocketUtil mSocket;

    private  static VoiceUtil voiceId=null;

    public static VoiceUtil getInstance(){
        if (voiceId==null){
            voiceId=new VoiceUtil();
        }
        return voiceId;
    }

    public void setMcontext(Context context,SocketUtil socket) {
        if (mcontext==null){
            this.mcontext = context;
            this.mSocket=socket;
        }
    }

    public void discern(){
        SpeechUtility.createUtility(mcontext, SpeechConstant.APPID+appip);
        RecognizerDialog iatDialog=new RecognizerDialog(mcontext,mInitListener);
        iatDialog.setParameter(SpeechConstant.LANGUAGE,"zh_cn");
        iatDialog.setParameter(SpeechConstant.ACCENT,"mandarin");
        iatDialog.setListener(new RecognizerDialogListener() {
            @Override
            public void onResult(RecognizerResult recognizerResult, boolean b) {
                if (!b){
                    result=parse_Voice(recognizerResult.getResultString());
                    if (!TextUtils.isEmpty(result)){
                        mSocket.addMessage(StringUtil.operateCmd("7",
                                StringUtil.operateCmd(result,ServerProtocol.NO_RESULT)));
                    }
                }
            }
            @Override
            public void onError(SpeechError speechError) {
                speechError.getPlainDescription(true);
            }
        });
        iatDialog.show();
    }



    private String parse_Voice(String resultString) {
        Gson gson=new Gson();
        Voice voiceBean=gson.fromJson(resultString,Voice.class);
        StringBuffer sb=new StringBuffer();
        ArrayList<Voice.WSBean> ws=voiceBean.ws;
        for(Voice.WSBean w:ws){
            String word=w.cw.get(0).w;
            sb.append(word);
        }
        return sb.toString();
    }



    private InitListener mInitListener=new InitListener() {
        @Override
        public void onInit(int i) {

            if (i!= ErrorCode.SUCCESS){
                Toast.makeText(mcontext,"初始化失败,错误码为："+i,Toast.LENGTH_SHORT).show();
            }
        }
    };
}
