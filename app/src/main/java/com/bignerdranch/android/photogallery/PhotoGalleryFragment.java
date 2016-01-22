package com.bignerdranch.android.photogallery;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by tonyk_000 on 1/15/2016.
 */
public class PhotoGalleryFragment extends Fragment {

    private static final String TAG = "PhotoGalleryFragment";

    private RecyclerView mPhotoRecyclerView;
    private Photos mItems;

    public static PhotoGalleryFragment newInstance(){
        return new PhotoGalleryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //retains the fragment so taht rotation does not repeatedly fire off new AsyncTasks to fetch data
        setRetainInstance(true);
        //this starts the AsyncTask and fires up the background thread and calls doInBackground

            FlickrService mFlickrService = FlickrService.retrofit.create(FlickrService.class);
            Call<Photos> call = mFlickrService.getFlickrPhotos();
            new FetchItemsTask().execute(call);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_photo_gallery, container, false);

        mPhotoRecyclerView = (RecyclerView)v.findViewById(R.id.fragment_photo_gallery_recycler_view);
        mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        return v;
    }

    private void setupAdapter(){
        if (isAdded()){ //this confirms that the fragment has been added to the activity, and that the activity will not be null
            mPhotoRecyclerView.setAdapter(new PhotoAdapter(mItems.getPhoto()));
        }
    }

    //third parameter is the result produced by AsyncTask. It sets the value returned by doInBackground,
    //as wellas the type of onPostExecute's input parameter
    public class FetchItemsTask extends AsyncTask<Call,Void, Photos>{
        @Override
        protected Photos doInBackground(Call... params){
            try {
                Call<Photos> call = params[0];
                Response<Photos> response = call.execute();
                return response.body();
            } catch (IOException ioe){
                Log.e(TAG, "Failed to fetch items", ioe);
            }
            return null;
        }

        //onPostExecute is run on the main thread, after doInBackground completes
        //accepts as input the list you fetched and returned inside doInBackground(...), puts it in mItems,
        //and updates the adapter
        @Override
        protected void onPostExecute(Photos items){
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

        public void bindGalleryItem(GalleryItem item){
            mTitleTextView.setText(item.toString());
        }
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder>{

        private List<GalleryItem> mGalleryItems;

        public PhotoAdapter(List<GalleryItem> galleryItems){
            mGalleryItems = galleryItems;
        }

        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TextView textView = new TextView(getActivity());
            return new PhotoHolder(textView);
        }

        @Override
        public void onBindViewHolder(PhotoHolder holder, int position) {
            GalleryItem galleryItem = mGalleryItems.get(position);
            holder.bindGalleryItem(galleryItem);

        }

        @Override
        public int getItemCount() {

            if (mGalleryItems != null){
                return mGalleryItems.size();
            }
            else {
                return 0;
            }
        }
    }
}
