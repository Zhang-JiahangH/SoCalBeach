package com.example.solcalbeach.util;

import com.google.android.gms.maps.model.LatLng;

public class Parking {
    private LatLng location;
    private String name;
    private String placeId;
    // TODO: implement picture/menu/openhour

    public Parking(String name, LatLng location, String placeId){
        this.name = name;
        this.location = location;
        this.placeId = placeId;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getName() {
        return name;
    }

    public LatLng getLocation() {
        return location;
    }

    public Double getLatitude(){
        return location.latitude;
    }

    public Double getLongitude(){
        return location.longitude;
    }
}