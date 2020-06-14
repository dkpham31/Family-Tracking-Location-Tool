package com.haroonstudios.familygpstracker.utils;


import com.haroonstudios.familygpstracker.fragments.RootFragment;

public interface OnBackPressListener {

    public boolean onBackPressed();

    public RootFragment getFragment();

}
