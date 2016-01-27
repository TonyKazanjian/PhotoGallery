package com.bignerdranch.android.photogallery;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tonyk_000 on 1/15/2016.
 */
public class FlickrFetchr {

    private static final String TAG = "FlickrFetchr";

    private static final String API_KEY = "5907a0314289bdcdb382af38cc33d6dc";
    private static final String flickrAPI = "https://api.flickr.com/services/rest/?method=flickr.photos.getRecent&api_key="+API_KEY+"&page&format=json&nojsoncallback=1";

    public byte[] getUrlBytes(String urlSpec) throws IOException {
        //creates URL object from a string (i.e. www.bignerdranch.com)
        URL url = new URL(urlSpec);
        //points connection to URL
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();

        try{
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            //this method call connects to the endpoint
            InputStream in = connection.getInputStream();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK){
                throw new IOException(connection.getResponseMessage() + ": with " + urlSpec);
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            //once the connection is made, read() is called repeatedly until the connection runs out of data
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    //converts the bytes fetched into a string
    public String getUrlString(String urlSpec) throws IOException{
        return new String (getUrlBytes(urlSpec));
    }

    public List<FlickrResponse.PhotosEntity.PhotoEntity> fetchItems(int page){


//        GsonBuilder gsonBuilder = new GsonBuilder();
//        Gson gson = gsonBuilder.create();
//
//        FlickrResponse flickrResponse = null;
//
//        try {
//            flickrResponse = gson.fromJson(getUrlString(flickrAPI), FlickrResponse.class);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return flickrResponse.getPhotos().getPhoto();

        List<FlickrResponse.PhotosEntity.PhotoEntity> items = new ArrayList<>();
        try {
            String url = Uri.parse("https://api.flickr.com/services/rest/")
                    .buildUpon()
                    .appendQueryParameter("method", "flickr.photos.getRecent")
                    .appendQueryParameter("api_key", API_KEY)
                    .appendQueryParameter("format", "json")
                    .appendQueryParameter("nojsoncallback", "1")
                    .appendQueryParameter("extras", "url_s")
                    .appendQueryParameter("page",Integer.toString(page))
                    .build().toString();
            String jsonString = getUrlString(url);
            Log.i(TAG, "Received JSON: " + jsonString);
            //parses JSON text into corresponding Java object
            JSONObject jsonBody = new JSONObject(jsonString);
            parseItems(items, jsonBody);
        } catch (JSONException je){
            Log.e(TAG, "Failed to parse JSON", je);
        } catch (IOException ioe){
            Log.e(TAG, "Failed to fetch items", ioe);
        }

        return items;
    }

    public void parseItems(List<FlickrResponse.PhotosEntity.PhotoEntity> items, JSONObject jsonBody)
            throws IOException, JSONException {
        JSONObject photosJsonObject = jsonBody.getJSONObject("photos");
        JSONArray photoJsonArray = photosJsonObject.getJSONArray("photo");

        for (int i = 0; i < photoJsonArray.length(); i++) {
            JSONObject photoJsonObject = photoJsonArray.getJSONObject(i);

            FlickrResponse.PhotosEntity.PhotoEntity item = new FlickrResponse.PhotosEntity.PhotoEntity();
            item.setId(photoJsonObject.getString("id"));
            item.setTitle(photoJsonObject.getString("title"));

            if (!photoJsonObject.has("url_s")) {
                continue;
            }

            item.setUrl(photoJsonObject.getString("url_s"));
            items.add(item);
        }
    }
}
