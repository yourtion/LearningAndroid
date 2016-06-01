package com.yourtion.draganddraw;

import android.graphics.PointF;

/**
 * Created by Yourtion on 6/1/16.
 */
public class Box {
    private PointF mOrigin;
    private PointF mCurrent;
    private int mAngle;

    public Box(PointF origin) {
        mOrigin = mCurrent = origin;
        mAngle = 0;
    }

    public PointF getOrigin() {
        return mOrigin;
    }

    public int getAngle() {
        return mAngle;
    }

    public void setAngle(int angle) {
        mAngle = angle;
    }

    public void setOrigin(PointF origin) {
        mOrigin = origin;
    }

    public PointF getCurrent() {
        return mCurrent;
    }

    public void setCurrent(PointF current) {
        mCurrent = current;
    }
}
