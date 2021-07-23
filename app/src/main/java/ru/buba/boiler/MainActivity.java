package ru.buba.boiler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity {

    Map<String, String> songMap;

    ImageButton firstButton;
    ImageButton secondButton;
    Button thirdButton;
    Button fourthButton;
    Button fifthButton;
    Button sixthButton;
    Button seventhButton;
    Button eighthButton;
    Button ninthButton;

    ImageView imageView;

    GridLayout gridLayout;

    ListView listView;

    long lastId = 0;

    MediaPlayer mediaPlayer = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        songMap = new HashMap<String, String>();

        songMap.put("iamdrunk.mp3", "Я в говно");
        songMap.put("idontlikefish.mp3", "Не любит рыбу");
        songMap.put("narodmudr.mp3", "Мудрость");
        songMap.put("narodmudrtwo.mp3", "Знание-сила");
        songMap.put("sanya.mp3", "Санёк,брат...");
        songMap.put("whoontheshpals.mp3", "Кто на шпалах?");
        songMap.put("youaredegrod.mp3", "Ты деградируешь");
        songMap.put("youarestupid.mp3", "Идиот");
        songMap.put("zaebal.mp3", "Достал");

//        gridLayout = findViewById(R.id.gridLayout);

        ArrayList<String> arrayList = DirectoryProvider.listofRaw();
        ArrayList<String> fileNames = new ArrayList<>();

        for(int i = 0; i < arrayList.size(); i++) {
            fileNames.add(songMap.get(arrayList.get(i)));
            Toast.makeText(this, songMap.get(arrayList.get(i)),Toast.LENGTH_SHORT).show();
        }

        listView = findViewById(R.id.listView);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, fileNames);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> play(id));

//        View.OnClickListener onClickListener = v -> {
//            play(v);
//        };

//        for(int i = 0; i < arrayList.size(); i++) {
//            Button button = new Button(this);
//            button.setText(songMap.get(arrayList.get(i)));
//            int finalI = i;
//            button.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    play(finalI);
//                }
//            });
//            gridLayout.addView(button);
//        }

        View.OnClickListener playOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.button:
                        pause(v);
                        break;
                    case R.id.button2:
                        play(lastId);
                        break;
                }
            }
        };


        firstButton = findViewById(R.id.button);
        secondButton = findViewById(R.id.button2);

        firstButton.setOnClickListener(playOnClickListener);
        secondButton.setOnClickListener(playOnClickListener);
//        thirdButton = findViewById(R.id.thirdButton);
//        fourthButton = findViewById(R.id.fourthButton);
//        fifthButton = findViewById(R.id.fifthButton);
//        sixthButton = findViewById(R.id.sixthButton);
//        seventhButton = findViewById(R.id.seventhButton);
//        eighthButton = findViewById(R.id.eithButton);
//        ninthButton = findViewById(R.id.ninthButton);

//        imageView = findViewById(R.id.imageView);
//        Bitmap bitmap = BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.boiler);
//        imageView.setImageBitmap(bitmap);

        mediaPlayer = MediaPlayer.create(this, R.raw.sanya);

    }
//    public void setCompletionListener(MediaPlayer mediaPlayer) {
//        mediaPlayer.setOnCompletionListener(mp -> {
//            stopPlayer();
//            Toast.makeText(MainActivity.this, "MediaPlayer is null", Toast.LENGTH_LONG).show();
//        });
//    }

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
                if (lastId == id) {
                    play = true;
                } else {
                    lastId = id;
                    stopPlayer();
                }
            }
        if (mediaPlayer == null) {
            switch ((int) id) {
//            switch (gridLayout.getChildAt()) {
                case 0:
                    mediaPlayer = MediaPlayer.create(this, R.raw.idontlikefish);
                    break;
                case 1:
                    mediaPlayer = MediaPlayer.create(this, R.raw.whoontheshpals);
                    break;
                case 2:
                    mediaPlayer = MediaPlayer.create(this, R.raw.youarestupid);
                    break;
                case 3:
                    mediaPlayer = MediaPlayer.create(this, R.raw.zaebal);
                    break;
                case 4:
                    mediaPlayer = MediaPlayer.create(this, R.raw.sanya);
                    break;
                case 5:
                    mediaPlayer = MediaPlayer.create(this, R.raw.iamdrunk);
                    break;
                case 6:
                    mediaPlayer = MediaPlayer.create(this, R.raw.youaredegrod);
                    break;
                case 7:
                    mediaPlayer = MediaPlayer.create(this, R.raw.narodmudr);
                    break;
                case 8:
                    mediaPlayer = MediaPlayer.create(this, R.raw.narodmudrtwo);
                    break;
                default:
                    mediaPlayer = MediaPlayer.create(this, R.raw.zaebal);
//            }
            }
        }

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopPlayer();
            }
        });
        if(play)
            mediaPlayer.start();
    }

    public void pause(View v) {
        if(mediaPlayer != null)
            mediaPlayer.pause();
    }

    public void stop(View v) {
        stopPlayer();
    }

    private void stopPlayer() {
        if(mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
//            Toast.makeText(this, "MediaPlayer is null", Toast.LENGTH_LONG).show();
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