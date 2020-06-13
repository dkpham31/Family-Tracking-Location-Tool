package com.haroonstudios.familygpstracker.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.haroonstudios.familygpstracker.R;
import com.haroonstudios.familygpstracker.utils.MyGDPR;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class LiveMapActivity extends AppCompatActivity implements OnMapReadyCallback {


    @BindView(R.id.toolbarLiveMap) Toolbar toolbar;

    GoogleMap mMap;
    LatLng friendLatLng;
    String latitude,longitude,name,userid,prevdate,prevImage,myImage,myName,myLat,myLng,myDate;
    Marker marker;
    DatabaseReference reference;

    ArrayList<String> mKeys;
    MarkerOptions myOptions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_map);
        ButterKnife.bind(this);
        MyGDPR.updateConsentStatus(LiveMapActivity.this);
        mKeys = new ArrayList<>();
        Intent intent = getIntent();

        if(intent!=null)
        {
            latitude=intent.getStringExtra("latitude");
            longitude = intent.getStringExtra("longitude");
            name = intent.getStringExtra("name");
            userid = intent.getStringExtra("userid");
            prevdate = intent.getStringExtra("date");
            prevImage = intent.getStringExtra("image");

            toolbar.setTitle(name +"'s" + " Location");
            setSupportActionBar(toolbar);
            if(getSupportActionBar()!=null)
            {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
            }

        }

        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userid);


        SupportMapFragment mapFragment =  (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        myName = dataSnapshot.child("name").getValue(String.class);
                        myLat = dataSnapshot.child("lat").getValue(String.class);
                        myLng = dataSnapshot.child("lng").getValue(String.class);
                        myDate = dataSnapshot.child("date").getValue(String.class);
                        myImage = dataSnapshot.child("profile_image").getValue(String.class);


                        friendLatLng = new LatLng(Double.parseDouble(myLat),Double.parseDouble(myLng));

                        myOptions = new MarkerOptions();
                        myOptions.position(friendLatLng);
                        myOptions.snippet("Last seen: "+myDate);
                        myOptions.title(myName);

                        if(marker == null)
                        {
                            marker = mMap.addMarker(myOptions);
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(friendLatLng,15));
                        }
                        else
                        {
                            marker.setPosition(friendLatLng);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });




            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View row = getLayoutInflater().inflate(R.layout.custom_snippet,null);
                TextView nameTxt = row.findViewById(R.id.snippetName);
                TextView dateTxt = row.findViewById(R.id.snippetDate);
                CircleImageView imageTxt = row.findViewById(R.id.snippetImage);
                if(myName == null && myDate == null)
                {
                    nameTxt.setText(name);
                    dateTxt.setText(dateTxt.getText().toString() + prevdate);
                    Picasso.get().load(prevImage).placeholder(R.drawable.defaultprofile).into(imageTxt);
                }
                else
                {
                    nameTxt.setText(myName);
                    dateTxt.setText(dateTxt.getText().toString() + myDate);
                    Picasso.get().load(myImage).placeholder(R.drawable.defaultprofile).into(imageTxt);
                }


                return row;
            }
        });

        friendLatLng = new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude));
        MarkerOptions optionsnew = new MarkerOptions();

        optionsnew.position(friendLatLng);
        optionsnew.title(name);
        optionsnew.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));


        if(marker == null)
        {
            marker = mMap.addMarker(optionsnew);
        }
        else
        {
            marker.setPosition(friendLatLng);
        }


        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(friendLatLng,15));



    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }
}
