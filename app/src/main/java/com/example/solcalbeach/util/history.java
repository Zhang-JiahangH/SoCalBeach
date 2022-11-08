package com.example.solcalbeach.util;

public class history {
    private String travelInfo;
    private String userId;

    public history(String travelInfo, String userId) {
        this.travelInfo = travelInfo;
        this.userId = userId;
    }

    public String getTravelInfo() {
        return travelInfo;
    }

    public void setTravelInfo(String travelInfo) {
        this.travelInfo = travelInfo;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
