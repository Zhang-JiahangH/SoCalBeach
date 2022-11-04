package com.example.solcalbeach.util;

import android.os.AsyncTask;
import android.util.Log;

import com.example.solcalbeach.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

public class FetchData extends AsyncTask<Object,String,String> {

    String nearby_places_data;
    GoogleMap mMap;
    String url;
    List<Beach> searchResult;

    @Override
    protected String doInBackground(Object... objects) {

        try{
            mMap = (GoogleMap) objects[0];
            url = (String) objects[1];
            searchResult = (List<Beach>) objects[2];
            DownloadUrl downloadUrl = new DownloadUrl();
            // Call the downloadUrl class function to get a full json value for nearby places.
            nearby_places_data = downloadUrl.retrieve_url(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return nearby_places_data;
    }

    @Override
    protected void onPostExecute(String s) {
        // The doInBackground will return the string to here as json
        // And we're parsing the Json into usable type

        try{
            Log.e("search result: ",s);
            JSONObject jsonObject = new JSONObject(s);
            JSONArray jsonArray = jsonObject.getJSONArray("results");

            for(int i=0; i< jsonArray.length() ; i++){
                // extract useful information from the json
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                JSONObject getLocation = jsonObject1.getJSONObject("geometry").getJSONObject("location");
                String lat = getLocation.getString("lat");
                String lng = getLocation.getString("lng");

                JSONObject getName = jsonArray.getJSONObject(i);
                String name = getName.getString("name");

                LatLng latLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));

                // TODO: for testing now, just add markers to the map. Replace it with beach class to store more info.
                mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(name));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}
