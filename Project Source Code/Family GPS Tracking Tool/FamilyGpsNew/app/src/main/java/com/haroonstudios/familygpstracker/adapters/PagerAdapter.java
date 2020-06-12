package com.haroonstudios.familygpstracker.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;


import com.haroonstudios.familygpstracker.fragments.HomeFragment;
import com.haroonstudios.familygpstracker.fragments.InviteFriendsFragment;
import com.haroonstudios.familygpstracker.fragments.JoinFragment;
import com.haroonstudios.familygpstracker.fragments.MyCircleFragment;

import java.util.ArrayList;


public class PagerAdapter extends FragmentPagerAdapter
{

    private ArrayList<Fragment> fragments = new ArrayList<>();
    private Fragment currentFragment;
    private Context mCxt;

    public PagerAdapter(FragmentManager fm, Context context) {
        super(fm);

        mCxt = context;

        fragments.clear();

        HomeFragment homeFragmentFragment = new HomeFragment();
        homeFragmentFragment.setTitle("Home");

        fragments.add(homeFragmentFragment);

        JoinFragment joinFragment = new JoinFragment();
        joinFragment.setTitle("Join Circle");

        fragments.add(joinFragment);


        MyCircleFragment myCircleFragment = new MyCircleFragment();
        myCircleFragment.setTitle("My Circle");
        fragments.add(myCircleFragment);

        InviteFriendsFragment inviteFriendsFragment = new InviteFriendsFragment();
        inviteFriendsFragment.setTitle("Invite Friends");

        fragments.add(inviteFriendsFragment);




    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        if (getCurrentFragment() != object) {
            currentFragment = ((Fragment) object);
        }
        super.setPrimaryItem(container, position, object);

    }


    public Fragment getCurrentFragment() {
        return currentFragment;
    }


}
