package com.bignerdranch.android.photogallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by tonyk_000 on 3/10/2016.
 */
public class ThumbnailDownloader<T> extends HandlerThread {
    private static final String TAG = "ThumbnailDownloader";
    //used to identify messages as download requests
    private static final int MESSAGE_DOWNLOAD = 0;

    //stores a reference to the Handler responsible for queueing download requests as messages
    //into the ThumnailDownloader background thread
    private Handler mRequestHandler;
    //ConcurrentHashMap is a threadsafe version of a HashMap
    private ConcurrentMap<T,String> mRequestMap = new ConcurrentHashMap<>();

    //to hold the Handler from the main thread. Interface is to communicate the response to the main thread
    private Handler mResponseHandler;
    private ThumbnailDownloadListener<T> mThumbnailDownlaodListener;

    /*
    Will be called when an image has fully downlaoded and ready to be added to UI.
    Using a listener delegates the task of setting the downloaded image to PhotoGalleryFragment.
    This separates the downloading from the UI updating
     */
    public interface ThumbnailDownloadListener<T>{
        void onThumbnailDownloaded(T target, Bitmap thumbnail);
    }

    public void setThumbnailDownloadListener(ThumbnailDownloadListener<T> listener){
        mThumbnailDownlaodListener = listener;
    }

    public ThumbnailDownloader(Handler responseHandler){
        super(TAG);
        mResponseHandler = responseHandler;
    }

    /*
    Defines what the Handler will do when downloaded messages are pulled off the queue and passed to it.
    This method is called before the Looper checks the queue for te first time, so that's why we implement the
    Handler here
     */
    @Override
    protected void onLooperPrepared(){
        mRequestHandler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                //check message type, retrieve obj value, pass it to handleRequest
                if (msg.what == MESSAGE_DOWNLOAD){
                    T target = (T)msg.obj;
                    Log.i(TAG, "Got a request for URL: " + mRequestMap.get(target));
                    handleRequest(target);
                }
            }
        };
    }

    /*
    helper method where the downloading happens
     */
    private void handleRequest(final T target) {
        try {
            //check for existence of URL
            final String url = mRequestMap.get(target);

            if(url == null){
                return;
            }

            //pass URL into FlickrFetchr
            byte[] bitmapBytes = new FlickrFetchr().getUrlBytes(url);
            final Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
            Log.i(TAG, "Bitmap created");

            //posts the message to the Handler
            mResponseHandler.post(new Runnable() {
                /*
                because mResponseHandler is now associated with the main thread (see class' constructor),
                all code inside run() is executed in main thread
                 */
                @Override
                public void run() {
                    //double check request map, bc recyclerView recycles views :) Makes sure PhotoHolder gets right URL
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        if (!Objects.equals(mRequestMap.get(target), url)){
                            return;
                        }
                    }
                    //removes PhotoHolder-URL mapping and sets the bitmap to the PhotoHolder
                    mRequestMap.remove(target);
                    mThumbnailDownlaodListener.onThumbnailDownloaded(target, bitmap);
                }
            });
        } catch (IOException ioe){
            Log.e(TAG, "Error downloading image", ioe);
        }
    }

    public void queueThumbnail(T target, String url){
        Log.i(TAG, "Got a URL: " + url);

        if(url == null){
            mRequestMap.remove(target);
        }else{
            /*
            obtain a mesage directly from mRequestHandler, which automatically
            sets the new Message object's target field to mRequestHandler.
            mRequestHandler is now in charge of processing hte message when it is pulled off hte queue
             */
            mRequestMap.put(target, url); //map the PhotoHolder and the URL for the quest. Using HashMap ensures we're matching the most recent URL for a given PhotoHolder
            mRequestHandler.obtainMessage(MESSAGE_DOWNLOAD, target) //Message's obj field is set to the T target value, which is a PhotoHolder
                    .sendToTarget();
        }
    }

    public void clearQueue(){
        mRequestHandler.removeMessages(MESSAGE_DOWNLOAD);
    }
}
