package com.yourtion.photogallery;

import android.os.HandlerThread;
import android.util.Log;

/**
 * Created by Yourtion on 5/30/16.
 */
public class ThumbnailDownloader<Token> extends HandlerThread {
    private static final String TAG = "ThumbnailDownloader";

    public ThumbnailDownloader() {
        super(TAG);
    }

    public void queueThumbnail(Token token, String url) {
        Log.i(TAG, "Got an URL: " + url);
    }

}
