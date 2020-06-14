package com.haroonstudios.familygpstracker.fragments;

import android.Manifest;
import android.animation.IntEvaluator;
import android.animation.ValueAnimator;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.haroonstudios.familygpstracker.R;
import com.haroonstudios.familygpstracker.services.LocationShareService;

import java.util.List;

public class HomeFragment extends RootFragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener, LocationListener, ResultCallback {

    // declare the mMap using google map, marker with latitude and longitude, database Reference on Firebase authentity and user
    GoogleMap mMap;
    GoogleApiClient client;
    LocationRequest request;
    LatLng latLngCurrent;
    Marker marker;
    DatabaseReference reference;
    private ValueAnimator lastPulseAnimator;
    FirebaseAuth auth;
    FirebaseUser user;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        return view;
    }

    // display support map
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        reference = FirebaseDatabase.getInstance().getReference().child("Users");
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    // call API location services
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        client = new GoogleApiClient.Builder(getContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        client.connect();
    }

    // check permission for the locaiton services displayed the map
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        request = new LocationRequest().create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(7000);

        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(client, request, this);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(request);
        builder.setAlwaysShow(true);
        PendingResult result =
                LocationServices.SettingsApi.checkLocationSettings(
                        client,
                        builder.build()
                );
        result.setResultCallback(this);  // dialog for location
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onResult(@NonNull Result result) {
        final Status status = result.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                break;

            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                try {


                    status.startResolutionForResult(getActivity(), 100);

                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
                break;



            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:

                break;
        }
    }

    @Override
    public void onLocationChanged(Location location)
    {

        latLngCurrent = new LatLng(location.getLatitude(), location.getLongitude());
        if (marker == null) {
            marker = mMap.addMarker(new MarkerOptions().position(latLngCurrent).title("Current Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.current_location_pointer)));

            CameraPosition cameraPosition =
                    new CameraPosition.Builder()
                            .target(latLngCurrent)
                            .bearing(43)
                            .tilt(90)
                            .zoom(14.0f)
                            .build();
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            createRippleEffect(latLngCurrent);


        } else {
            marker.setPosition(latLngCurrent);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngCurrent, 13));
        }

    }


    private void createRippleEffect(final LatLng sourceLocation) {

        // First ripple effect
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                final GroundOverlay groundOverlay11 = mMap.addGroundOverlay(new
                        GroundOverlayOptions()
                        .position(sourceLocation, 2000)
                        .transparency(0.6f)
                        .image(BitmapDescriptorFactory.fromResource(R.drawable.riple_icon)));
                OverLay(groundOverlay11, 6000);
            }
        }, 0);

        //Second ripple effect
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                final GroundOverlay groundOverlay12 = mMap.addGroundOverlay(new
                        GroundOverlayOptions()
                        .position(sourceLocation, 2000)
                        .transparency(0.6f)
                        .image(BitmapDescriptorFactory.fromResource(R.drawable.riple_icon)));
                OverLay(groundOverlay12, 5000);
            }
        }, 0);

        //Third ripple effect
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                final GroundOverlay groundOverlay13 = mMap.addGroundOverlay(new
                        GroundOverlayOptions()
                        .position(sourceLocation, 200)
                        .transparency(0.6f)
                        .image(BitmapDescriptorFactory.fromResource(R.drawable.riple_icon)));
                OverLay(groundOverlay13, 4000);
            }
        }, 0);

    }



    public void OverLay(final GroundOverlay groundOverlay, int duration) {
        lastPulseAnimator = ValueAnimator.ofInt(0, 1500);
        int r = 99999;
        lastPulseAnimator.setRepeatCount(r);
        //lastPulseAnimator.setIntValues(0, 500);
        lastPulseAnimator.setDuration(duration);
        lastPulseAnimator.setEvaluator(new IntEvaluator());
        lastPulseAnimator.setInterpolator(new LinearInterpolator());
        lastPulseAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedFraction = valueAnimator.getAnimatedFraction();
                Integer i = (Integer) valueAnimator.getAnimatedValue();
                groundOverlay.setDimensions(i);
                groundOverlay.setTransparency(animatedFraction);
            }
        });
        lastPulseAnimator.start();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.my_navigation_tutorial, menu);
    }


    // 2 case for user. case1 user sharing their location and case2 user stop sharing with notice messages as well
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_send:

                if(isServiceRunning(getContext(), LocationShareService.class))
                {
                    Toast.makeText(getContext(),"You are already sharing your location.",Toast.LENGTH_SHORT).show();
                }
                else
                {

                    Intent myIntent = new Intent(getContext(),LocationShareService.class);
                    getContext().startService(myIntent);
                }


                break;
            case R.id.action_stop:
                Intent myIntent2 = new Intent(getContext(),LocationShareService.class);
                getContext().stopService(myIntent2);

                reference.child(user.getUid()).child("issharing").setValue("false")
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {
                                    Toast.makeText(getContext(),"Location sharing is now stopped",Toast.LENGTH_SHORT).show();

                                }
                                else
                                {
                                    Toast.makeText(getContext(),"Location sharing could not be stopped",Toast.LENGTH_SHORT).show();
                                }

                            }
                        });

                break;


        }
        return super.onOptionsItemSelected(item);
    }


    public boolean isServiceRunning(Context c, Class<?> serviceClass)
    {
        ActivityManager activityManager = (ActivityManager)c.getSystemService(Context.ACTIVITY_SERVICE);


        List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);



        for(ActivityManager.RunningServiceInfo runningServiceInfo : services)
        {
            if(runningServiceInfo.service.getClassName().equals(serviceClass.getName()))
            {
                return true;
            }
        }

        return false;


    }


}
