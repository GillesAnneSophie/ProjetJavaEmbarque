package com.example.server;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private final static String CLASSNAME = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void onClickEventDownloadButton(View view){
        Log.i(CLASSNAME, "User clicked on Download button");
        Intent launchActivityDownload = new Intent(MainActivity.this, DownloadActivity.class);
        startActivity(launchActivityDownload);
    }
    public void onClickEventShareButton(View view){
        Log.i(CLASSNAME, "User clicked on Share button");
        Intent launchActivityShare = new Intent(MainActivity.this, ShareActivity.class);
        startActivity(launchActivityShare);
    }
}