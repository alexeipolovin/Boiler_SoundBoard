/*
 * Copyright 2021 Alexei Polovin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *
 */

package ru.buba.boiler;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ru.buba.boiler.utils.SongData;
import ru.buba.boiler.utils.WebConnector;

public class MainActivity extends AppCompatActivity {

    public static WebConnector webConnector;
    Map<Integer, Integer> songMap;

    ImageButton playButton;
    ImageButton stopButton;

    TextView textView;
    TextView allTime;
    TextView currTime;

    SeekBar progressBar;

    Handler handler = new Handler();

    ListView listView;

    long lastId = 0;
    private static final int playButtonID = R.id.playButton;
    private static final int stopButtonID = R.id.stopButton;

    public MediaPlayer mediaPlayer;

    public Toolbar toolbar;

    public static String baseAuthUrl = "https://barybians.ru/api/v2/auth?username=Test&password=TEST";
    public static String baseSongListUrl = "https://barybians.ru/api/v2/boiler";


    public static int getId(String resourceName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(resourceName);
            return idField.getInt(idField);
        } catch (Exception e) {
            throw new RuntimeException("No resource ID found for: "
                    + resourceName + " / " + c, e);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        songMap = new HashMap<Integer, Integer>();

        allTime = findViewById(R.id.allTime);
        currTime = findViewById(R.id.currTime);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setMax(100);

        listView = findViewById(R.id.listView);
        toolbar = findViewById(R.id.toolbar);
        textView = toolbar.findViewById(R.id.toolbarText);

        setSupportActionBar(toolbar);
        webConnector = new WebConnector();
        webConnector.auth();
        ArrayList<SongData> songDataArrayList= webConnector.getSongsList(this);
        listView.setAdapter(webConnector.getAdapter());

        View.OnClickListener playOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case playButtonID:
                        play(0);
                        break;
                    case stopButtonID:
                        pause(v);
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + v.getId());
                }
            }
        };


        playButton = toolbar.findViewById(R.id.playButton);
        stopButton = toolbar.findViewById(R.id.stopButton);

//        playButton.setOnClickListener(playOnClickListener);
        stopButton.setOnClickListener(playOnClickListener);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()) {
                    handler.removeCallbacks(updater);
                    mediaPlayer.pause();
                } else {
                    mediaPlayer.start();
                     updateSeekBar();
                }
            }
        });
        prepareMediaPlayer();
    }

    Runnable updater = new Runnable() {
        @Override
        public void run() {
            updateSeekBar();
            long currentDuration =  mediaPlayer.getCurrentPosition();
            currTime.setText(milisecToTimer(currentDuration));
        }
    };

    void updateSeekBar() {
        if(mediaPlayer.isPlaying()) {
            progressBar.setProgress((int)((float) mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration()) * 100);
            handler.postDelayed(updater, 1000);
        }
    }

    String milisecToTimer(long time) {
        String timerString = "";
        String secondsString;

        int hours = (int) (time / ( 1000 * 60 * 60));
        int minutes = (int) (time % (1000 * 60 * 60)) / (1000*60);
        int seconds = (int) ((time % (1000 * 60 * 60)) % (1000*60) / 1000);

        if(hours > 0) {
            timerString = hours + ":";
        }
        if(seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        timerString = timerString + minutes + ":" + secondsString;
        return timerString;

    }

    void prepareMediaPlayer() {
        try {

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource("https://file-examples-com.github.io/uploads/2017/11/file_example_MP3_700KB.mp3");
            mediaPlayer.prepare();
            mediaPlayer.start();
            allTime.setText(milisecToTimer(mediaPlayer.getDuration()));
        } catch (IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG);
        }
    }

    public void play(long id) {
        boolean play = true;

        if (mediaPlayer != null)
            if (mediaPlayer.isPlaying()) {
                if (lastId == id) {
                    mediaPlayer.pause();
                    play = false;
                } else {
                    lastId = id;
                    stopPlayer();
                }
            } else {
                if (lastId != id) {
                    lastId = id;
                    stopPlayer();
                }
            }

        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, songMap.get((int) id));
        }


        mediaPlayer.setOnCompletionListener(mp -> stopPlayer());
        if (play)
            mediaPlayer.start();
    }

    public void pause(View v) {
        if (mediaPlayer != null)
            mediaPlayer.pause();
    }

    private void stopPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopPlayer();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("OldBoiler");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent = new Intent(this, OldBoiler.class);
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }

}