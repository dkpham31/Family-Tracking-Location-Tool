package com.haroonstudios.familygpstracker.utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.haroonstudios.familygpstracker.fragments.RootFragment;


public class BackPressImpl implements OnBackPressListener {

    private Fragment parentFragment;

    public BackPressImpl(Fragment parentFragment) {
        this.parentFragment = parentFragment;
    }

    // press back button to back to the previous fragment
    @Override
    public boolean onBackPressed() {

        if (parentFragment == null) return false;

        int childCount = parentFragment.getChildFragmentManager().getBackStackEntryCount();

        if (childCount == 0) {
            return false;

        } else {
            // get the child Fragment
            FragmentManager childFragmentManager = parentFragment.getChildFragmentManager();
            OnBackPressListener childFragment = (OnBackPressListener) childFragmentManager.getFragments().get(0);

            // propagate onBackPressed method call to the child Fragment
            if (!childFragment.onBackPressed()) {
                childFragmentManager.popBackStackImmediate();
            }

            return true;
        }
    }

    // return root fragment of the parent fragment
    @Override
    public RootFragment getFragment() {
        return (RootFragment) parentFragment;
    }
}
