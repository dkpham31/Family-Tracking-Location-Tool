package com.haroonstudios.familygpstracker.fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.haroonstudios.familygpstracker.activities.HomeScreenActivity;
import com.haroonstudios.familygpstracker.utils.BackPressImpl;
import com.haroonstudios.familygpstracker.utils.OnBackPressListener;


public class RootFragment extends Fragment implements OnBackPressListener {

    private boolean fragmentResume =false;
    private boolean fragmentVisible =false;
    private boolean fragmentOnCreated =false;

    String fragmentTitle = null;

    @Override
    public boolean onBackPressed() {
        return new BackPressImpl(this).onBackPressed();
    }

    @Override
    public RootFragment getFragment() {
        return this;
    }

    public void setTitle(String title) {
        this.fragmentTitle = title;
    }

    public String getTitle() {
        return fragmentTitle;
    }

    public void checkFirstTImeFragmentCreated(){
        if (!fragmentResume && fragmentVisible) {   //only when first time fragment is created
            changeToolbarTitle(false);
        }
    }

    @Override
    public void setUserVisibleHint(boolean visible) {
        super.setUserVisibleHint(visible);
        if (visible && isResumed()) {   // only at fragment screen is resumed
            fragmentResume = true;
            fragmentVisible = false;
            fragmentOnCreated = true;
            changeToolbarTitle(true);
        } else if (visible) {        // only at fragment onCreated
            fragmentResume = false;
            fragmentVisible = true;
            fragmentOnCreated = true;
        } else if (!visible && fragmentOnCreated) {// only when you go out of fragment screen
            fragmentVisible = false;
            fragmentResume = false;
        }
    }

    public void changeToolbarTitle(Boolean onResume) {
        RootFragment fragment = this.getFragment();
        if (onResume) {
            int countChildFragments = fragment.getChildFragmentManager().getBackStackEntryCount();
            while (countChildFragments > 0) {
                fragment = (RootFragment) fragment.getChildFragmentManager().getFragments().get(0);
                countChildFragments = fragment.getChildFragmentManager().getBackStackEntryCount();
            }
        }
        if (fragment.getParentFragment() != null) {
            ((HomeScreenActivity) getActivity()).updateToolBar(fragment, fragment.getTitle(), true);
        } else {
            ((HomeScreenActivity) getActivity()).updateToolBar(fragment, fragment.getTitle(), false);
        }
    }

    public void newFragmentTransection(Fragment fragment, int id){
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(id, fragment); // give your fragment container id in first parameter
        transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
        transaction.commit();
    }


}
