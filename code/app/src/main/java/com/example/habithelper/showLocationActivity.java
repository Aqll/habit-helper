package com.example.habithelper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class showLocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private FirebaseUser user;
    private HabitEvent habitEvent;
    private String date;
    private String comment;
    private Double Lat;
    private Double Long;
    private String address;


    private final static int PLACE_PICKER_REQUEST = 999;
    private final static int LOCATION_REQUEST_CODE = 23;
    private Marker marker;
    private LatLng markerPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_picker);

        //Setting up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_location);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("View/Edit Location");

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            address = extras.getString("address");
            Lat = extras.getDouble("lat");
            Long = extras.getDouble("long");
            date = extras.getString("date");
            comment = extras.getString("comment");
            user = (FirebaseUser) extras.get("currentUser");
            habitEvent = (HabitEvent) extras.getSerializable("habitEvent");

        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // requesting location permission
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                LOCATION_REQUEST_CODE);

    }

    /**
     * Check location permission provided by user and displays map if permission granted
     * @param requestCode
     *      The permission's request code
     * @param permissions
     *      permissions granted
     * @param grantResults
     *        List of all permissions granted
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LOCATION_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // location permission granted

                    // animate selected location/default location at centre of screen
                    if (Lat != 0 & Long != 0) {
                        mMap.clear();
                        markerPosition = new LatLng(Lat, Long);
                        marker = mMap.addMarker(new MarkerOptions()
                                .position(markerPosition)
                                .title(address));

                        CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
                                markerPosition, 15);
                        mMap.animateCamera(location);
                    } else {
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(getApplicationContext(), "Location permission not given", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }
                        mMap.setMyLocationEnabled(true);

                        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                            @Override
                            public void onMyLocationChange(Location location) {
                                LatLng ltlng = new LatLng(location.getLatitude(), location.getLongitude());
                                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                                        ltlng, 16f);
                                mMap.animateCamera(cameraUpdate);
                            }
                        });
                    }

                    // if a location on map is clicked, display dialog fragment to confirm location selected
                    mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(LatLng latLng) {
                            markerPosition = latLng;
                            MarkerOptions markerOptions = new MarkerOptions();
                            markerOptions.position(latLng);

                            address = getAddress(latLng);
                            Lat = markerPosition.latitude;
                            Long = markerPosition.longitude;
                            markerOptions.title(address);
                            mMap.clear();
                            CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
                                    latLng, 15);
                            mMap.animateCamera(location);
                            mMap.addMarker(markerOptions);
                        }
                    });



                } else {
                    // permission denied, Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getApplicationContext(), "Location permission not given", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
                return;
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    /**
     * Given the latitude and longitude of a point on the map, it returns the address that corresponds
     * to this point.
     * @param latLng
     *      an object that contains the latitude and longitude of a point
     * @return
     *      the address of the latitude and longitude of a point on the map
     */
    private String getAddress(LatLng latLng){

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            String address = addresses.get(0).getAddressLine(0);
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            Fragment prev = getFragmentManager().findFragmentByTag("dialog");
            if (prev != null) {

                ft.remove(prev);
            }
            ft.addToBackStack(null);
            DialogFragment dialogFragment = new ConfirmAddressFragment();

            Bundle args = new Bundle();
            args.putDouble("lat", latLng.latitude);
            args.putDouble("long", latLng.longitude);
            args.putString("address", address);

            dialogFragment.setArguments(args);
            dialogFragment.show(ft, "dialog");
            return address;
        } catch (IOException e) {
            e.printStackTrace();
            return "No Address Found";

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.view_location_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            //When cancel is selected in the menu
            case R.id.goBack:
                onBackPressed();
                return true;

            case R.id.done:

                Intent intent = new Intent(showLocationActivity.this, ViewHabitEventsActivity.class);
                intent.putExtra("lat", Lat);
                intent.putExtra("long", Long);
                intent.putExtra("address", address);
                intent.putExtra("classFrom", showLocationActivity.class.toString());
                intent.putExtra("habitEvent", habitEvent);
                intent.putExtra("date", date);
                intent.putExtra("comment", comment);
                intent.putExtra("currentUser", user);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();

        }
        return super.onOptionsItemSelected(item);
    }
}
