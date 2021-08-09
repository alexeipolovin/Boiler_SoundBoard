package ru.buba.boiler;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.RawRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Map<Integer, Integer> songMap;

    ImageButton playButton;
    ImageButton stopButton;

    ListView listView;

    long lastId = 0;

    private MediaPlayer mediaPlayer = null;

    public Toolbar toolbar;

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

        setSupportActionBar(toolbar);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, DirectoryProvider.listofRaw());

        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> play(id));

        View.OnClickListener playOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.playButton:
                        play(lastId);
                        break;
                    case R.id.stopButton:
                        pause(v);
                        break;
                }
            }
        };


        playButton = toolbar.findViewById(R.id.playButton);
        stopButton = toolbar.findViewById(R.id.stopButton);

        playButton.setOnClickListener(playOnClickListener);
        stopButton.setOnClickListener(playOnClickListener);

        mediaPlayer = MediaPlayer.create(this, R.raw.sanya);

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