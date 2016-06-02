package com.yourtion.runtracker;

import android.content.Context;
import android.location.Location;
import android.util.Log;

/**
 * Created by Yourtion on 6/2/16.
 */
public class TrackingLocationReceiver extends LocationReceiver {
    private static final String TAG = "TrackingReceiver";

    @Override
    protected void onLocationReceived(Context c, Location loc) {
        Log.d(TAG, this + " Got location from " + loc.getProvider() + ": " + loc.getLatitude() + ", " + loc.getLongitude());
        RunManager.get(c).insertLocation(loc);
    }
}
