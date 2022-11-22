package com.example.solcalbeach;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import com.example.solcalbeach.util.Review;
import com.example.solcalbeach.util.TravelHistory;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class historyTest {
    Map<String, HashMap<String,String>> travels;
    String TravelInfo;
    String userId;
    TravelHistory th;
    Calendar startTime;
    Calendar endTime;

    @Before
    public void init() {
        travels = new HashMap<>();
        HashMap<String,String> tempView = new HashMap<>();
        startTime = Calendar.getInstance();
        endTime = Calendar.getInstance();
        th = new TravelHistory("Bay Beach", startTime, endTime);
        TravelInfo = th.toString();
        userId = "uELtDY3eqbZFhECnaFKAlQNjAQy2";

        tempView.put("travelInfo", TravelInfo);
        tempView.put("userId", userId);
        UUID travelId = UUID.randomUUID();
        travels.put(travelId.toString(), tempView);
    }

    // when given a null object
    // return null back
    @Test
    public void testUpdateListExpectNull() {
        Map<String, HashMap<String,String>> trial = null;
        ArrayList<String> toBeHandle = historyActivity.updateList(trial);
        assertNull(toBeHandle);
    }

    // when given a not null object
    // return not null back
    @Test
    public void testUpdateListExpectNotNull() {
        ArrayList<String> toBeHandle = historyActivity.updateList(travels);
        assertNotNull(toBeHandle);
    }

    // return value is correct as we expected
    @Test
    public void testUpdateListExpectTheSame() {
        ArrayList<String> toBeHandle = historyActivity.updateList(travels);
        assertEquals(TravelInfo, toBeHandle.get(0));
    }

    // toString() is consistent
    @Test
    public void testHistoryToString() {
        TravelHistory newth = new TravelHistory("Bay Beach", startTime, endTime);
        assertEquals(TravelInfo, newth.toString());
    }
}
