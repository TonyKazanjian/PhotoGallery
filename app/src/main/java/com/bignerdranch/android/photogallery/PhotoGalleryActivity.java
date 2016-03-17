package com.bignerdranch.android.photogallery;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

public class PhotoGalleryActivity extends SingleFragmentActivity {

    //needed for PollService to call in order to wrap the result in a PendingIntent for setting to a notification
    public static Intent newIntent(Context context){
        return new Intent(context, PhotoGalleryActivity.class);
    }

    @Override
    public Fragment createFragment(){
        return PhotoGalleryFragment.newInstance();
    }
}
