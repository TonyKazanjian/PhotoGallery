package com.bignerdranch.android.photogallery;

/**
 * Created by tonyk_000 on 1/18/2016.
 */
public class GalleryItem {
    private String title;
    private String id;


    @Override
    public String toString(){
        return title;
    }

    public String getCaption() {
        return title;
    }

    public void setCaption(String caption) {
        title = caption;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
