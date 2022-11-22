package com.example.solcalbeach;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import com.example.solcalbeach.homeActivity;
import com.example.solcalbeach.util.Beach;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class homeTest {
    List<Beach> nearbyBeaches;

    @Before
    public void init() throws JSONException, IOException {
        nearbyBeaches = homeActivity.initializeBeachMarkers("https://maps.googleapis.com/maps/api/place/textsearch/json?location=37.421998333333335,-122.084&query=beach&types=natural_feature&key=AIzaSyBNF_W_dJPHr-HGw3YtFCbfMoUcvKdBlSg");
    }

    // Beach creation
    @Test
    public void nearbyBeachesNotNULL() {
        // not null
        assertNotNull(nearbyBeaches);
    }

    // Return value of initializeBeachMarkers should be the same as we expected
    @Test
    public void nearbyBeachesCorrect() {
        HashMap<String, Beach> beachHashMap = new HashMap<>();
        beachHashMap.put("Belmont Slough Beach", new Beach("Belmont Slough Beach",new LatLng(37.5573259,-122.2454921),0.0, "ChIJtcHhgiCZj4AR5xRiBRAgGWw",0));
        beachHashMap.put("Bay Beach", new Beach("Bay Beach",new LatLng(37.563398,-122.2489931),0.0, "ChIJa56yCSSZj4ARLr_ehI2OoT8",0));
        beachHashMap.put("Coyote Point Beach", new Beach("Coyote Point Beach",new LatLng(37.5913162,-122.3230841),0.0, "ChIJd5s4iMidj4ARMNDbOfCdrB4",0));
        beachHashMap.put("Martin's Beach", new Beach("Martin's Beach",new LatLng(37.3736981,-122.4083224),0.0, "ChIJUxMntgwJj4ARKimsNeeRkz0",0));
        beachHashMap.put("Elmar Beach", new Beach("Elmar Beach",new LatLng(37.4743853,-122.4480316),0.0, "ChIJjW0UdV9zj4AR6JqULfUwXiU",0));
        beachHashMap.put("Poplar Beach", new Beach("Poplar Beach",new LatLng(37.4552185,-122.4447988),0.0, "ChIJjY4QN1QLj4AR2ZTMsptNzXs",0));

        for(int i=0; i<6; ++i) {
            // should contain this element
            assertNotNull(beachHashMap.get(nearbyBeaches.get(i).getName()));
            // should have the same LatLang
            assertEquals(nearbyBeaches.get(i).getLocation(), beachHashMap.get(nearbyBeaches.get(i).getName()).getLocation());
            // rating should be zero and voter should be zero
            assertEquals(0.0, beachHashMap.get(nearbyBeaches.get(i).getName()).getRating(), 0);
            assertEquals(0, beachHashMap.get(nearbyBeaches.get(i).getName()).getUser_ratings_total());
            // placeId should be the same
            assertEquals(nearbyBeaches.get(i).getPlaceId(), beachHashMap.get(nearbyBeaches.get(i).getName()).getPlaceId());
        }
    }

    // Beach update
    // update the rating correct
    @Test
    public void changeRatingInfoTest() {
        Beach tempBeach = new Beach("Belmont Slough Beach",new LatLng(37.5573259,-122.2454921),0.0, "ChIJtcHhgiCZj4AR5xRiBRAgGWw",0);
        Beach[] temp = new Beach[1];
        temp[0] = tempBeach;
        homeActivity.changeRatingInfo(temp, 5.0);
        assertEquals(5.0, tempBeach.getRating(), 0.0);
    }
}
