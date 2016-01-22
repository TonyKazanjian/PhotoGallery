package com.bignerdranch.android.photogallery;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

/**
 * Created by tonyk_000 on 1/15/2016.
 */
public class FlickrFetchr {

    private static final String API_KEY = "5907a0314289bdcdb382af38cc33d6dc";
    private static final String URL = "https://api.flickr.com/services/rest/?method=flickr.photos.getRecent&api_key="+API_KEY+"&format=json&nojsoncallback=1";

    public List<FlickrResponse.PhotosEntity.PhotoEntity> fetchItems(){

        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();

        FlickrResponse flickrResponse = gson.fromJson(URL, FlickrResponse.class);

        return flickrResponse.getPhotos().getPhoto();
    }
}
