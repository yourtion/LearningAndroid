package com.yourtion.runtracker;

import android.content.Context;

/**
 * Created by Yourtion on 6/2/16.
 */
public class RunLoader extends DataLoader<Run> {
    private long mRunId;

    public  RunLoader(Context context,long runId) {
        super(context);
        mRunId = runId;
    }

    @Override
    public Run loadInBackground() {
        return RunManager.get(getContext()).getRun(mRunId);
    }
}
