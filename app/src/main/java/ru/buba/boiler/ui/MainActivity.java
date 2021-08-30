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

package ru.buba.boiler.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import ru.buba.boiler.utils.Player;
import ru.buba.boiler.R;
import ru.buba.boiler.utils.SongData;
import ru.buba.boiler.utils.WebConnector;

public class MainActivity extends AppCompatActivity {

    public static WebConnector webConnector;

    ImageButton playButton;
    ImageButton stopButton;

    TextView textView;
    TextView allTime;
    TextView currTime;

    SeekBar progressBar;

    Handler handler = new Handler();

    ListView listView;

    private static final int playButtonID = R.id.playButton;
    private static final int stopButtonID = R.id.stopButton;

    public MediaPlayer mediaPlayer;
    Player player;

    public Toolbar toolbar;

    public static String baseAuthUrl = "https://barybians.ru/api/v2/auth?username=Test&password=TEST";
    public static String baseSongListUrl = "https://barybians.ru/api/v2/boiler";
    private ArrayList<SongData> songDataArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);


        player = new Player(Player.PlayType.STREAM, "https://barybians.ru/boiler/timestamp.mp3");

        allTime = findViewById(R.id.allTime);
        currTime = findViewById(R.id.currTime);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setMax(100);

        listView = findViewById(R.id.listView);
        toolbar = findViewById(R.id.toolbar);
        textView = toolbar.findViewById(R.id.toolbarText);

        setSupportActionBar(toolbar);
        webConnector = new WebConnector();
        webConnector.auth(this, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                String tokenBase = "";
                Log.d("Boiler", responseBody);
//                getSongsList(context);
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(responseBody);
                    String token = jsonObject.getString("token");
                    webConnector.setToken(token);
                    webConnector.getSongsList(MainActivity.this, new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            Toast.makeText(MainActivity.this, "Error in http response", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            String responseBody = response.body().string();
                            Log.d("Boiler SongList", responseBody);
                            try {
                                songDataArrayList = new ArrayList<SongData>();
                                JSONArray jsonArray = new JSONArray(responseBody);
                                ArrayList<String> songNames = new ArrayList<>();
                                for (int index = 0; index < jsonArray.length(); index++) {
                                    Log.d("Boiler", jsonArray.getJSONObject(index).getString("name"));
                                    songNames.add(jsonArray.getJSONObject(index).getString("name"));
                                    SongData songData = new SongData(0, jsonArray.getJSONObject(index).getString("name"), jsonArray.getJSONObject(index).getString("mp3"));
                                    songDataArrayList.add(songData);

                                }
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, songNames);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listView.setAdapter(adapter);
                                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                if (songDataArrayList.get((int) id) != null) {
                                                    player.stop();
                                                    player.playAsync(songDataArrayList.get((int) id).getTimestamp());

                                                }
                                            }
                                        });
                                    }
                                });

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        listView.setAdapter(webConnector.getAdapter());

        View.OnClickListener playOnClickListener = v -> {
            switch (v.getId()) {
                case playButtonID:
                    break;
                case stopButtonID:
                    player.pause();
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + v.getId());
            }
        };


        playButton = toolbar.findViewById(R.id.playButton);
        stopButton = toolbar.findViewById(R.id.stopButton);

        stopButton.setOnClickListener(playOnClickListener);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (player.isPlaying()) {
                    handler.removeCallbacks(updater);
                    player.pause();
                } else {
                    player.start();
                    updateSeekBar();
                }
            }
        });
    }

    Runnable updater = new Runnable() {
        @Override
        public void run() {
            updateSeekBar();
            long currentDuration = player.getCurrentPosition();
            currTime.setText(milisecToTimer(currentDuration));
        }
    };

    void updateSeekBar() {
        if (player.isPlaying()) {
            progressBar.setProgress((int) ((float) player.getCurrentPosition() / player.getDuration()) * 100);
            handler.postDelayed(updater, 1000);
        }
    }

    String milisecToTimer(long time) {
        String timerString = "";
        String secondsString;

        int hours = (int) (time / (1000 * 60 * 60));
        int minutes = (int) (time % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((time % (1000 * 60 * 60)) % (1000 * 60) / 1000);

        if (hours > 0) {
            timerString = hours + ":";
        }
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        timerString = timerString + minutes + ":" + secondsString;
        return timerString;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("OldBoiler");
        menu.add("Exit");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        CharSequence title = item.getTitle();
        if ("OldBoiler".equals(title)) {
            Intent intent = new Intent(this, OldBoiler.class);
            startActivity(intent);
        } else if ("Exit".equals(title)) {
            SharedPreferences sharedPreferences = getSharedPreferences("auth", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("token", "");
            editor.apply();
        }

        return super.onOptionsItemSelected(item);
    }

}