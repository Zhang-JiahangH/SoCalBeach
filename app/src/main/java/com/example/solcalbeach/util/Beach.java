package com.example.solcalbeach.util;

import com.google.android.gms.maps.model.LatLng;

public class Beach{
    private LatLng location;
    private String name;

    public Beach(String name, LatLng location){
        this.name = name;
        this.location = location;
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

    public Double getLongtitude(){
        return location.longitude;
    }
}
