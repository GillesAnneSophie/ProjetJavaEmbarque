package com.example.server;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void onClickEventDownloadButton(View view){
        Intent launchActivityDownload = new Intent(MainActivity.this, DownloadActivity.class);
        startActivity(launchActivityDownload);
    }
    public void onClickEventShareButton(View view){
        Intent launchActivityShare = new Intent(MainActivity.this, ShareActivity.class);
        startActivity(launchActivityShare);
    }
}