package com.example.solcalbeach.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TravelHistory {
    String beachName;
    Calendar startTime;
    Calendar endTime;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    public TravelHistory(String beachName, Calendar startTime, Calendar endTime) {
        this.beachName = beachName;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Calendar getStartTime() {
        return startTime;
    }

    public void setStartTime(Calendar startTime) {
        this.startTime = startTime;
    }

    public Calendar getEndTime() {
        return endTime;
    }

    public void setEndTime(Calendar endTime) {
        this.endTime = endTime;
    }

    public String getBeachName() {
        return beachName;
    }

    public void setBeachName(String beachName) {
        this.beachName = beachName;
    }

    @Override
    public String toString() {
        String toRet = "Travel to: "+beachName+" from "+simpleDateFormat.format(startTime.getTime())
                +" to "+ simpleDateFormat.format(endTime.getTime());
        return toRet;
    }
}
