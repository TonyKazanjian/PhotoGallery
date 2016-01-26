package com.bignerdranch.android.photogallery;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by tonyk_000 on 1/15/2016.
 */
public class FlickrFetchr {

    private static final String API_KEY = "5907a0314289bdcdb382af38cc33d6dc";
    private static final String flickrAPI = "https://api.flickr.com/services/rest/?method=flickr.photos.getRecent&api_key="+API_KEY+"&format=json&nojsoncallback=1";

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

    public List<FlickrResponse.PhotosEntity.PhotoEntity> fetchItems(){


        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();

        FlickrResponse flickrResponse = null;

        try {
            flickrResponse = gson.fromJson(getUrlString(flickrAPI), FlickrResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return flickrResponse.getPhotos().getPhoto();
    }
}
