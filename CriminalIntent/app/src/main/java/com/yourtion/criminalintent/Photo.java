package com.yourtion.criminalintent;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Yourtion on 5/20/16.
 */
public class Photo {
    private static final String JSON_FILENAME = "filename";
    private static final String JSON_ROTATE = "rotate";

    private String mFilename;
    private int mRotate;

    /**
     * Create a Photo representing an existing file on disk
     */
    public Photo(String filename, int rotate) {
        mFilename = filename;
        mRotate = rotate;
    }

    public Photo(JSONObject json) throws JSONException {
        mFilename = json.getString(JSON_FILENAME);
        mRotate = json.getInt(JSON_ROTATE);
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(JSON_FILENAME, mFilename);
        json.put(JSON_ROTATE, mRotate);
        return json;
    }

    public String getFilename() {
        return mFilename;
    }

    public int getRotate() {
        return mRotate;
    }

    public void setRotate(int rotate) {
        mRotate = rotate;
    }
}
