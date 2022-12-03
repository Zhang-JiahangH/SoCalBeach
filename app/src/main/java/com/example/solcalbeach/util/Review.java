package com.example.solcalbeach.util;

public class Review {
    private String rating;
    private String userId;
    private String placeId;
    private String beachName;
    private String reviewId;
    private String comments;
    private String imageUrl;

    public Review(String rating, String userId, String placeId, String beachName, String reviewId, String comments) {
        this.rating = rating;
        this.placeId = placeId;
        this.userId = userId;
        this.beachName = beachName;
        this.reviewId = reviewId;
        this.comments = comments;
        this.imageUrl = "";
    }

    public Review(String rating, String userId, String placeId, String beachName, String reviewId, String comments, String imageUrl) {
        this.rating = rating;
        this.placeId = placeId;
        this.userId = userId;
        this.beachName = beachName;
        this.reviewId = reviewId;
        this.comments = comments;
        this.imageUrl = imageUrl;
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

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
