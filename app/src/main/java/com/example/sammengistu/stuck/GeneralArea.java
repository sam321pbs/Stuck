package com.example.sammengistu.stuck;

import android.content.Context;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Gets the users general area
 */
public class GeneralArea {

    private static final String TAG = "GeneralArea";

    public static String getAddressOfCurrentLocation(Location location, Context context) {

        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        String currentLocationAddress = "";


        //Builder to get only the city and state of the user
        StringBuilder builder = new StringBuilder();
        try {
            List<android.location.Address> address =
                new Geocoder(context, Locale.getDefault()).getFromLocation(latitude, longitude, 1);

            if (address.get(0).getLocality() != null){
                builder.append(address.get(0).getLocality());
                builder.append(", ");
            }

            if (address.get(0).getAdminArea() != null) {
                builder.append(address.get(0).getAdminArea());
            }
            currentLocationAddress = builder.toString(); //This is the complete address.
        } catch (IOException e) {
        } catch (NullPointerException e) {
        }

        Log.i(TAG, currentLocationAddress);

        return currentLocationAddress;
    }
}
