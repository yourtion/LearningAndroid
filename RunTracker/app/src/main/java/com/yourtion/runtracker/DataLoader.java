package com.yourtion.runtracker;

import android.content.AsyncTaskLoader;
import android.content.Context;

/**
 * Created by Yourtion on 6/2/16.
 */
public abstract class DataLoader<D> extends AsyncTaskLoader<D> {
    private D mData;

    public DataLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        if (mData != null) {
            deliverResult(mData);
        } else {
            forceLoad();
        }
    }

    @Override
    public void deliverResult(D data) {
        mData = data;
        if (isStarted()) super.deliverResult(data);
    }
}
