package com.stpauls.dailyliturgy.others;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;

import com.stpauls.dailyliturgy.MainActivity;

public class DrawingImageView extends AppCompatImageView {

    private static final String TAG = "DrawingImageView";

    private PointF point;
    private Paint paint = new Paint();

    public DrawingImageView(Context context) {
        super(context);
    }

    public DrawingImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawingImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        String action = "";

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                getAngle(event);
                action = "DOWN";
                point = new PointF(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                action = "MOVE";
                //point.set(x, y);
                point = null;
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                action = "UP";
                point = null;
                invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
                action = "CANCEL";
                point = null;
                invalidate();
                break;
            default:
                action = "OTHERS";
        }
        return true;
    }

    private void getAngle(MotionEvent event) {
        float touchY3 = event.getY();
        float touchX3 = event.getX();

        int x1 = 0;
        int y1 = (int) (getHeight() / 2.6);

        int cx2 = getWidth() / 2;
        int cy2 = y1;

        get(x1, cx2, Math.round(touchX3), y1, cy2, Math.round(touchY3));

    }


    private void get(int x1, int x2, int x3, int y1, int y2, int y3) {


        double a = Math.sqrt(Math.pow(x2 - x3, 2) +
                Math.pow(y2 - y3, 2)); //  BC = a


        double c = Math.sqrt(Math.pow(x2 - x1, 2) +
                Math.pow(y2 - y1, 2)); //  AB = c

        double b = Math.sqrt(Math.pow(x1 - x3, 2) +
                Math.pow(y1 - y3, 2)); //  AC = b


        double deg180 = Math.toDegrees(Math.acos((a * a + c * c - b * b) / (2 * a * c)));
        double deg360 = y3 > y2 ? 180 + deg180 : deg180;
        if (listener != null) listener.onSectorClick((deg360/ 45));
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        if (point != null) {
            //canvas.drawCircle(point.x, point.y, 10, paint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int squareParam = Math.min(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(squareParam, squareParam);
    }

    private ImageListener listener;

    public void setImageSectorListener(MainActivity customCircle) {
        this.listener = customCircle;
    }

    public interface ImageListener {
        void onSectorClick(double pos);
    }


}