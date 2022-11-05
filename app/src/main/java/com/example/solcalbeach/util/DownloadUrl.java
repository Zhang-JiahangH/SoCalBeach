package com.example.solcalbeach.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadUrl {

    public String retrieve_url(String url) throws IOException{
        String urlData = "";
        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;

        try{
            // Using httpConnection to connect and ask for data from the url
            URL getUrl = new URL(url);
            httpURLConnection = (HttpURLConnection) getUrl.openConnection();
            httpURLConnection.connect();
            // get and read the data from the URL.
            inputStream = httpURLConnection.getInputStream();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer stringBuffer = new StringBuffer();

            String line = "";
            while((line = bufferedReader.readLine())!=null){
                stringBuffer.append(line);
            }

            urlData = stringBuffer.toString();
            bufferedReader.close();
        }catch (Exception e){
            Log.e("Exception: ", e.toString());
        }finally {
            if(inputStream!=null)
                inputStream.close();
            if(httpURLConnection!=null)
                httpURLConnection.disconnect();
        }
        return urlData;
    }
}
