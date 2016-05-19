package com.yourtion.criminalintent;

import android.app.Fragment;

/**
 * Created by Yourtion on 5/19/16.
 */
public class CrimeCameraActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new CrimeCameraFragment();
    }
}
