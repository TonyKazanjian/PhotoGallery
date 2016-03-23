package com.bignerdranch.android.photogallery;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

/**
 * Created by tonyk_000 on 3/22/2016.
 */
public class PhotoPageFragment extends VisibleFragment {
    private static final String ARG_URI = "photo_page_url";

    private Uri mUri;
    private WebView mWebView;
    private ProgressBar mProgressBar;

    //forwarding the URL to display as a fragment argument
    public static PhotoPageFragment newInstance(Uri uri){
        Bundle args = new Bundle();
        args.putParcelable(ARG_URI, uri);

        PhotoPageFragment fragment = new PhotoPageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUri = getArguments().getParcelable(ARG_URI);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photo_page, container, false);

        mProgressBar = (ProgressBar)v.findViewById(R.id.fragment_photo_page_progress_bar);
        mProgressBar.setMax(100); //WebChromeClient reports in range 0 - 100

        mWebView = (WebView)v.findViewById(R.id.fragment_photo_page_web_view);
        //getting an instance of WebSettings to set JavaScript enabled
        mWebView.getSettings().setJavaScriptEnabled(true);
        //WebViewClient is an event interface that can respond to rendering events
        mWebView.setWebViewClient(new WebViewClient() {
            //this method determines what will happen when a new URL is loaded into the WebView
            //like by pressing a link. By returning false, you are letting the WebView handle it
            //rather than doing anything with it yourself.
            //We override the default implementation in order to use our own webview
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });

        //To hook up progressbar, we need WebView's WebChromeClient callback, which is an interface
        //for reacting to events that should change elements of Chrome around the browser
        mWebView.setWebChromeClient(new WebChromeClient(){
            //for progress updates
            public void onProgressChanged(WebView webView, int newProgress){
                if (newProgress == 100){
                    mProgressBar.setVisibility(View.GONE);
                } else {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mProgressBar.setProgress(newProgress);
                }
            }
            //for title updates
            public void onReceivedTitle(WebView webView, String title){
                AppCompatActivity activity = (AppCompatActivity)getActivity();
                activity.getSupportActionBar().setSubtitle(title);
            }
        });
        mWebView.loadUrl(mUri.toString());
        return v;
    }
}
