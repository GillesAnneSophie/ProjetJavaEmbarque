package com.example.projetjavaembarque;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoActivity extends AppCompatActivity {
    private final static String CLASSNAME = VideoActivity.class.getSimpleName();
    private VideoView videoView;
    private int currentPosition = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        videoView = findViewById(R.id.videoView);

        MediaController vidControl = new MediaController(this);
        vidControl.setAnchorView(videoView);
        videoView.setMediaController(vidControl);

        videoView.setVideoPath("https://ia800201.us.archive.org/22/items/ksnn_compilation_master_the_internet/ksnn_compilation_master_the_internet_512kb.mp4");
        videoView.start();
        Log.i(CLASSNAME, "Start video");
    }

    @Override
    public void onResume() {
        super.onResume();
        videoView.start();
        videoView.seekTo(currentPosition);
        Log.i(CLASSNAME, "Resume video and set to last known position");
    }
    @Override
    public void onPause() {
        super.onPause();
        currentPosition = videoView.getCurrentPosition();
        videoView.pause();
        Log.i(CLASSNAME, "Pause video and save current position");
    }
}