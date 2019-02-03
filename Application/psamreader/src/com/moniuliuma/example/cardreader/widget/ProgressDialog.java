
package com.moniuliuma.example.cardreader.widget;

import com.moniuliuma.example.cardreader.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

public class ProgressDialog extends Dialog {
    private static final String TAG = ProgressDialog.class.getSimpleName();
    private TextView mShowMsgTextView;
    private LayoutParams params = null;
    public ProgressDialog(Context context) {
        super(context, R.style.proDialog);
        setContentView(R.layout.progress_dialog);
        mShowMsgTextView = (TextView) findViewById(R.id.showmsg);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        params = getWindow().getAttributes();
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        params.width = (int) (display.getWidth() * 0.2);        
        params.height = LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(params);
    }

    public void show(int msg) {
        setMsgText(msg);
        show();
    }

    public void setMsgText(int msg) {
    	if(msg == 0){
    		mShowMsgTextView.setVisibility(View.GONE);
    		params.width = LayoutParams.WRAP_CONTENT;
    	}else{
            mShowMsgTextView.setVisibility(View.VISIBLE);
    		mShowMsgTextView.setText(msg);
    	}
    }
}
