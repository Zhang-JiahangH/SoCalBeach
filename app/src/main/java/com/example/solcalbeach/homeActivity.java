package com.example.solcalbeach;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationRequest;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.solcalbeach.util.Beach;
import com.example.solcalbeach.util.DownloadUrl;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;

import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class homeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback{

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;


    boolean isPermissionGranted;
    private GoogleMap mMap;
    private final LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;

    //    Location currentLocation;
    Location curLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationCallback locationCallback;
    private static final int REQUEST_CODE = 101;

    // Search beaches needed
    List<Beach> nearbyBeaches;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        // Initialize the Tool Bar
        setSupportActionBar(toolbar);


        // Initialize navigation drawer menu
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.black));
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Initialize clickable items in the menu
        navigationView.setNavigationItemSelectedListener(this);

        // Initialize map view
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getCurrentLocation();
        if (isPermissionGranted) {
            SupportMapFragment supportMapFragment = (SupportMapFragment) this.getSupportFragmentManager().findFragmentById(R.id.home_map_view);
            supportMapFragment.getMapAsync(this);
        }



        navigationView.bringToFront();
        toolbar.bringToFront();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return true;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //customize the styling of the base map
        try {
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));
            if (!success) Log.e("Map Styling Error", "Unable to load map style");
        } catch (Resources.NotFoundException e) {
            Log.e("Map styling error", e.toString());
        }

    }

    private void getCurrentLocation() {
        // Check if the required permission has been granted
        // If not, request for permission
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE);
            return;
        }

        isPermissionGranted = true;

        @SuppressLint("MissingPermission") Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @SuppressLint("MissingPermission")
            @Override
            public void onSuccess(Location location) {
                if (task.isSuccessful()) {
                    curLocation = task.getResult();
                    if (curLocation != null) {
                        // Congratulation we don't need to do anything else.
                    } else {
                        // request for the current location if no prior location has been called.
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            LocationRequest.Builder builder = new LocationRequest.Builder(10000);
                            builder.setQuality(LocationRequest.QUALITY_HIGH_ACCURACY);
                            locationCallback = new LocationCallback() {
                                @Override
                                public void onLocationResult(@NonNull LocationResult locationResult) {
                                    super.onLocationResult(locationResult);
                                    if (locationResult == null) {
                                        return;
                                    }
                                    curLocation = locationResult.getLastLocation();
                                }
                            };
                        }
                    }
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(curLocation.getLatitude(),
                                    curLocation.getLongitude()), DEFAULT_ZOOM));

                    // Enable the blue dot for user location
                    mMap.setMyLocationEnabled(true);

                    // Find nearby beaches and initialize them on the map

                    findNearbyBeaches();

                } else {
                    Toast.makeText(homeActivity.this, "unable to get location", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void findNearbyBeaches(){
        if (curLocation == null) {
            Log.e("Error", "current location is null in find nearby beaches.");
        }
        if (!isPermissionGranted) {
            Log.e("Error", "permission not granted in find nearby beaches,");
        }
        Log.d("search starts", "starting searching nearby beaches...");


        String url = "https://maps.googleapis.com/maps/api/place/textsearch/json?" +
                "location=" + curLocation.getLatitude() + "," + curLocation.getLongitude() +
                "&query=beach" +
                "&types=natural_feature" +
                "&key=AIzaSyBNF_W_dJPHr-HGw3YtFCbfMoUcvKdBlSg";


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    initializeBeachMarkers(url);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

        while(nearbyBeaches==null){
            double i = Math.log(309209387);
        }
        while(nearbyBeaches.size()!=6){
            double i = Math.log(309209387);
        }
        // Put the beaches result into
        for(Beach beach : nearbyBeaches){
            mMap.addMarker(new MarkerOptions()
                    .position(beach.getLocation())
                    .title(beach.getName())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        }


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                // Makes all markers clickable
                Log.d("Marker clicked: ", marker.getTitle());
                return false;
            }
        });

    }

    public void initializeBeachMarkers(String url) throws IOException, JSONException {
        nearbyBeaches = new ArrayList<>();
        DownloadUrl downloadUrl = new DownloadUrl();
        String nearby_places_data;
        nearby_places_data = downloadUrl.retrieve_url(url);

        JSONObject jsonObject = new JSONObject(nearby_places_data);
        JSONArray jsonArray = jsonObject.getJSONArray("results");
        for(int i=0; i< 6 ; i++){
            // extract useful information from the json
            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

            JSONObject getLocation = jsonObject1.getJSONObject("geometry").getJSONObject("location");
            String lat = getLocation.getString("lat");
            String lng = getLocation.getString("lng");

            JSONObject getName = jsonArray.getJSONObject(i);
            String name = getName.getString("name");
            LatLng latLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
            Beach beach = new Beach(name,latLng);
            nearbyBeaches.add(beach);
        }
    }
}
