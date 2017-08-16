package com.lp.flashremote.views;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.lp.flashremote.R;

/**
 * Created by PUJW on 2017/8/16.
 */

public class CodeDialog extends Dialog {

    public CodeDialog(@NonNull Context context) {
        super(context);
    }

    public CodeDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

   public static class Builder{
       private Context mContext;
       private Bitmap bitmap;

       public void setBitmap(Bitmap bitmap) {
           this.bitmap = bitmap;
       }

       public Bitmap getBitmap() {
           return bitmap;
       }
       public Builder(Context context){
           this.mContext=context;
       }
       public CodeDialog create(){
           LayoutInflater layoutInflater=(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
           CodeDialog codeDialog=new CodeDialog(mContext, R.style.Dialog);
           View v=layoutInflater.inflate(R.layout.code2image,null);
           codeDialog.addContentView(v,new WindowManager.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT
                   , android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
           codeDialog.setContentView(v);
           ImageView img = (ImageView)v.findViewById(R.id.img_qrcode);
           img.setImageBitmap(getBitmap());
           return codeDialog;
       }
   }
}
