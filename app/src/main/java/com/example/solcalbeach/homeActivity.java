package com.example.solcalbeach;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationRequest;
import android.media.Rating;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
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
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;

import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class homeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback{

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    // using a hashmap to store all on-map markers for operations.
    List<Marker> allBeachMarkers= new ArrayList<>();
    List<Marker> allRestaurantMarkers = new ArrayList<>();
    List<Marker> allParkingMarkers = new ArrayList<>();

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
    Circle circle;

    // Search Restaurants needed
    double restaurantRange = 304.8;     // default for 1000 feet.

    // popup window needed
    TextView tvPopupName, tvPopupRating;
    ImageButton btnPopupBack;
    Button btnDirection, btnRestaurant, btnSubmit, btnHomeBack, btnRange1000, btnRange2000, btnRange3000;
    CheckBox cbAnonymous;
    RatingBar ratingBar;




    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        btnHomeBack = (Button) findViewById(R.id.Btn_home_back);
        btnHomeBack.setVisibility(View.INVISIBLE);
        btnRange1000 = (Button) findViewById(R.id.Btn_home_range_1000);
        btnRange2000 = (Button) findViewById(R.id.Btn_home_range_2000);
        btnRange3000 = (Button) findViewById(R.id.Btn_home_range_3000);
        btnRange1000.setVisibility(View.INVISIBLE);
        btnRange2000.setVisibility(View.INVISIBLE);
        btnRange3000.setVisibility(View.INVISIBLE);

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
        // Put the found beaches as markers onto map and add them in hashmap.
        for(Beach beach : nearbyBeaches){
            Marker curMarker = mMap.addMarker(new MarkerOptions()
                    .position(beach.getLocation())
                    .title(beach.getName())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            allBeachMarkers.add(curMarker);
        }


        // Makes all markers clickable
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                for(Beach beach : nearbyBeaches){
                    if(beach.getName().equals(marker.getTitle())) {
                        // Smoothly move the camera to the marker and display the popup window
                        createPopupWindow(beach, marker);
                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(beach.getLocation())
                                .zoom(15)
                                .build();
                        CameraUpdate cu = CameraUpdateFactory.newCameraPosition(cameraPosition);
                        mMap.animateCamera(cu);
                    }
                }
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

            JSONObject getRating = jsonArray.getJSONObject(i);
            double rating = Double.parseDouble(getRating.getString("rating"));

            JSONObject getPlaceId = jsonArray.getJSONObject(i);
            String placeId = getPlaceId.getString("place_id");

            LatLng latLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
            Beach beach = new Beach(name,latLng,rating,placeId);
            nearbyBeaches.add(beach);
        }
    }

    @SuppressLint({"SetTextI18n", "MissingInflatedId"})
    public void createPopupWindow(Beach beach, Marker marker){
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View beachPopupView = layoutInflater.inflate(R.layout.pop_up_window,null);
        PopupWindow popupWindow = new PopupWindow(beachPopupView,900,1500,true);

        tvPopupName = (TextView) beachPopupView.findViewById(R.id.tv_pop_up_name);
        tvPopupRating = (TextView) beachPopupView.findViewById(R.id.tv_pop_up_rating);
        btnPopupBack = (ImageButton) beachPopupView.findViewById(R.id.imgBtn_popup_back);
        btnDirection = (Button) beachPopupView.findViewById(R.id.btn_beach_popup_direction);
        btnRestaurant = (Button) beachPopupView.findViewById(R.id.btn_beach_popup_restaurant);
        cbAnonymous = (CheckBox) beachPopupView.findViewById(R.id.cb_beach_popup_anonymous);
        ratingBar = (RatingBar) beachPopupView.findViewById(R.id.rating);
        btnSubmit = (Button) beachPopupView.findViewById(R.id.btn_beach_popup_submitRating);

        tvPopupName.setText(beach.getName());
        tvPopupRating.setText("rating: "+ String.valueOf(beach.getRating())+" out of 5");

        popupWindow.showAtLocation(findViewById(R.id.home_map_view), Gravity.CENTER,0,0);

        btnPopupBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });


        // Nearby Restaurant button onclick listener
        btnRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // When clicking the restaurant button:
                // 0. close the popup window
                popupWindow.dismiss();
                // 1. zoom in to the beach
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        beach.getLocation(), DEFAULT_ZOOM+1));
                // 2. hide all other beaches markers
                for(Marker markerBeach: allBeachMarkers){
                    if(!markerBeach.getTitle().equals(beach.getName()))
                        markerBeach.setVisible(false);
                }
                // 3. show buttons on map that enables user to select range (default 1000feet)
                btnRange1000.setVisibility(View.VISIBLE);
                btnRange1000.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        restaurantRange = 304.8;
                        circle.setRadius(restaurantRange);
                        try {
                            findRestaurant(beach,restaurantRange);
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                btnRange2000.setVisibility(View.VISIBLE);
                btnRange2000.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        restaurantRange = 609.6;
                        circle.setRadius(restaurantRange);
                        try {
                            findRestaurant(beach,restaurantRange);
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                btnRange3000.setVisibility(View.VISIBLE);
                btnRange3000.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        restaurantRange = 914.4;
                        circle.setRadius(restaurantRange);
                        try {
                            findRestaurant(beach,restaurantRange);
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                // 4. draw a default circle centered at the beach with range corresponding to selected range
                circle = mMap.addCircle(new CircleOptions()
                        .center(beach.getLocation())
                        .radius(restaurantRange)
                        .strokeColor(Color.RED).strokeWidth(2)
                        .fillColor(Color.parseColor("#22C8BAA9")));

                // 5. show a button on map that can exit restaurant mode.
                btnHomeBack.setVisibility(View.VISIBLE);
                btnHomeBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        for(Marker marker1: allRestaurantMarkers){
                            marker1.remove();
                        }
                        for(Marker marker1: allBeachMarkers){
                            marker1.setVisible(true);
                        }
                        circle.remove();
                        btnHomeBack.setVisibility(View.INVISIBLE);
                        btnHomeBack.setOnClickListener(null);
                        btnRange1000.setVisibility(View.INVISIBLE);
                        btnRange1000.setOnClickListener(null);
                        btnRange2000.setVisibility(View.INVISIBLE);
                        btnRange2000.setOnClickListener(null);
                        btnRange3000.setVisibility(View.INVISIBLE);
                        btnRange3000.setOnClickListener(null);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                beach.getLocation(), 10));
                    }
                });
                // 6. call api to find nearby restaurants
                // 7. add restaurants markers to map, set onclicklistener for popup window

            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Float rating = ratingBar.getRating();
                // TODO: change the layout, record the rating into current beach and current user profile.
            }
        });
    }


    public void findRestaurant(Beach beach, double range) throws IOException, JSONException {
        String url = "";
        // TODO: find nearby restaurants and inflate markers on map (remember adding them in list).
    }
}
