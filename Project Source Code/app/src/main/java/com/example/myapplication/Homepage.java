package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class Homepage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        Toolbar toolbar = findViewById(R.id.toolBar);
        drawer = findViewById(R.id.drawer_layout);
        setSupportActionBar(toolbar);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer,toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmet_container, new Fragment_homepage()).commit();
            navigationView.setCheckedItem(R.id.nav_view);
        }
    }

    /*public void logout (View view){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish(); }*/
    public  void user_profile (View view){
        startActivity(new Intent(getApplicationContext(), UserProfile.class));

    }
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            //case R.id.home:
             //   getSupportFragmentManager().beginTransaction().replace(R.id.fragmet_container, new Fragment_homepage()).commit();
             //   break;
            //case R.id.expedition:
             //   getSupportFragmentManager().beginTransaction().replace(R.id.fragmet_container, new Fragment_expedition()).commit();
             //   break;
            //case R.id.blog:
            //    startActivity(new Intent(HomePage.this, Mylog.class));
            //    break;
            //case R.id.contact:
             //   startActivity(new Intent(HomePage.this, Contact.class));
             //   break;
            //case R.id.donate:
            //    getSupportFragmentManager().beginTransaction().replace(R.id.fragmet_container, new Fragment_donation()).commit();
            //    break;
            case R.id.signout:
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(this, MainActivity.class));
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

