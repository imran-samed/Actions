package com.stpauls.dailyliturgy.base;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

public abstract class BaseDialog extends Dialog {

    public BaseDialog(@NonNull Context context) {
        super(context);
    }

    protected void setDimBlur(Window window){
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.dimAmount = 0.5f;
            window.setAttributes(lp);
            window.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
            window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}