package com.example.solcalbeach.util;

public class Review {
    private String rating;
    private String userId;
    private String placeId;
    private String beachName;

    public Review(String rating, String userId, String placeId, String beachName) {
        this.rating = rating;
        this.placeId = placeId;
        this.userId = userId;
        this.beachName = beachName;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getBeachName() {
        return beachName;
    }

    public void setBeachName(String beachName) {
        this.beachName = beachName;
    }
}
