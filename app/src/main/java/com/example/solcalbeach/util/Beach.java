package com.example.solcalbeach.util;

import com.google.android.gms.maps.model.LatLng;

public class Beach{
    private LatLng location;
    private String name;
    private double rating;
    private String placeId;
    private int user_ratings_total;
    private String address;
    // TODO: implement rating related stuff(list+constructor...)

    public Beach(String name, LatLng location, double rating, String placeId, int user_ratings_total, String address){
        this.name = name;
        this.location = location;
        this.rating=rating;
        this.placeId = placeId;
        this.user_ratings_total = user_ratings_total;
        this.address = address;
    }

    public LatLng getLocation() {
        return location;
    }


    public void setLocation(LatLng location) {
        this.location = location;
    }

    public Double getLatitude(){
        return location.latitude;
    }

    public Double getLongitude(){
        return location.longitude;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public int getUser_ratings_total() {
        return user_ratings_total;
    }

    public void setUser_ratings_total(int user_ratings_total) {
        this.user_ratings_total = user_ratings_total;
    }

}