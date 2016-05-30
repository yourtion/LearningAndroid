package com.yourtion.photogallery;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.util.LruCache;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Yourtion on 5/30/16.
 */
public class ThumbnailDownloader<Token> extends HandlerThread {
    private static final String TAG = "ThumbnailDownloader";
    private static final int MESSAGE_DOWNLOAD = 0;
    private static final int MESSAGE_PREDOWNLOAD = 1;
    private static final int MEM_MAX_SIZE = 32 * 1024 * 1024;// MEM 32MB
    Handler mHandler;
    Map<Token, String> requestMap = Collections.synchronizedMap(new HashMap<Token, String>());
    Handler mResponseHandler;
    Listener<Token> mListener;
    private LruCache<String, Bitmap> mMemoryCache = null;

    public ThumbnailDownloader(Handler responseHandler) {
        super(TAG);
        mResponseHandler = responseHandler;
    }

    public void setListener(Listener<Token> listener) {
        mListener = listener;
    }

    @SuppressLint("HandlerLeak")
    protected void onLooperPrepared() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MESSAGE_DOWNLOAD) {
                    @SuppressWarnings("unchecked")
                    Token token = (Token) msg.obj;
                    Log.i(TAG, "Got a request for url: " + requestMap.get(token));
                    handleRequest(token);
                }
                if (msg.what == MESSAGE_PREDOWNLOAD) {
                    @SuppressWarnings("unchecked")
                    String url = (String) msg.obj;
                    Log.i(TAG, "Got a predownload request for url: " + url);
                    getBitmap(url);
                }
            }
        };

        mMemoryCache = new LruCache<String, Bitmap>(MEM_MAX_SIZE) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }

            @Override
            protected void entryRemoved(boolean evicted, String key,
                                        Bitmap oldValue, Bitmap newValue) {
                super.entryRemoved(evicted, key, oldValue, newValue);
            }
        };
    }

    public void preloadThumbnail(String url) {
        mHandler.obtainMessage(MESSAGE_PREDOWNLOAD, url).sendToTarget();
    }

    public void queueThumbnail(Token token, String url) {
        synchronized (mMemoryCache) {
            final Bitmap bitmap = mMemoryCache.get(url);
            if (bitmap != null && !bitmap.isRecycled()) {
                mListener.onThumbnailDownloaded(token, bitmap);
                return;
            }
        }
        requestMap.put(token, url);
        mHandler.obtainMessage(MESSAGE_DOWNLOAD, token).sendToTarget();
    }

    private Bitmap getBitmap(String url) {
        if (url == null) return null;
        synchronized (mMemoryCache) {
            Bitmap bitmap = mMemoryCache.get(url);
            if (bitmap != null && !bitmap.isRecycled()) {
                Log.i(TAG, "Get Bitmap from cache: " + url);
                return bitmap;
            }
        }
        try {
            byte[] bitmapBytes = new FlickrFetchr().getUrlBytes(url);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
            synchronized (mMemoryCache) {
                mMemoryCache.put(url, bitmap);
            }
            Log.i(TAG, "Created Bitmap from url: " + url);
            return bitmap;
        } catch (IOException ioe) {
            Log.e(TAG, "Error downloading image", ioe);
            return null;
        }
    }

    private void handleRequest(final Token token) {
        final String url = requestMap.get(token);
        if (url == null) return;

        final Bitmap bitmap = getBitmap(url);
        mResponseHandler.post(new Runnable() {
            @Override
            public void run() {
                if (requestMap.get(token) != url) return;
                if (bitmap == null) return;
                requestMap.remove(token);
                mListener.onThumbnailDownloaded(token, bitmap);
            }
        });
    }

    public void clearQueue() {
        mHandler.removeMessages(MESSAGE_DOWNLOAD);
        mHandler.removeMessages(MESSAGE_PREDOWNLOAD);
        requestMap.clear();
    }

    public interface Listener<Token> {
        void onThumbnailDownloaded(Token token, Bitmap thumbnail);
    }

}
