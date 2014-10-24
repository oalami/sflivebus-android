package com.firebase.sflivebus;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private HashMap<String, Marker> vehicleMarkerMap = new HashMap<String, Marker>();
    private Firebase ref;
    private LatLngInterpolator interpolator = new LatLngInterpolator.Linear();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
                setupFirebase();
            }
        }
    }

    private void setupFirebase() {
        Firebase.setAndroidContext(this);
        ref = new Firebase("https://publicdata-transit.firebaseio.com/sf-muni");

        ref.child("vehicles").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //do nothing
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Marker m = vehicleMarkerMap.get(dataSnapshot.getName());

                if(m != null) {
                    Map vehicle = dataSnapshot.getValue(Map.class);

                    MarkerAnimation.animateMarkerToICS(m,
                            new LatLng((Double)vehicle.get("lat"), (Double)vehicle.get("lon")),
                            interpolator);
                } else {
                    Map vehicle = dataSnapshot.getValue(Map.class);
                    addRoute(dataSnapshot.getName(),
                            (String) vehicle.get("routeTag"),
                            new LatLng((Double) vehicle.get("lat"), (Double) vehicle.get("lon")));
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Marker m = vehicleMarkerMap.get(dataSnapshot.getName());

                if(m != null) {
                    m.remove();
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                // do nothing
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                // do nothing
            }
        });
    }

    private void addRoute(String id, String routeName, LatLng location) {
        MarkerOptions mo = new MarkerOptions()
                .position(location)
                .title("Vehicle " + id + " on route " + routeName)
                .icon(BitmapDescriptorFactory.fromBitmap(TextMarkerFactory.buildMarker(getApplicationContext(), routeName)));

        Marker m = mMap.addMarker(mo);
        vehicleMarkerMap.put(id, m);

    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
//        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(
//                new LatLngBounds(
//                        new LatLng(37.70977, -122.51692),
//                        new LatLng(37.83569, -122.36654)),0));
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
                new LatLng(37.75771992816863, -122.43760000000003), 13, 0, 0)));
//
//        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }




}
