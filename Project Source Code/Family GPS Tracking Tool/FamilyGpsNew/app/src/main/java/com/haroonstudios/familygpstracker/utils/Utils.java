package com.haroonstudios.familygpstracker.utils;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.regex.Pattern;

public class Utils
{
    public static boolean findDistance(LatLng currentLatLng, LatLng busLatLng)
    {
        float[] results = new float[1];
        Location.distanceBetween(currentLatLng.latitude, currentLatLng.longitude, busLatLng.latitude, busLatLng.longitude, results);
        float distanceInMeters = results[0];
        boolean isWithin5km = distanceInMeters < 500;
        return isWithin5km;
    }
    public static boolean isValid(String email)
    {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }
}
