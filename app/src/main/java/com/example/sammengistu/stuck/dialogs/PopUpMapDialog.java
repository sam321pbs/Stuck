package com.example.sammengistu.stuck.dialogs;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.example.sammengistu.stuck.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

public class PopUpMapDialog extends DialogFragment {

    private static final String ADDRESS_TO_SHOW_ON_MAP = "Address for map";
    private static final String TAG = "Pop Up";

    private GoogleMap mGoogleMap;

    private MapView mMapView;
    private List<android.location.Address> geocodeMatches;

    private String mAddress;

    @NonNull
    @SuppressWarnings("deprecation")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View v = getActivity().
            getLayoutInflater()
            .inflate(R.layout.fragment_location_info_dialog, null);

        mMapView = (MapView) v.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();// needed to get the map to display immediately

        mAddress = getArguments().getString(ADDRESS_TO_SHOW_ON_MAP);

        TextView title = ((TextView) v.findViewById(R.id.fragment_location_pop_title_of_location));
        title.setText(getArguments().getString(ADDRESS_TO_SHOW_ON_MAP));


        geocodeMatches = null;

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mGoogleMap = mMapView.getMap();

        showAddressOnMap(mAddress);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v);

        builder.setNeutralButton("Cancel", null);

        AlertDialog alertDialog = builder.create();

        alertDialog.show();

        return alertDialog;
    }

    private void showAddressOnMap(String address) {
        double latitude;
        double longitude;

        try {
            geocodeMatches =
                new Geocoder(getActivity()).getFromLocationName(
                    address, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!geocodeMatches.isEmpty()) {
            latitude = geocodeMatches.get(0).getLatitude();
            longitude = geocodeMatches.get(0).getLongitude();

            // Changing marker icon
            mGoogleMap.addMarker(
                new MarkerOptions().position(new LatLng(latitude, longitude))
                    .visible(true));

            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15));
        }
    }

    public static void startGoogleMaps(String address, Activity activity) {

        Uri gmmIntentUri = Uri.parse("geo:0,0?q= " + address);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivity(mapIntent);
        } else {
            Toast.makeText(activity, "Please download google maps",
                Toast.LENGTH_SHORT).show();
        }
    }

    public static PopUpMapDialog newInstance(String address) {
        Bundle bundle = new Bundle();
        bundle.putString(ADDRESS_TO_SHOW_ON_MAP, address);

        PopUpMapDialog popUpMapDialog = new PopUpMapDialog();
        popUpMapDialog.setArguments(bundle);

        return popUpMapDialog;
    }
}
