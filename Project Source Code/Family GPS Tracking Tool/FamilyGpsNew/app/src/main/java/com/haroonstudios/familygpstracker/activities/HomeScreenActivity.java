package com.haroonstudios.familygpstracker.activities;


import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationAdapter;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationViewPager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.haroonstudios.familygpstracker.R;
import com.haroonstudios.familygpstracker.adapters.PagerAdapter;
import com.haroonstudios.familygpstracker.fragments.HomeFragment;
import com.haroonstudios.familygpstracker.utils.MyGDPR;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeScreenActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    // declare BindView for toolbar, horizontal view and bottom navigation
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.vp_horizontal_ntb) AHBottomNavigationViewPager viewPager;
    @BindView(R.id.bottomNavigation) AHBottomNavigation bottomNavigationView;

    // declare navigation view, current fragment, navigation adapter, mypage adapter and the tab colors for homescreen
    private NavigationView navigationView;
    Fragment currentFragment;
    AHBottomNavigationAdapter navigationAdapter;
    PagerAdapter myPagerAdapter;
    private int[] tabColors;

    // set value backtoexit equals false, declare firebase for user
    boolean doubleBackToExitPressedOnce = false;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;

    // I planned to add some add view to earn money for the app, that's why I create MyGDPR activity however it will distract user while using, so I dont put it in my code now
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        ButterKnife.bind(this);

        // requirement of SDK >= 21
        MyGDPR.updateConsentStatus(HomeScreenActivity.this);
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));
        }

        // receive user information from Firebase for authentity
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        // set toolbar and navigation with view page and bottom navigation function
        setToolbar();
        setNavDrawer();

        setViewPager();
        setBottomNav();
        setTabSelectedListener();


        // set up tab color and bottom navigation for the current page
        tabColors = this.getResources().getIntArray(R.array.tab_colors);
        navigationAdapter = new AHBottomNavigationAdapter(this, R.menu.menu_bottom_navigation);
        navigationAdapter.setupWithBottomNavigation(bottomNavigationView, tabColors);

    }


    // set navigation drawer function with creating an action bar, toolbar with comment open or close navigation drawer
    private void setNavDrawer()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        // set value navigation view for id nav_view
        this.navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }


    // set up bottom navigation with background and color
    private void setBottomNav()
    {

        bottomNavigationView.setDefaultBackgroundColor(getResources().getColor(R.color.white));
        bottomNavigationView.setBehaviorTranslationEnabled(true);
        bottomNavigationView.setTitleState(AHBottomNavigation.TitleState.SHOW_WHEN_ACTIVE);
        bottomNavigationView.setUseElevation(true);
        bottomNavigationView.setAccentColor(Color.parseColor("#F44336"));
    }


    // set my page adapter with the current fragment, return value true when current fragment was selected or had my page adapter value
    private void setTabSelectedListener() {
        bottomNavigationView.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {

                if (currentFragment == null) {
                    currentFragment = (HomeFragment) myPagerAdapter.getCurrentFragment();
                }

                if (wasSelected) {

                    return true;
                }

                if (currentFragment != null) {

                }

                viewPager.setCurrentItem(position, false);

                if (currentFragment == null) {
                    return true;
                }

                currentFragment = myPagerAdapter.getCurrentFragment();

                return true;
            }

        });

    }

    // set up toolbar for title Home
    private void setToolbar()
    {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Home");
    }

    // set up off screen page limited 4 pages, return value current fragment for my page adapter
    private void setViewPager()
    {

        myPagerAdapter = new PagerAdapter(getSupportFragmentManager(), HomeScreenActivity.this);
        viewPager.setOffscreenPageLimit(4);
        viewPager.setAdapter(myPagerAdapter);

        currentFragment = myPagerAdapter.getCurrentFragment();

    }

    // update toolbar value, return true set display home if click Back, return false set display home if not
    public void updateToolBar(Fragment fragment, String title, Boolean back) {

        if (back) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
        getSupportActionBar().setTitle(title);
    }


    // I have 2 case on navigation for selection: logout- to log out the app and About me- link with my Facebook account if users want to know about me, contact with me or sth else
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        // case logout
        if (id == R.id.nav_logout) {
            auth.signOut();
            finish();
            Intent intent = new Intent(HomeScreenActivity.this,MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        // case about me
        else if(id == R.id.nav_help)
        {
            Uri uriFb = Uri.parse("https://www.facebook.com/MidouKhoa31"); // missing 'http://' will cause crashed
            Intent intent = new Intent(Intent.ACTION_VIEW, uriFb);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    // back function to get back the previous page or exit
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }


}
