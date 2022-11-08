package com.example.solcalbeach.util;

import com.google.android.gms.maps.model.LatLng;

public class Restaurant {
    private LatLng location;
    private String name;
    private double rating;
    private String placeId;
    private boolean open;
    private String address;

    public Restaurant(String name, LatLng location, double rating, String placeId, boolean open, String address){
        this.name = name;
        this.location = location;
        this.rating=rating;
        this.placeId = placeId;
        this.open = open;
        this.address = address;
    }

    public Boolean getOpen() {
        return open;
    }

    public void setOpen(Boolean open) {
        this.open = open;
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

    public String getAddress() {
        return address;
    }

    public double getRating() {
        return rating;
    }


    public void setRating(double rating) {
        this.rating = rating;
    }

    public LatLng getLocation() {
        return location;
    }

    public Double getLatitude(){
        return location.latitude;
    }

    public Double getLongtitude(){
        return location.longitude;
    }
}
