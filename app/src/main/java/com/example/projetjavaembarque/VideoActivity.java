package com.example.projetjavaembarque;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

public class VideoActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        mediaPlayer = new MediaPlayer();
        SurfaceView surface = (SurfaceView)findViewById(R.id.surfaceView);
        SurfaceHolder holder = surface.getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        holder.setFixedSize(400, 300);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mediaPlayer.setDisplay(holder);
            mediaPlayer.setDataSource(Environment.getExternalStorageDirectory().getPath()+"/Movies/video.mp4");
            //mediaPlayer.setDataSource("https://ia800201.us.archive.org/22/items/ksnn_compilation_master_the_internet/ksnn_compilation_master_the_internet_512kb.mp4");
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IllegalArgumentException | IllegalStateException | IOException e) {
            Log.d("MEDIA_PLAYER",
                    e.getMessage());
        }
    }
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) { }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mediaPlayer.stop();
        mediaPlayer.release();
    }
}