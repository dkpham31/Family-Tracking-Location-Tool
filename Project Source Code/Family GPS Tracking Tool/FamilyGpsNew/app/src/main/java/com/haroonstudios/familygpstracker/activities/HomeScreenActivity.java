package com.haroonstudios.familygpstracker.activities;

import android.content.ActivityNotFoundException;
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
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.haroonstudios.familygpstracker.R;
import com.haroonstudios.familygpstracker.adapters.PagerAdapter;
import com.haroonstudios.familygpstracker.fragments.HomeFragment;
import com.haroonstudios.familygpstracker.utils.MyGDPR;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeScreenActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{


    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.vp_horizontal_ntb) AHBottomNavigationViewPager viewPager;
    @BindView(R.id.bottomNavigation) AHBottomNavigation bottomNavigationView;
   // @BindView(R.id.adViewMainScreen) AdView adView;


    private NavigationView navigationView;
    Fragment currentFragment;
    AHBottomNavigationAdapter navigationAdapter;
    PagerAdapter myPagerAdapter;
    private int[] tabColors;

    boolean doubleBackToExitPressedOnce = false;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        ButterKnife.bind(this);

        MyGDPR.updateConsentStatus(HomeScreenActivity.this);
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));
        }

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        setToolbar();
        setNavDrawer();

        setViewPager();
        setBottomNav();
        setTabSelectedListener();

     //   adView.loadAd(new AdRequest.Builder()
     //           .addNetworkExtrasBundle(AdMobAdapter.class, MyGDPR.getBundleAd(this)).build());


        tabColors = this.getResources().getIntArray(R.array.tab_colors);
        navigationAdapter = new AHBottomNavigationAdapter(this, R.menu.menu_bottom_navigation);
        navigationAdapter.setupWithBottomNavigation(bottomNavigationView, tabColors);

    }


    private void setNavDrawer()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        this.navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }


    private void setBottomNav()
    {

        bottomNavigationView.setDefaultBackgroundColor(getResources().getColor(R.color.white));
        bottomNavigationView.setBehaviorTranslationEnabled(true);
        bottomNavigationView.setTitleState(AHBottomNavigation.TitleState.SHOW_WHEN_ACTIVE);
        bottomNavigationView.setUseElevation(true);
        bottomNavigationView.setAccentColor(Color.parseColor("#F44336"));
    }




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

    private void setToolbar()
    {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Home");
    }

    private void setViewPager()
    {

        myPagerAdapter = new PagerAdapter(getSupportFragmentManager(), HomeScreenActivity.this);
        viewPager.setOffscreenPageLimit(4);
        viewPager.setAdapter(myPagerAdapter);

        currentFragment = myPagerAdapter.getCurrentFragment();

    }

    public void updateToolBar(Fragment fragment, String title, Boolean back) {

        if (back) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
        getSupportActionBar().setTitle(title);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_logout) {
            auth.signOut();
            finish();
            Intent intent = new Intent(HomeScreenActivity.this,MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

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
