package com.haroonstudios.familygpstracker.utils;


import com.haroonstudios.familygpstracker.fragments.RootFragment;

/**
 * Created by Farhan Ijaz on 6/6/14.
 */
public interface OnBackPressListener {

    public boolean onBackPressed();

    public RootFragment getFragment();

}
