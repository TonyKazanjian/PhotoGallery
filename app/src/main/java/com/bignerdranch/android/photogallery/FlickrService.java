package com.bignerdranch.android.photogallery;

import retrofit2.Call;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.http.GET;

/**
 * Created by tonyk_000 on 1/20/2016.
 */
public interface FlickrService {

   String API_KEY = "5907a0314289bdcdb382af38cc33d6dc";

    @GET("?method=flickr.photos.getRecent&api_key="+API_KEY+"&format=json&nojsoncallback=1")
    Call<FlickrResponse> getFlickrPhotos(); //annotated by previous line

         Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://api.flickr.com/services/rest/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
