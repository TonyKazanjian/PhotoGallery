package com.bignerdranch.android.photogallery;

import java.util.List;

/**
 * Created by tonyk_000 on 1/20/2016.
 */
public class Photos {

    private int page;
    private int pages;
    private int perpage;
    private int total;
    private List<GalleryItem> photo;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public int getPerpage() {
        return perpage;
    }

    public void setPerpage(int perpage) {
        this.perpage = perpage;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<GalleryItem> getPhoto() {
        return photo;
    }

    public void setPhoto(List<GalleryItem> photo) {
        this.photo = photo;
    }
}
