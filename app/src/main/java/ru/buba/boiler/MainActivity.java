package ru.buba.boiler;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RawRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.JsonObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.*;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    public static WebConnector webConnector;
    Map<Integer, Integer> songMap;

    ImageButton playButton;
    ImageButton stopButton;

    static TextView textView;

    ListView listView;

    long lastId = 0;

    private static String token;

    private MediaPlayer mediaPlayer = null;

    public static Toolbar toolbar;

    public static String baseAuthUrl = "https://barybians.ru/api/v2/auth?username=Test&password=TEST";
    public static String baseSongListUrl = "https://barybians.ru/api/v2/boiler";

    public void auth() {
        webConnector = new WebConnector();
        webConnector.post(baseAuthUrl, "", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                String tokenBase = "";
                Log.d("Boiler", responseBody);
                JSONObject jsonObject;

                try {
                    jsonObject = new JSONObject(responseBody);
                    tokenBase = jsonObject.getString("token");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String finalToken = token;
                token = tokenBase;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        textView.setText(finalToken);
                    }
                });

            }
        });
    }

    public void getSongsList() {
        webConnector.get(baseSongListUrl, "", token, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                Log.d("Boiler SongList", responseBody);
            }
        });
    }

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

        Field[] fields = R.raw.class.getFields();
        ArrayList<Integer> arrayList = new ArrayList<>();
        for(Field field : fields) {
            try {
                @RawRes int rawId = (Integer) field.get(null);
                String name = field.getName();
                        arrayList.add(this.getResources().getIdentifier(field.getName(), "raw",this.getPackageName()));

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        for(int i = 0; i < arrayList.size(); i++) {
            songMap.put(i, 1800000 + i);
        }


        listView = findViewById(R.id.listView);
        toolbar = findViewById(R.id.toolbar);
        textView = toolbar.findViewById(R.id.toolbarText);

        setSupportActionBar(toolbar);
        auth();
        token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJCYXJ5YmlhbnMiLCJhdWQiOiIzNSIsImlhdCI6MTM1Njk5OTUyNCwibmJmIjoxMzU3MDAwMDAwfQ.Tjeta5peBDb8EKZkzDoHGXIo3uxHJ0SmS0aPUO_IzA0";
        getSongsList();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, DirectoryProvider.listofRaw());

        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> playAsync());

        View.OnClickListener playOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.playButton:
                        playAsync();
                        break;
                    case R.id.stopButton:
                        pause(v);
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + v.getId());
                }
            }
        };


        playButton = toolbar.findViewById(R.id.playButton);
        stopButton = toolbar.findViewById(R.id.stopButton);

        playButton.setOnClickListener(playOnClickListener);
        stopButton.setOnClickListener(playOnClickListener);

//        mediaPlayer = MediaPlayer.create(this, R.raw.sanya);

    }

    void playAsync() {
        mediaPlayer = MediaPlayer.create(this, R.raw.sanya);
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            public boolean onError(MediaPlayer mp, int what, int extra) {
                mediaPlayer.reset();
                return false;
            }
        });
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
                mediaPlayer.start();
            }
        });
        try {
            mediaPlayer.setDataSource("https://barybians.ru/boiler/1626598850.mp3");
            mediaPlayer.prepareAsync();
        } catch (IllegalArgumentException e) {
        } catch (IllegalStateException e) {
        } catch (IOException e) {
        }
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaPlayer.release();
            }
        });
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
//            switch ((int) id) {
//                case 0:
//                    mediaPlayer = MediaPlayer.create(this, R.raw.iamdrunk);
//                    break;
//                case 1:
//                    mediaPlayer = MediaPlayer.create(this, R.raw.idontlikefish);
//                    break;
//                case 2:
//                    mediaPlayer = MediaPlayer.create(this, R.raw.narodmudr);
//                    break;
//                case 3:
//                    mediaPlayer = MediaPlayer.create(this, R.raw.narodmudrtwo);
//                    break;
//                case 4:
//                    mediaPlayer = MediaPlayer.create(this, R.raw.sanya);
//                    break;
//                case 5:
//                    mediaPlayer = MediaPlayer.create(this, R.raw.whoontheshpals);
//                    break;
//                case 6:
//                    mediaPlayer = MediaPlayer.create(this, R.raw.youaredegrod);
//                    break;
//                case 7:
//                    mediaPlayer = MediaPlayer.create(this, R.raw.youarestupid);
//                    break;
//                case 8:
//                    mediaPlayer = MediaPlayer.create(this, R.raw.zaebal);
//                    break;
//                default:
//                    mediaPlayer = MediaPlayer.create(this, R.raw.sanya);
//                    break;
//            }
        }


        mediaPlayer.setOnCompletionListener(mp -> stopPlayer());
        if(play)
            mediaPlayer.start();
    }

    public void pause(View v) {
        if(mediaPlayer != null)
            mediaPlayer.pause();
    }

    private void stopPlayer() {
        if(mediaPlayer != null) {
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