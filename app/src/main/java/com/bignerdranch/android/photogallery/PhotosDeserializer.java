package com.bignerdranch.android.photogallery;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by tonyk_000 on 1/20/2016.
 */
public class PhotosDeserializer implements JsonDeserializer<Photos> {


    @Override
    public Photos deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        JsonElement content = json.getAsJsonObject().get("photos");

        return new Gson().fromJson(content, Photos.class);
    }
}
