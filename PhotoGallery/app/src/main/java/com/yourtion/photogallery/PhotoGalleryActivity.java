package com.yourtion.photogallery;

import android.app.Fragment;

public class PhotoGalleryActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new PhotoGalleryFragment();
    }
}
