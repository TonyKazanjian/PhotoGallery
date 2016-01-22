package com.bignerdranch.android.photogallery;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tonyk_000 on 1/15/2016.
 */
public class PhotoGalleryFragment extends Fragment {

    private static final String TAG = "PhotoGalleryFragment";

    private RecyclerView mPhotoRecyclerView;
    private List<FlickrResponse.PhotosEntity.PhotoEntity> mItems = new ArrayList<>();

    public static PhotoGalleryFragment newInstance(){
        return new PhotoGalleryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //retains the fragment so taht rotation does not repeatedly fire off new AsyncTasks to fetch data
        setRetainInstance(true);
        //this starts the AsyncTask and fires up the background thread and calls doInBackground
        new FetchItemsTask().execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_photo_gallery, container, false);

        mPhotoRecyclerView = (RecyclerView)v.findViewById(R.id.fragment_photo_gallery_recycler_view);
        mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        setupAdapter();

        return v;
    }

    private void setupAdapter(){
        if (isAdded()){ //this confirms that the fragment has been added to the activity, and that the activity will not be null
            mPhotoRecyclerView.setAdapter(new PhotoAdapter(mItems));
        }
    }

    //third parameter is the result produced by AsyncTask. It sets the value returned by doInBackground,
    //as wellas the type of onPostExecute's input parameter
    private class FetchItemsTask extends AsyncTask<Void,Void,List<FlickrResponse.PhotosEntity.PhotoEntity>>{
        @Override
        protected List<FlickrResponse.PhotosEntity.PhotoEntity> doInBackground(Void... params){
            return new FlickrFetchr().fetchItems();
        }

        //onPostExecute is run on the main thread, after doInBackground completes
        //accepts as input the list you fetched and returned inside doInBackground(...), puts it in mItems,
        //and updates the adapter
        @Override
        protected void onPostExecute(List<FlickrResponse.PhotosEntity.PhotoEntity> items){
            mItems = items;
            setupAdapter();
        }
    }

    private class PhotoHolder extends RecyclerView.ViewHolder {
        private TextView mTitleTextView;

        public PhotoHolder(View itemView){
            super(itemView);

            mTitleTextView = (TextView) itemView;
        }

        public void bindGalleryItem(FlickrResponse.PhotosEntity.PhotoEntity item){
            mTitleTextView.setText(item.toString());
        }
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder>{

        private List<FlickrResponse.PhotosEntity.PhotoEntity> mPhotoEntities;

        public PhotoAdapter(List<FlickrResponse.PhotosEntity.PhotoEntity> photoEntities){
            mPhotoEntities = photoEntities;
        }

        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TextView textView = new TextView(getActivity());
            return new PhotoHolder(textView);
        }

        @Override
        public void onBindViewHolder(PhotoHolder holder, int position) {
            FlickrResponse.PhotosEntity.PhotoEntity photoEntity= mPhotoEntities.get(position);
            holder.bindGalleryItem(photoEntity);

        }

        @Override
        public int getItemCount() {
            return mPhotoEntities.size();
        }
    }
}
