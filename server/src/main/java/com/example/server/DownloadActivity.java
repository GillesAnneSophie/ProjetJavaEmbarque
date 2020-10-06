package com.example.server;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import java.io.File;

// For Network connectivity, Activity should inherit from FramgmentActivity. Since AppCompatActivity inherit from FragmentActivity, it should be okay.
public class DownloadActivity extends AppCompatActivity {
    private final static String CLASSNAME = DownloadActivity.class.getSimpleName();
    private static final int PERMISSION_REQUEST_CODE = 1;
    private Button btn_download;
    private long downloadId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        registerReceiver(onDownloadComplete,new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        btn_download=findViewById(R.id.downloadVideoButton);
        btn_download.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                Log.i(CLASSNAME, "onClick");
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (checkPermission()){
                        beginDownload("https://ia800201.us.archive.org/22/items/ksnn_compilation_master_the_internet/ksnn_compilation_master_the_internet_512kb.mp4");
                    } else {
                        requestPermission();
                    }
                }
                else{
                    Toast.makeText(DownloadActivity.this,"Permission Is Granted..",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void beginDownload(String file_link){
        File file=new File(getExternalFilesDir(null),"Dummy");

        checkPermission();

        Log.i(CLASSNAME, file_link);
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
            Log.i(CLASSNAME,"IF : "+Uri.fromFile(file).toString());
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
                Log.i(CLASSNAME, "getExternalStorageState : " + Environment.getExternalStorageState() );
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
            Log.i(CLASSNAME, "CheckPermission true");
            return true;
        } else {
            Log.i(CLASSNAME, "CheckPermission False");
            return false;
        }
    }


    private void requestPermission() {

        ActivityCompat.requestPermissions(DownloadActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }
}