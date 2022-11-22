package com.example.solcalbeach;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import com.example.solcalbeach.util.Review;
import com.example.solcalbeach.profileActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class profileTest {
    Map<String, HashMap<String,String>> reviews;
    String placeId;
    String userId;
    String reviewId;

    // init test cases
    @Before
    public void init() {
        reviews = new HashMap<>();
        HashMap<String,String> tempView = new HashMap<>();
        placeId = "ChIJjY4QN1QLj4AR2ZTMsptNzXs";
        userId = "uELtDY3eqbZFhECnaFKAlQNjAQy2";
        reviewId = "a4d648c5-6e48-42a2-a88d-f1fc8cde11b7";

        // case 1
        tempView.put("placeId", placeId);
        tempView.put("rating", "5.0");
        tempView.put("beachName", "Poplar Beach");
        tempView.put("reviewId", reviewId);
        tempView.put("userId", userId);
        reviews.put("a4d648c5-6e48-42a2-a88d-f1fc8cde11b7", tempView);
    }

    // when given a null object
    // return null back
    @Test
    public void testUpdateListExpectNull() {
        Map<String, HashMap<String,String>> trial = null;
        ArrayList<Review> toBeHandle = profileActivity.updateList(trial);
        assertNull(toBeHandle);
    }

    // when given a not null object
    // return not null back
    @Test
    public void testUpdateListExpectNotNull() {
        ArrayList<Review> toBeHandle = profileActivity.updateList(reviews);
        assertNotNull(toBeHandle);
    }

    // return value is correct as we expected
    @Test
    public void testUpdateListExpectTheSame() {
        ArrayList<Review> toBeHandle = profileActivity.updateList(reviews);
        assertEquals(placeId, toBeHandle.get(0).getPlaceId());
        assertEquals(userId, toBeHandle.get(0).getUserId());
        assertEquals("5.0", toBeHandle.get(0).getRating());
        assertEquals(reviewId, toBeHandle.get(0).getReviewId());
        assertEquals("Poplar Beach", toBeHandle.get(0).getBeachName());
    }
}
