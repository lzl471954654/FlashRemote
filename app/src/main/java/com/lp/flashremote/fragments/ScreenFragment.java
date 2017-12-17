package com.lp.flashremote.fragments;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.lp.flashremote.FlashApplication;
import com.lp.flashremote.R;
import com.lp.flashremote.beans.Content;
import com.lp.flashremote.beans.FileCommand;
import com.lp.flashremote.beans.FileDescribe;
import com.lp.flashremote.beans.PropertiesUtil;
import com.lp.flashremote.beans.UserInfo;
import com.lp.flashremote.utils.Command2JsonUtil;
import com.lp.flashremote.utils.SocketUtil;
import com.lp.flashremote.utils.StringUtil;
import com.lp.flashremote.utils.ToastUtil;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

public class ScreenFragment extends Fragment {
    private String filename;
    public  ProgressBar mPb;
    private ImageView mImage;
    private SocketUtil socket;
    private View rootView;


    private  Handler handler=new MyHandler(this);

    static  class MyHandler extends Handler{
        WeakReference<ScreenFragment> mFragmentReference;

        MyHandler(ScreenFragment fg){
            mFragmentReference=new WeakReference<ScreenFragment>(fg);
        }
        @Override
        public void handleMessage(Message msg) {
            ScreenFragment fileFragment=mFragmentReference.get();

            if (fileFragment != null){
                if (msg.what==0){
                    ToastUtil.toastText(mFragmentReference.get().getActivity(),"接受文件"+msg.obj.toString()+"失败");
                }
                if (msg.what==1){
                    fileFragment.mPb.setVisibility(View.GONE);
                    Bitmap b= BitmapFactory.decodeFile(FlashApplication.acceptFolder+File.separator+msg.obj.toString()+".jpg",null);
                    if (b!=null){
                        fileFragment.mImage.setImageBitmap(b);
                        fileFragment.mImage.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }
    private  String content=null;

    public ScreenFragment(String f){
        this.filename=f;
        socket=SocketUtil.getInstance(UserInfo.getUsername(),UserInfo.getPassword());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView=inflater.inflate(R.layout.screenfragment,container,false);
        initView(rootView);
        socket.addMessage(StringUtil.operateCmd(
                Command2JsonUtil.getJson("2",filename,true)));

        new Thread(new Runnable() {
            @Override
            public void run() {
                content=socket.readLine();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Content c=StringUtil.getContent(content);
                        FileCommand command=new Gson().fromJson(c.getContent(),FileCommand.class);
                        fileOperation(command, c.getContent());
                    }
                });
            }
        }).start();
        return rootView;
    }

    private void fileOperation(FileCommand command, String content) {
        int type = Integer.valueOf(command.getType());
        switch (type) {
            case 21:
                acceptFile(command.getDescribe(), content);
                break;
        }
    }

    private void acceptFile(final FileDescribe[] describes, final String content) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                socket.addMessage(StringUtil.stringAddUnderline(PropertiesUtil.FILE_READY,content));
                for (FileDescribe describe : describes) {
                    String fileName = describe.getFileName() + "." + describe.getFileType();
                    Long fileSize = describe.getFileSize();
                    int count = 0;
                    long size = 0;
                    File file = new File(FlashApplication.acceptFolder+
                            File.separator+fileName);
                    FileOutputStream outputStream = null;
                    BufferedInputStream inputStream = null;
                    try {
                        byte[] bytes = new byte[4096];
                        outputStream = new FileOutputStream(file);
                        inputStream = new BufferedInputStream(socket.socketInput);
                        while ((count = inputStream.read(bytes)) != -1) {
                            outputStream.write(bytes, 0, count);
                            outputStream.flush();
                            size += count;
                            if (size >= fileSize)
                                break;
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (outputStream != null)
                                outputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    Message m=new Message();
                    m.obj=fileName;
                    if (size>=fileSize){
                        m.what=1;
                    }else{
                        m.what=0;
                    }
                    handler.sendMessage(m);

                }
            }
        }).start();
    }

    private void initView(View rootView) {
        mPb=rootView.findViewById(R.id.progressbar);
        mImage=rootView.findViewById(R.id.operation);
        mPb.setVisibility(View.VISIBLE);
    }
}
