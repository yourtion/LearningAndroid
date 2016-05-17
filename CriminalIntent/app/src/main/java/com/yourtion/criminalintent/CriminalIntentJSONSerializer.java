package com.yourtion.criminalintent;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

/**
 * Created by Yourtion on 5/17/16.
 */
public class CriminalIntentJSONSerializer {
    private static final String TAG = "CrimeJSONSerializer";

    private Context mContext;
    private String mFilename;

    public CriminalIntentJSONSerializer(Context c, String f) {
        mContext = c;
        mFilename = f;
    }

    private File getSDFile(String filename) {
        if (android.os.Environment.isExternalStorageEmulated()) {
            File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File(sdCard.getAbsolutePath() + "/Crimes/");
            dir.mkdirs();
            File file = new File(dir, filename);
            if (file.canWrite()) return file;
        }
        return null;
    }

    public ArrayList<Crime> loadCrimes() throws IOException, JSONException {
        ArrayList<Crime> crimes = new ArrayList<Crime>();
        BufferedReader reader = null;
        try {
            // Open and read the file into a SringBuilder
            InputStream in;
            File sdFile = getSDFile(mFilename);
            if (sdFile != null) {
                in = new FileInputStream(sdFile);
                Log.d(TAG, "Read from SD");
            } else {
                in = mContext.openFileInput(mFilename);
                Log.d(TAG, "Read from Local");
            }

            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder jsonString = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                // Line breaks are omitted and irrelevant
                jsonString.append(line);
            }
            // Parse the JSON using JSONTokener
            JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
            // Build the array of crimes from JSONObjects
            for (int i = 0; i < array.length(); i++) {
                crimes.add(new Crime(array.getJSONObject(i)));
            }
        } catch (FileNotFoundException e) {
            // Ignore this one; it happens when starting fresh
        } finally {
            if (reader != null) reader.close();
        }
        return crimes;
    }

    public void saveCrimes(ArrayList<Crime> crimes) throws JSONException, IOException {
        // Build an array in JSON
        JSONArray array = new JSONArray();
        for (Crime c : crimes) {
            array.put(c.toJSON());
        }
        // Write the file to disk
        Writer writer = null;
        try {
            OutputStream out;
            File sdFile = getSDFile(mFilename);
            if (sdFile != null) {
                out = new FileOutputStream(sdFile);
                Log.d(TAG, "Write to SD");
            } else {
                out = mContext.openFileOutput(mFilename, Context.MODE_PRIVATE);
                Log.d(TAG, "Write to Local");
            }

            writer = new OutputStreamWriter(out);
            writer.write(array.toString());
        } finally {
            if (writer != null) writer.close();
        }
    }

}
