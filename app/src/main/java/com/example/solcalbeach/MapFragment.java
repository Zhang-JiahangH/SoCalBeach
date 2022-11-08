//package com.example.solcalbeach;
//
//import androidx.annotation.NonNull;
//import androidx.core.app.ActivityCompat;
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentActivity;
//
//import android.Manifest;
//import android.annotation.SuppressLint;
//import android.content.pm.PackageManager;
//import android.location.Location;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//
//import com.google.android.gms.location.FusedLocationProviderClient;
//import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.OnMapReadyCallback;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.MarkerOptions;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.android.gms.tasks.Task;
//
//
//public class MapFragment extends Fragment implements OnMapReadyCallback {
//
//    private GoogleMap mMap;
//
//    private final LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085);
//    private static final int DEFAULT_ZOOM = 15;
//
////    Location currentLocation;
//    Location curLocation;
//    FusedLocationProviderClient fusedLocationProviderClient;
//    private static final int REQUEST_CODE=101;
//
//
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//
//        return inflater.inflate(R.layout.fragment_map, container, false);
//
////        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
////
////        getCurrentLocation();
////        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
////        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
////                .findFragmentById(R.id.map);
////        mapFragment.getMapAsync(this);
//    }
//
//    @Override
//    public void onViewCreated(View view, Bundle savedInstanceState){
//        super.onViewCreated(view, savedInstanceState);
//
//        MapFragment fragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.map);
//
//    }
//
//
//
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(defaultLocation));
//        getCurrentLocation();
//
//    }
//
//    private void getCurrentLocation(){
//        // Check if the required permission has been granted
//        // If not, request for permission
//        if(ActivityCompat.checkSelfPermission(
//                this, Manifest.permission.ACCESS_FINE_LOCATION)
//                !=PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
//                            Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE);
//            return;
//        }
//
//         @SuppressLint("MissingPermission") Task<Location> task = fusedLocationProviderClient.getLastLocation();
//         task.addOnSuccessListener(new OnSuccessListener<Location>() {
//            @Override
//            public void onSuccess(Location location) {
//                if(task.isSuccessful()){
//                    curLocation = task.getResult();
//                    if(curLocation != null){
//                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
//                                new LatLng(curLocation.getLatitude(),
//                                        curLocation.getLongitude()), DEFAULT_ZOOM));
//                        mMap.addMarker(new MarkerOptions().position
//                                (new LatLng(curLocation.getLatitude(),curLocation.getLongitude())).title("Current Location"));
//                    }else{
//                        mMap.moveCamera(CameraUpdateFactory
//                                .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
//                        mMap.addMarker(new MarkerOptions().position
//                                (defaultLocation).title("Default Location"));
//                    }
//                }
//            }
//        });
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (REQUEST_CODE){
//            case REQUEST_CODE:
//                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                    getCurrentLocation();
//                }
//                break;
//        }
//    }
//}