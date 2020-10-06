package com.example.server;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;

// For Network connectivity, Activity should inherit from FramgmentActivity. Since AppCompatActivity inherit from FragmentActivity, it should be okay.
public class DownloadActivity extends FragmentActivity implements DownloadCallback {
    private final static String CLASSNAME = DownloadActivity.class.getSimpleName();

    // Keep a reference to the NetworkFragment, which owns the AsyncTask object
    // that is used to execute network ops.
    private NetworkFragment networkFragment;

    // Boolean telling us whether a download is in progress, so we don't trigger overlapping
    // downloads with consecutive button clicks.
    private boolean downloading = false;

    InputStream inputStream = null;
    //...
    // Adapt for VIdeo View
    //Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
    //ImageView imageView = (ImageView) findViewById(R.id.image_view);
    //imageView.setImageBitmap(bitmap);

    // Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
    // ImageView imageView = (ImageView) findViewById(R.id.image_view);
    // imageView.setImageBitmap(bitmap);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        //networkFragment = NetworkFragment.getInstance(getSupportFragmentManager(), "https://abhiandroid.com/ui/wp-content/uploads/2016/04/videoviewtestingvideo.mp4");
        networkFragment = NetworkFragment.getInstance(getSupportFragmentManager(), "https://ia800201.us.archive.org/22/items/ksnn_compilation_master_the_internet/ksnn_compilation_master_the_internet_512kb.mp4");

        startDownload();
    }

    private void startDownload() {
        if (!downloading && networkFragment != null) {
            // Execute the async download.
            networkFragment.startDownload();
            Log.wtf(CLASSNAME,"startDownload() - 1");
            downloading = true;
            Log.wtf(CLASSNAME,"startDownload() - 2");
        }
    }

    //void updateFromDownload(T result);

    @Override
    public void updateFromDownload(Object result) {
        Log.wtf(CLASSNAME,"FUCK");
        // updateFromDownload(result);
        // Update your UI here based on result of download.
    }

    @Override
    public NetworkInfo getActiveNetworkInfo() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo;
    }

    @Override
    public void onProgressUpdate(int progressCode, int percentComplete) {
        switch(progressCode) {
            // You can add UI behavior for progress updates here.
            case Progress.ERROR:
                Log.e(CLASSNAME,"ERROR");
            //...
                break;
            case Progress.CONNECT_SUCCESS:
            //...
                Log.i(CLASSNAME,"Progress.CONNECT_SUCCESS");
                break;
            case Progress.GET_INPUT_STREAM_SUCCESS:
            //...
                Log.i(CLASSNAME,"Progress.GET_INPUT_STREAM_SUCCESS");
                break;
            case Progress.PROCESS_INPUT_STREAM_IN_PROGRESS:
                Log.i(CLASSNAME,"Progress.PROCESS_INPUT_STREAM_IN_PROGRESS");
            //...
                break;
            case Progress.PROCESS_INPUT_STREAM_SUCCESS:
                Log.i(CLASSNAME,"Progress.PROCESS_INPUT_STREAM_SUCCESS");
            //...
                break;
        }
    }

    @Override
    public void finishDownloading() {
        downloading = false;
        if (networkFragment != null) {
            networkFragment.cancelDownload();
        }
    }
}