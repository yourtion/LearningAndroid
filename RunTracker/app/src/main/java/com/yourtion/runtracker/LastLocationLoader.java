package com.yourtion.runtracker;

import android.content.Context;
import android.location.Location;

/**
 * Created by Yourtion on 6/2/16.
 */
public class LastLocationLoader extends DataLoader<Location> {
    private long mRunId;

    public LastLocationLoader(Context context, long runId) {
        super(context);
        mRunId = runId;
    }

    @Override
    public Location loadInBackground() {
        return RunManager.get(getContext()).getLastLocationForRun(mRunId);
    }
}
