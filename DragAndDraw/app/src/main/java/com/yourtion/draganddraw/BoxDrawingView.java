package com.yourtion.draganddraw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by Yourtion on 6/1/16.
 */
public class BoxDrawingView extends View {
    public static final String TAG = "BoxDrawingView";

    private Box mCurrentBox;
    private ArrayList<Box> mBoxes;
    private Paint mBoxPaint;
    private Paint mBackgroundPaint;

    // Used when creating the view in code
    public BoxDrawingView(Context context) {
        this(context, null);
    }

    // Used when inflating the view from XML
    public BoxDrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Paint the boxes a nice semitransparent red (ARGB)
        mBoxPaint = new Paint();
        mBoxPaint.setColor(0x22ff0000);

        // Paint the background off-white
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(0xfff8efe0);
    }

    public ArrayList<Box> getBoxes() {
        return mBoxes;
    }

    public void setBoxes(ArrayList<Box> boxes) {
        mBoxes = boxes;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Fill the background
        canvas.drawPaint(mBackgroundPaint);

        if (mBoxes == null) mBoxes = new ArrayList<>();

        int angle = 0;

        for (Box box : mBoxes) {
            float left = Math.min(box.getOrigin().x, box.getCurrent().x);
            float right = Math.max(box.getOrigin().x, box.getCurrent().x);
            float top = Math.min(box.getOrigin().y, box.getCurrent().y);
            float bottom = Math.max(box.getOrigin().y, box.getCurrent().y);

            angle = box.getAngle();
            canvas.rotate(angle, left, top);
            canvas.drawRect(left, top, right, bottom, mBoxPaint);
            canvas.rotate(-angle, left, top);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        PointF curr = new PointF(event.getX(), event.getY());

        Log.i(TAG, "Received event at x=" + curr.x + ", y=" + curr.y + ":");

        if (event.getPointerCount() > 2) {
            mBoxes = null;
            mCurrentBox = null;
            invalidate();
            return true;
        }

        if (event.getPointerCount() > 1) {
            int angle = rotate(event);
            if (mCurrentBox != null) mCurrentBox.setAngle(angle);
            Log.i(TAG, "Multitouch event at:" + angle);
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.i(TAG, "ACTION_DOWN");
                // Reset drawing state
                mCurrentBox = new Box(curr);
                mBoxes.add(mCurrentBox);
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i(TAG, "ACTION_MOVE");
                if (mCurrentBox != null) {
                    mCurrentBox.setCurrent(curr);
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.i(TAG, "ACTION_UP");
                mCurrentBox = null;
                break;
            case MotionEvent.ACTION_CANCEL:
                Log.i(TAG, "ACTION_CANCEL");
                mCurrentBox = null;
                break;
        }
        return true;
    }

    private int rotate(MotionEvent event) {
        float deltaX = event.getX(0) - event.getX(1);
        float deltaY = event.getY(0) - event.getY(1);
        double radians = Math.atan(deltaY / deltaX);
        return (int) (radians * 180 / Math.PI);
    }
}
