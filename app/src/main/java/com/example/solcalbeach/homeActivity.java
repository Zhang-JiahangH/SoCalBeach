package com.example.solcalbeach;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationRequest;
import android.os.Build;
import android.os.Bundle;
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
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.solcalbeach.util.Beach;
import com.example.solcalbeach.util.Parking;
import com.example.solcalbeach.util.Restaurant;
import com.example.solcalbeach.util.DownloadUrl;
import com.example.solcalbeach.util.Review;
import com.example.solcalbeach.util.TravelHistory;
import com.example.solcalbeach.util.history;
import com.example.solcalbeach.util.userRegisterHelper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;


import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
    List<Restaurant> nearbyRestaurant;

    //Parking info
    List<Parking> nearbyLots;

    //route info
    List<Double> Lat;
    List<Double> Long;
    String encoded, estimate;
    Polyline polyline;
    Calendar startTime;
    Calendar endTime;

    // popup window needed
    PopupWindow popupWindow;
    TextView tvPopupName, tvPopupRating;
    ImageButton btnPopupBack;
    Button btnDirection, btnRestaurant, btnSubmit, btnHomeBack, btnRange1000, btnRange2000, btnRange3000, ETA, endRoute;
    CheckBox cbAnonymous;
    RatingBar ratingBar;

    // review needed
    Beach curBeach;
    FirebaseUser curUser;
    private DatabaseReference mDatabase;

    // Dialog
    AlertDialog dialog;

    // formating
    NumberFormat nf;


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
        ETA = (Button) findViewById(R.id.ETA);
        ETA.setVisibility(View.INVISIBLE);
        btnRange1000 = (Button) findViewById(R.id.Btn_home_range_1000);
        btnRange2000 = (Button) findViewById(R.id.Btn_home_range_2000);
        btnRange3000 = (Button) findViewById(R.id.Btn_home_range_3000);
        btnRange1000.setVisibility(View.INVISIBLE);
        btnRange2000.setVisibility(View.INVISIBLE);
        btnRange3000.setVisibility(View.INVISIBLE);
        endRoute = (Button) findViewById(R.id.btn_end_route);
        endRoute.setVisibility(View.INVISIBLE);

        // Init number format
        nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(1);
        nf.setRoundingMode(RoundingMode.UP);

        // Init database basic
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initial user Info
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        curUser = mAuth.getCurrentUser();

        // Change Header Info
        changeHeader();

        // Initialize the Tool Bar
        setSupportActionBar(toolbar);

        // Initialize navigation drawer menu
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.black));
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Initialize clickable items in the menu
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        if(item.getItemId() == R.id.nav_profile) {
                            Intent intent = new Intent();
                            intent.setClass(homeActivity.this,profileActivity.class);
                            startActivity(intent);
                        }
                        else if(item.getItemId() == R.id.History) {
                            Intent intent = new Intent();
                            intent.setClass(homeActivity.this,historyActivity.class);
                            startActivity(intent);
                        }
                        return false;
                    }
                }
        );

        // Initialize map view
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getCurrentLocation();
        if (isPermissionGranted) {
            SupportMapFragment supportMapFragment = (SupportMapFragment) this.getSupportFragmentManager().findFragmentById(R.id.home_map_view);
            supportMapFragment.getMapAsync(this);
        }

        // Initialize dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(homeActivity.this);
        // Add the buttons
        builder.setMessage("Message Created/Updated")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        popupWindow.dismiss();
                    }
                });
        dialog = builder.create();


        navigationView.bringToFront();
        toolbar.bringToFront();

    }

    public void changeHeader() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users");
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userRegisterHelper profile = dataSnapshot.getValue(userRegisterHelper.class);
                System.out.println(profile.getName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
        usersRef.child(curUser.getUid()).child("name").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    updateHeaderText(String.valueOf(task.getResult().getValue()));
                }
            }
        });
    }

    void updateHeaderText(String newUser) {
        TextView navHead = (TextView)findViewById(R.id.bar_header_welcome);
        navHead.setText(newUser);
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
//            return;
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
                    .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.beach))));
            allBeachMarkers.add(curMarker);
        }

        // Makes all markers clickable
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                for(Beach beach : nearbyBeaches){
                    if(beach.getName().equals(marker.getTitle())) {
                        curBeach = beach;
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
                //todo: restaurant pop up window and parking clicks
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

            int user_ratings_total = Integer.parseInt(getRating.getString("user_ratings_total"));

            LatLng latLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
            curBeach = new Beach(name,latLng,rating,placeId,user_ratings_total);
            nearbyBeaches.add(curBeach);
        }
    }

    @SuppressLint({"SetTextI18n", "MissingInflatedId"})
    public void createPopupWindow(Beach beach, Marker marker){
        Log.i("create pop up","got here");
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View beachPopupView = layoutInflater.inflate(R.layout.pop_up_window,null);
        popupWindow = new PopupWindow(beachPopupView,900,1500,true);

        tvPopupName = (TextView) beachPopupView.findViewById(R.id.tv_pop_up_name);
        tvPopupRating = (TextView) beachPopupView.findViewById(R.id.tv_pop_up_rating);
        btnPopupBack = (ImageButton) beachPopupView.findViewById(R.id.imgBtn_popup_back);
        btnDirection = (Button) beachPopupView.findViewById(R.id.btn_beach_popup_direction);
        btnRestaurant = (Button) beachPopupView.findViewById(R.id.btn_beach_popup_restaurant);
        cbAnonymous = (CheckBox) beachPopupView.findViewById(R.id.cb_beach_popup_anonymous);
        ratingBar = (RatingBar) beachPopupView.findViewById(R.id.rating);
        btnSubmit = (Button) beachPopupView.findViewById(R.id.btn_beach_popup_submitRating);

        tvPopupName.setText(beach.getName());
        upDateRating();
        Log.e("rating: ", String.valueOf(beach.getRating()));
        tvPopupRating.setText("rating: "+ nf.format(beach.getRating())+" out of 5");

        popupWindow.showAtLocation(findViewById(R.id.home_map_view), Gravity.CENTER,0,0);

        btnPopupBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });

        btnDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 0. close the popup window
                popupWindow.dismiss();
                // 1. zoom in to the beach
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        beach.getLocation(), DEFAULT_ZOOM-1));
                // 2. hide all other beaches markers
                for(Marker markerBeach: allBeachMarkers){
                    if(!markerBeach.getTitle().equals(beach.getName()))
                        markerBeach.setVisible(false);
                }
                findNearbyParking(beach);
                btnHomeBack.setVisibility(View.VISIBLE);
                btnHomeBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        for(Marker marker1: allParkingMarkers){
                            marker1.remove();
                        }
                        for(Marker marker1: allBeachMarkers){
                            marker1.setVisible(true);
                        }
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                beach.getLocation(), 10));
                        btnHomeBack.setVisibility(View.INVISIBLE);
                        ETA.setVisibility(View.INVISIBLE);
                        if(polyline != null){
                            polyline.remove();}
                        nearbyLots.removeAll(nearbyLots);
                    }
                });

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
                        nearbyRestaurant.removeAll(nearbyRestaurant);
                        endRoute.setVisibility(View.INVISIBLE);
                        endRoute.setVisibility(View.INVISIBLE);
                        if(polyline != null){
                            polyline.setVisible(false);
                            polyline.remove();}
                    }
                });
                // 6. call api to find nearby restaurants
                // 7. add restaurants markers to map, set onclicklistener for popup window

            }
        });

        // Initialize the submit review button for user to leave a review
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Float rating = ratingBar.getRating();
                Review newReview;
                UUID reviewId = UUID.randomUUID();
                if(cbAnonymous.isChecked()) {
                    newReview = new Review(String.valueOf(rating), "", beach.getPlaceId(), beach.getName(), reviewId.toString());
                }
                else {
                    newReview = new Review(String.valueOf(rating), curUser.getUid(), beach.getPlaceId(), beach.getName(), reviewId.toString());
                }
                // TODO: change the layout, record the rating into current beach and current user profile.
                writeReview(reviewId, newReview);
                upDateRating();
                dialog.show();
            }
        });
    }

    public void writeReview(UUID uuid, Review review) {
        mDatabase.child("reviews").child(uuid.toString()).setValue(review);
    }

    public void upDateRating() {
        Log.e("update rating:", "start");
        Query res = mDatabase.child("reviews").orderByChild("placeId").equalTo(curBeach.getPlaceId());
        res.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            }
            else {
                Log.e("firebase", String.valueOf(task.getResult().getValue()));
                Map<String, HashMap<String,String>> reviews = (HashMap<String, HashMap<String,String>>)task.getResult().getValue();
                if(reviews != null) {
                    for(HashMap<String,String> review:reviews.values()) {
                        Log.e("placeId", review.get("placeId"));
                        changeRatingInfo(Double.parseDouble(review.get("rating")));
                    }
                }
            }
        });
        Log.e("update rating:", "end");
    }

    public void changeRatingInfo(double rates) {
        curBeach.setRating((curBeach.getRating() * curBeach.getUser_ratings_total() + rates) / (curBeach.getUser_ratings_total() + 1));
        curBeach.setUser_ratings_total(curBeach.getUser_ratings_total() + 1);
    }


    public void findRestaurant(Beach beach, double range) throws IOException, JSONException {
         Log.i("triggered: ", "findRest");
        if (curLocation == null) {
                Log.e("Error", "current location is null in find nearby beaches.");
            }
            if (!isPermissionGranted) {
                Log.e("Error", "permission not granted in find nearby beaches,");
            }
            Log.d("search starts", "starting searching nearby restaurants...");


            String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" +
                    "location=" + beach.getLatitude() + "," + beach.getLongitude() +
                    "&radius="+ range +
                    "&types=restaurant" +
                    "&key=AIzaSyBNF_W_dJPHr-HGw3YtFCbfMoUcvKdBlSg";


            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        initializeRestaurantMarkers(url);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
            while(nearbyRestaurant==null){
                double i = Math.log(309209387);
                Log.i("bye ", "bye");

            }

            // Put the found beaches as markers onto map and add them in hashmap.
            for(Restaurant restaurant : nearbyRestaurant){
                Marker curMarker = mMap.addMarker(new MarkerOptions()
                        .position(restaurant.getLocation())
                        .title(restaurant.getName())
                        .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.food))));
                allRestaurantMarkers.add(curMarker);
            }


            // Makes all markers clickable
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(@NonNull Marker marker) {
                    for(Restaurant restaurant : nearbyRestaurant){
                        if(restaurant.getName().equals(marker.getTitle())) {
                            // Smoothly move the camera to the marker and display the popup window
                            CameraPosition cameraPosition = new CameraPosition.Builder()
                                    .target(restaurant.getLocation())
                                    .zoom(15)
                                    .build();
                            CameraUpdate cu = CameraUpdateFactory.newCameraPosition(cameraPosition);
                            mMap.animateCamera(cu);
                            if(polyline != null){
                                polyline.remove();}
                            try {
                                displayWalk(restaurant, beach);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    for(Beach beach : nearbyBeaches){
                        if(beach.getName().equals(marker.getTitle())) {
                            // Smoothly move the camera to the marker and display the popup window
                            curBeach = beach;
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

    public void initializeRestaurantMarkers(String url) throws IOException, JSONException {
        nearbyRestaurant = new ArrayList<>();
        DownloadUrl downloadUrl = new DownloadUrl();
        String nearby_places_data;
        nearby_places_data = downloadUrl.retrieve_url(url);
        Log.i("nearby place data", nearby_places_data);


        if (nearby_places_data.equals("")){
            Log.i("no"," rest");}
        else{
            JSONObject jsonObject = new JSONObject(nearby_places_data);
            JSONArray jsonArray = jsonObject.getJSONArray("results");
            Log.i("JsonArrayLen",String.valueOf(jsonArray.length()));
            for(int i = 0; i < jsonArray.length();i++){
                // extract useful information from the json
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                JSONObject getLocation = jsonObject1.getJSONObject("geometry").getJSONObject("location");
                String lat = getLocation.getString("lat");
                String lng = getLocation.getString("lng");

                JSONObject getName = jsonArray.getJSONObject(i);
                String name = getName.getString("name");
                Log.i("restaurant name", name);
                JSONObject getRating = jsonArray.getJSONObject(i);
                double rating = Double.parseDouble(getRating.getString("rating"));

                JSONObject getPlaceId = jsonArray.getJSONObject(i);
                String placeId = getPlaceId.getString("place_id");

                LatLng latLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                Restaurant restaurant = new Restaurant(name,latLng,rating,placeId);
                nearbyRestaurant.add(restaurant);
            }
        }


    }


    private void findNearbyParking(Beach beach){
        if (curLocation == null) {
            Log.e("Error", "current location is null in find nearby beaches.");
        }
        if (!isPermissionGranted) {
            Log.e("Error", "permission not granted in find nearby beaches,");
        }
        Log.d("search starts", "starting searching nearby parkings...");


        String url = "https://maps.googleapis.com/maps/api/place/textsearch/json?" +
                "location=" + beach.getLatitude() + "," + beach.getLongitude() +
                "&query=parking" +
                "&key=AIzaSyBNF_W_dJPHr-HGw3YtFCbfMoUcvKdBlSg";


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    initializeParkingMarkers(url);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

        while(nearbyLots==null){double i = Math.log(309209387);}
        while(nearbyLots.size()!=3){double i = Math.log(309209387);}

        // Put the found beaches as markers onto map and add them in hashmap.
        for(Parking parking : nearbyLots){
            Marker curMarker = mMap.addMarker(new MarkerOptions()
                    .position(parking.getLocation())
                    .title(parking.getName())
                    .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.parking_location))));
            allParkingMarkers.add(curMarker);
        }

                    // Makes all markers clickable
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @SuppressLint("PotentialBehaviorOverride")
                @Override
                public boolean onMarkerClick(@NonNull Marker marker) {
                    for(Parking parking : nearbyLots){
                        if(parking.getName().equals(marker.getTitle())) {
                            // Smoothly move the camera to the marker and display the popup window
                            CameraPosition cameraPosition = new CameraPosition.Builder()
                                    .target(parking.getLocation())
                                    .zoom(15)
                                    .build();
                            CameraUpdate cu = CameraUpdateFactory.newCameraPosition(cameraPosition);
                            mMap.animateCamera(cu);
                            if(polyline != null){
                                polyline.remove();}
                            try {
                                displayRoute(parking,beach);
                                ETA.setVisibility(View.VISIBLE);
                                ETA.setText("ETA: " + estimate);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    for(Beach beach : nearbyBeaches){
                        if(beach.getName().equals(marker.getTitle())) {
                            // Smoothly move the camera to the marker and display the popup window
                            curBeach = beach;
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



    public void initializeParkingMarkers(String url) throws IOException, JSONException {
        nearbyLots = new ArrayList<>();
        DownloadUrl downloadUrl = new DownloadUrl();
        String nearby_places_data;
        nearby_places_data = downloadUrl.retrieve_url(url);

        Log.i("nearby data",nearby_places_data);

        JSONObject jsonObject = new JSONObject(nearby_places_data);
        JSONArray jsonArray = jsonObject.getJSONArray("results");
        for(int i=0; i< 3 ; i++){
            // extract useful information from the json
            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

            JSONObject getLocation = jsonObject1.getJSONObject("geometry").getJSONObject("location");
            String lat = getLocation.getString("lat");
            String lng = getLocation.getString("lng");
            Log.i("lat",lat);
            Log.i("lng",lng);

            JSONObject getName = jsonArray.getJSONObject(i);
            String name = getName.getString("name");
            Log.i("name",name);

            JSONObject getRating = jsonArray.getJSONObject(i);
            double rating = Double.parseDouble(getRating.getString("rating"));

            JSONObject getPlaceId = jsonArray.getJSONObject(i);
            String placeId = getPlaceId.getString("place_id");
            Log.i("placeid",placeId);

            LatLng latLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
            Parking parking = new Parking(name,latLng,placeId);
            nearbyLots.add(parking);
        }
    }


    public void displayWalk(Restaurant destination, Beach beach) throws IOException {
        if (curLocation == null) {
            Log.e("Error", "current location is null in find nearby beaches.");
        }
        if (!isPermissionGranted) {
            Log.e("Error", "permission not granted in find nearby beaches,");
        }
        Log.d("search starts", "starting drawing route...");

        String url = "https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=" + beach.getLatitude() + "," + beach.getLongitude() +
                "&destination=place_id:" + destination.getPlaceId() +
                "&mode=walking"+
                "&key=AIzaSyBNF_W_dJPHr-HGw3YtFCbfMoUcvKdBlSg";

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    initializePolyLine(url);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

        while(encoded==null){
            double i = Math.log(309209387);
        }

        List<LatLng> decoded = PolyUtil.decode(encoded);
        PolylineOptions poly = new PolylineOptions().addAll(decoded);
        polyline = mMap.addPolyline(poly);
    }


    public void displayRoute(Parking destination, Beach beach) throws IOException {
        if (curLocation == null) {
            Log.e("Error", "current location is null in find nearby beaches.");
        }
        if (!isPermissionGranted) {
            Log.e("Error", "permission not granted in find nearby beaches,");
        }
        Log.d("search starts", "starting drawing route...");

        String url = "https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=" + curLocation.getLatitude() + "," + curLocation.getLongitude() +
                "&destination=place_id:" + destination.getPlaceId() +
                "&key=AIzaSyBNF_W_dJPHr-HGw3YtFCbfMoUcvKdBlSg";

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    initializePolyLine(url);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

        while(encoded==null){double i = Math.log(309209387);}

        List<LatLng> decoded = PolyUtil.decode(encoded);
        PolylineOptions poly = new PolylineOptions().addAll(decoded);
        startTime = Calendar.getInstance();
        polyline = mMap.addPolyline(poly);
        endRoute.setVisibility(View.VISIBLE);
        endRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast save = Toast.makeText(getApplicationContext(),"Travel History recorded.", Toast.LENGTH_LONG);
                endTime = Calendar.getInstance();
                TravelHistory newTravel = new TravelHistory(beach.getName(),startTime,endTime);
                // TODO:Save the travel history to database
                UUID travelId = UUID.randomUUID();
                history travHis = new history(newTravel.toString(), curUser.getUid());
                writeHistory(travelId, travHis);

                for(Marker marker1: allRestaurantMarkers){marker1.remove();}
                for(Marker marker: allParkingMarkers){marker.remove();}
                for(Marker marker1: allBeachMarkers){marker1.setVisible(true);}
                btnHomeBack.setVisibility(View.INVISIBLE);
                btnHomeBack.setOnClickListener(null);
                endRoute.setVisibility(View.INVISIBLE);
                endRoute.setVisibility(View.INVISIBLE);
                ETA.setVisibility(View.INVISIBLE);
                ETA.setText("");
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        beach.getLocation(), 10));
                if(polyline != null){
                    polyline.setVisible(false);
                    polyline.remove();}

            }
        });
    }

    public void writeHistory(UUID uuid, history newTravel) {
        mDatabase.child("history").child(uuid.toString()).setValue(newTravel);
    }

    public void initializePolyLine(String url) throws IOException, JSONException{
        Lat = new ArrayList<>();
        Long = new ArrayList<>();
        DownloadUrl downloadUrl = new DownloadUrl();
        String nearby_places_data;
        nearby_places_data = downloadUrl.retrieve_url(url);

        JSONObject jsonObject = new JSONObject(nearby_places_data);
        JSONArray route = jsonObject.getJSONArray("routes");
        for(int i = 0; i < route.length(); i++){
            JSONObject jb1 = route.getJSONObject(i);
            JSONArray legs = jb1.getJSONArray("legs");
            Log.i("route", legs.toString());
            Log.i("route", String.valueOf(legs.length()));
            JSONObject overview_polyline = jb1.getJSONObject("overview_polyline");
            encoded = overview_polyline.getString("points");
            Log.i("legs", String.valueOf(overview_polyline.length()));
            for(int j = 0; j < legs.length(); j++){
                JSONObject jb2 = legs.getJSONObject(j);
                JSONObject eta = jb2.getJSONObject("duration");
                estimate = eta.getString("text");
                Log.i("ETA",estimate);
                JSONArray steps = jb2.getJSONArray("steps");
                for(int k = 0; k < steps.length(); k++){
                    JSONObject jb3 = steps.getJSONObject(k);
                    JSONObject getStartLocation = jb3.getJSONObject("start_location");
                    String lat = getStartLocation.getString("lat");
                    String lng = getStartLocation.getString("lng");
                    Log.i("lat",lat);
                    Log.i("lng",lng);
                    Lat.add(Double.valueOf(lat));
                    Long.add(Double.valueOf(lng));

                }
            }

        }
    }

}
