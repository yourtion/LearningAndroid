package com.yourtion.photogallery;

import android.app.Fragment;

/**
 * Created by Yourtion on 5/31/16.
 */
public class PhotoPageActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new PhotoPageFragment();
    }
}
