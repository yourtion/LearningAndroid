package com.yourtion.criminalintent;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.view.Display;
import android.widget.ImageView;

/**
 * Created by Yourtion on 5/20/16.
 */
public class PictureUtils {
    /**
     * Get a BitmapDrawable from a local file that is scaled down
     * to fit the current Window size.
     */

    @SuppressWarnings("deprecation")
    public static BitmapDrawable getScaledDrawable(Activity a, String path, int rorate) {
        Display display = a.getWindowManager().getDefaultDisplay();
        float destWidth = display.getWidth();
        float destHeight = display.getHeight();

        // Read in the dimensions of the image on disk
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        float scrWidth = options.outWidth;
        float scrHeight = options.outHeight;

        int inSampleSize = 1;
        if (scrHeight > destHeight || scrWidth > destWidth) {
            if (scrWidth > scrHeight) {
                inSampleSize = Math.round(scrHeight / destHeight);
            } else {
                inSampleSize = Math.round(scrWidth / destWidth);
            }
        }

        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        Bitmap bitmap = BitmapFactory.decodeFile(path, options);

        if (rorate != 0) {
            bitmap = applyRotate(bitmap, rorate);
        }

        return new BitmapDrawable(a.getResources(), bitmap);
    }

    public static Bitmap applyRotate(Bitmap bitmap, int rotate) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix mtx = new Matrix();
        mtx.postRotate(rotate);
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
    }

    public static void cleanImageView(ImageView imageView) {
        if (!(imageView.getDrawable() instanceof BitmapDrawable)) return;

        // Clean up the view's image for the sake of memory
        BitmapDrawable b = (BitmapDrawable) imageView.getDrawable();
        if (b != null){
            b.getBitmap().recycle();
            imageView.setImageDrawable(null);
        }
    }
}
