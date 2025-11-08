package com.stpauls.dailyliturgy.others;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;

import androidx.annotation.Nullable;

import com.stpauls.dailyliturgy.Global;

import java.util.HashMap;

public class ArcDrawable extends Drawable {

    private static final String TAG = "ArcDrawable";

    private int left, right, top, bottom;
    private  Paint[] paints = new Paint[6];
    private HashMap<Path, Paint> pathMap = new HashMap();


    public ArcDrawable() {

        // red paint
        Paint whitePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        whitePaint.setColor(Color.TRANSPARENT);
        paints[0]= whitePaint;

        // green paint
        Paint greenPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        greenPaint.setColor(Color.TRANSPARENT);
        paints[1]= greenPaint;

        // yellow paint
        Paint yellowPaint =new Paint(Paint.ANTI_ALIAS_FLAG);
        yellowPaint.setColor(Color.TRANSPARENT);
        paints[2]= yellowPaint;


        // MAGENTA paint
        Paint magentaPaint =new Paint(Paint.ANTI_ALIAS_FLAG);
        magentaPaint.setColor(Color.TRANSPARENT);
        paints[3]= magentaPaint;


        // CYAN paint
        Paint CYAN =new Paint(Paint.ANTI_ALIAS_FLAG);
        CYAN.setColor(Color.TRANSPARENT);
        paints[4]= CYAN;

        // LTGRAY paint
        Paint LTGRAY =new Paint(Paint.ANTI_ALIAS_FLAG);
        LTGRAY.setColor(Color.TRANSPARENT);
        paints[5]= LTGRAY;
    }

    @Override
    public void draw(Canvas canvas) {

        //----------USE PATHS----------
        /*// Define and use custom  Path
        for (Map.Entry<Path, Paint> entry : pathMap.entrySet()) {
            // Draw Path on respective Paint style
            canvas.drawPath(entry.getKey(),  entry.getValue());

        }*/

        // -------OR use conventional Style---------
        drawArcs(canvas);

    }


    //Same result
    private void drawArcs(Canvas canvas) {
        RectF rectF = new RectF(left, top, right, bottom);

        /*// method 1
        canvas.drawArc (rectF, 90, 45, true,  paints[0]);

        // method 2
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.drawArc (left, top, right, bottom, 0, 45, true, paints[1]);
        }

        // method two with stroke
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.drawArc (left, top, right, bottom, 180, 45, true,  paints[2]);
        }*/

        float swipeAngle = 60F;
        float startAngle = 0F;

        canvas.drawArc(rectF, startAngle += 60F, swipeAngle, true, paints[0]);

        canvas.drawArc(rectF, startAngle += 60F, swipeAngle, true, paints[1]);

        canvas.drawArc(rectF, startAngle += 60F, swipeAngle, true, paints[2]);

        canvas.drawArc(rectF, startAngle += 60F, swipeAngle, true, paints[3]);

        canvas.drawArc(rectF, startAngle += 60F, swipeAngle, true, paints[4]);

        canvas.drawArc(rectF, startAngle += 60F, swipeAngle, true, paints[5]);
        Global.INSTANCE.setPaint(paints[5]);
        Global.INSTANCE.setRectF(rectF);
    }


    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);

        Log.d(TAG, "onBoundsChange: "+ bounds.toString());

        int width = bounds.width();
        int height = bounds.height();

        left = bounds.left;
        right = bounds.right;
        top = bounds.top;
        bottom = bounds.bottom;

        final int size = Math.min(width, height);
        final int centerX = bounds.left + (width / 2);
        final int centerY = bounds.top + (height / 2);

        pathMap.clear();
        //update pathmap using new bounds
        //recreatePathMap(size, centerX, centerY);
        invalidateSelf();
    }


    private Path recreatePathMap(int size, int centerX, int centerY) {

        RectF rectF = new RectF(left, top, right, bottom);

        // first arc
        Path arcPath = new Path();
        arcPath.moveTo(centerX,centerY);
        arcPath.arcTo (rectF, 90, 45);
        arcPath.close();
        // add to draw Map
        pathMap.put(arcPath, paints[0]);

        //second arc
        arcPath = new Path();
        arcPath.moveTo(centerX,centerY);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
          arcPath.arcTo (rectF, 0, 45);
        }
        arcPath.close();
        // add to draw Map
        pathMap.put(arcPath, paints[1]);

        // third arc
        arcPath = new Path();
        arcPath.moveTo(centerX,centerY);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            arcPath.arcTo (rectF, 180, 45);

        }
        arcPath.close();
        // add to draw Map
        pathMap.put(arcPath, paints[2]);

        return arcPath;

    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.UNKNOWN;
    }


}