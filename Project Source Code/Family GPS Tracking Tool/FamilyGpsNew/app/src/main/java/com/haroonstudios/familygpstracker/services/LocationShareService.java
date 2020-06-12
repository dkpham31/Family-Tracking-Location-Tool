package com.haroonstudios.familygpstracker.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.haroonstudios.familygpstracker.R;
import com.haroonstudios.familygpstracker.activities.HomeScreenActivity;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LocationShareService extends Service implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    public LocationShareService() {
    }

    GoogleApiClient client;
    LocationRequest request;
    LatLng latLngCurrent;
    DatabaseReference reference;
    FirebaseAuth auth;
    FirebaseUser user;
    public Notification.Builder builder123;


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
       return null;

    }

    @Override
    public void onCreate() {
        super.onCreate();
        reference = FirebaseDatabase.getInstance().getReference().child("Users");
        auth = FirebaseAuth.getInstance();

        displayNotifications();

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        request = new LocationRequest().create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(500);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(client, request, this);

        // display notification




    }

    private void displayNotifications()
    {

        showNotits(45002);
    }


    private void showNotits(int download_id) {

        // downloadStarted = true;


        Intent notificationIntent = new Intent(this, HomeScreenActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,notificationIntent,0);



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("channelid1", String.valueOf(download_id), NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("This is description");
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);

            builder123 = new Notification.Builder(getApplicationContext(), notificationChannel.getId());
            builder123.setSmallIcon(R.mipmap.ic_launcher)
                    .setContentText("You are sharing your location")
                    .setWhen(System.currentTimeMillis())
                    .setContentIntent(pendingIntent)
                    .setContentTitle("Family GPS Tracker");

            NotificationManagerCompat nmc = NotificationManagerCompat.from(getApplicationContext());
            nmc.notify(download_id, builder123.build());
        } else {

            builder123 = new Notification.Builder(getApplicationContext());
            builder123.setSmallIcon(R.drawable.app_icon)
                    .setWhen(System.currentTimeMillis())
                    .setContentIntent(pendingIntent)
                    .setContentText("You are sharing your location")
                    .setContentTitle("FamTracker");

            NotificationManagerCompat nmc = NotificationManagerCompat.from(getApplicationContext());
            nmc.notify(download_id, builder123.build());
        }


    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        latLngCurrent = new LatLng(location.getLatitude(), location.getLongitude());
        shareLocation();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        user = auth.getCurrentUser();
        client = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        client.connect();


        startForeground(45002,builder123.build());

        return START_NOT_STICKY;
    }

    public void shareLocation()
    {
        Date date = new Date();

        SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MMM-yyyy hh:mm", Locale.getDefault());
        String myDate = sdf1.format(date);

        reference.child(user.getUid()).child("issharing").setValue("true");
        reference.child(user.getUid()).child("date").setValue(myDate);
        reference.child(user.getUid()).child("lat").setValue(String.valueOf(latLngCurrent.latitude));
        reference.child(user.getUid()).child("lng").setValue(String.valueOf(latLngCurrent.longitude))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(!task.isSuccessful())
                        {
                            Toast.makeText(getApplicationContext(),"Could not share Location.",Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }


    @Override
    public void onDestroy() {

        LocationServices.FusedLocationApi.removeLocationUpdates(client,this);
        client.disconnect();
        Log.d("destroy123","destroyed service");

        NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        nm.cancel(45002);
        stopForeground(true);
        stopSelf();
    }
}
