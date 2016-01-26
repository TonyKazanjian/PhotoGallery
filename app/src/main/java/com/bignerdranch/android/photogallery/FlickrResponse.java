package com.bignerdranch.android.photogallery;

import java.util.List;

/**
 * This is the next level in the JSON hierarchy, meant to handle the array of objectsd
 */
public class FlickrResponse {


    private PhotosEntity photos;

    private String stat;

    public void setPhotos(PhotosEntity photos) {
        this.photos = photos;
    }

    public void setStat(String stat) {
        this.stat = stat;
    }

    public PhotosEntity getPhotos() {
        return photos;
    }

    public String getStat() {
        return stat;
    }

    public static class PhotosEntity {
        private int page;
        private int pages;
        private int perpage;
        private int total;
        /**
         * id : 23905305214
         * owner : 120728468@N08
         * secret : 13b5be5336
         * server : 1650
         * farm : 2
         * title : tmpVpADJY
         * ispublic : 1
         * isfriend : 0
         * isfamily : 0
         */

        private List<PhotoEntity> photo;

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

        public void setPhoto(List<PhotoEntity> photo) {
            this.photo = photo;
        }

        public int getPage() {
            return page;
        }

        public int getPages() {
            return pages;
        }

        public int getPerpage() {
            return perpage;
        }

        public int getTotal() {
            return total;
        }

        public List<PhotoEntity> getPhoto() {
            return photo;
        }

        public static class PhotoEntity {
            private String id;
            private String title;

            public void setId(String id) {
                this.id = id;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getId() {
                return id;
            }

            @Override
            public String toString(){
                return title + " ("+id+")";
            }

            public String getTitle() {
                return title;
            }
        }
    }
}
