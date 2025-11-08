package com.stpauls.dailyliturgy.others;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

public class MyImage extends AppCompatImageView {
    private static final String TAG = "MyImage";
    public MyImage(@NonNull Context context) {
        super(context);
    }

    public MyImage(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyImage(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);

    }
}
