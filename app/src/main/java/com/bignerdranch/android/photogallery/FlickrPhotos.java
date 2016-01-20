package com.bignerdranch.android.photogallery;

import java.util.List;

/**
 * This is the next level in the JSON hierarchy, meant to handle the array of objectsd
 */
public class FlickrPhotos {

    public List<Photo> mPhotos;

    private int page;
    private int pages;
    private int perpage;
    private int total;

    public void setPage(int page) {
        this.page = page;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public void setPerpage(int perpage) {
        this.perpage = perpage;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
