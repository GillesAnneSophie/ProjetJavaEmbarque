package com.example.server;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.documentfile.provider.DocumentFile;

import java.io.File;
import java.io.InputStream;

// For Network connectivity, Activity should inherit from FramgmentActivity. Since AppCompatActivity inherit from FragmentActivity, it should be okay.
public class DownloadActivity extends FragmentActivity {
    private final static String CLASSNAME = DownloadActivity.class.getSimpleName();
    private Button btn_download;
    private long downloadId;
    private static final int PERMISSION_REQUEST_CODE = 1;

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
        //networkFragment = NetworkFragment.getInstance(getSupportFragmentManager(), "https://ia800201.us.archive.org/22/items/ksnn_compilation_master_the_internet/ksnn_compilation_master_the_internet_512kb.mp4");

        registerReceiver(onDownloadComplete,new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        // startDownload();
        btn_download=findViewById(R.id.downloadVideoButton);
        btn_download.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                Log.i(CLASSNAME, "onClick");
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){


                    if (checkPermission())
                    {
                        /*** If Storage Permission Is Given, Check External storage is available for read and write***/
                        Uri image_uri = Uri.parse("https://unifiedclothes.com/Unifiedclothes/App_Gallery/thumb_8_121432471036-1432471036-SC-505.jpg");
                        beginDownload("http://www.tutorialspoint.com/java/java_tutorial.pdf");
                        //referenceID = DownloadImage(image_uri);
                    } else {
                        requestPermission();
                    }
                }
                else{
                    Toast.makeText(DownloadActivity.this,"Permission Is Granted..",Toast.LENGTH_SHORT).show();
                }
                /*
                https://medium.com/@aungkyawmyint_26195/downloading-file-properly-in-android-d8cc28d25aca
                String url = "http://speedtest.ftp.otenet.gr/files/test10Mb.db";
                String fileName = url.substring(url.lastIndexOf('/') + 1);
                fileName = fileName.substring(0,1).toUpperCase() + fileName.substring(1);
                File file = Util.createDocumentFile(fileName, context);

                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url))
                        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN)// Visibility of the download Notification
                        .setDestinationUri(Uri.fromFile(file))// Uri of the destination file
                        .setTitle(fileName)// Title of the Download Notification
                        .setDescription("Downloading")// Description of the Download Notification
                        .setRequiresCharging(false)// Set if charging is required to begin the download
                        .setAllowedOverMetered(true)// Set if download is allowed on Mobile network
                        .setAllowedOverRoaming(true);// Set if download is allowed on roaming network

                 */
            }
        });
    }

    private void beginDownload(String file_link){
        File file=new File(getExternalFilesDir("/"),"Dummy");

        //checking if android version is equal and greater than noughat

        //now if download complete file not visible now lets show it
        DownloadManager.Request request=null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            request=new DownloadManager.Request(Uri.parse(file_link))
                    .setTitle("Dummy")
                    .setDescription("Downloading")
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationUri(Uri.fromFile(file))
                    .setRequiresCharging(false)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true);
            Log.i(CLASSNAME,"IF");
        }
        else{
            request=new DownloadManager.Request(Uri.parse(file_link))
                    .setTitle("Dummy")
                    .setDescription("Downloading")
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationUri(Uri.fromFile(file))
                    .setAllowedOverRoaming(true);
            Log.i(CLASSNAME,"ELSE");
        }

        DownloadManager downloadManager=(DownloadManager)getSystemService(DOWNLOAD_SERVICE);
        downloadId=downloadManager.enqueue(request);


    }

    private BroadcastReceiver onDownloadComplete=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long id=intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID,-1);
            if(downloadId==id){
                Toast.makeText(DownloadActivity.this, "Download Completed", Toast.LENGTH_SHORT).show();
            }
        }
    };
    //void updateFromDownload(T result);
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(onDownloadComplete);
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(DownloadActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }


    private void requestPermission() {

        ActivityCompat.requestPermissions(DownloadActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }
}