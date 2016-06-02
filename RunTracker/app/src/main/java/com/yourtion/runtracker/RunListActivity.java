package com.yourtion.runtracker;

import android.app.Fragment;

/**
 * Created by Yourtion on 6/2/16.
 */
public class RunListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new RunListFragment();
    }
}
