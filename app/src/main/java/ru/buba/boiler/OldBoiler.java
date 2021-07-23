package ru.buba.boiler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Map;

public class OldBoiler extends AppCompatActivity {

    Button firstButton;
    Button secondButton;
    Button thirdButton;
    Button fourthButton;
    Button fifthButton;
    Button sixthButton;
    Button seventhButton;
    Button eighthButton;
    Button ninthButton;

    MediaPlayer mediaPlayer = null;

    long lastId = R.id.firstButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_old_boiler);
        Map<String, String> songMap;


        ImageView imageView;

        GridLayout gridLayout;

        ListView listView;




        View.OnClickListener onClickListener = v -> {
            play(v);
        };


        firstButton = findViewById(R.id.firstButton);
        secondButton = findViewById(R.id.secondButton);

        thirdButton = findViewById(R.id.thirdButton);
        fourthButton = findViewById(R.id.fourthButton);
        fifthButton = findViewById(R.id.fifthButton);
        sixthButton = findViewById(R.id.sixthButton);
        seventhButton = findViewById(R.id.seventhButton);
        eighthButton = findViewById(R.id.eithButton);
        ninthButton = findViewById(R.id.ninthButton);

//        imageView = findViewById(R.id.imageView);
//        Bitmap bitmap = BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.boiler);
//        imageView.setImageBitmap(bitmap);

        mediaPlayer = MediaPlayer.create(this, R.raw.sanya);

//    public void setCompletionListener(MediaPlayer mediaPlayer) {
//        mediaPlayer.setOnCompletionListener(mp -> {
//            stopPlayer();
//            Toast.makeText(MainActivity.this, "MediaPlayer is null", Toast.LENGTH_LONG).show();
//        });
//    }

        firstButton.setOnClickListener(onClickListener);
        secondButton.setOnClickListener(onClickListener);
        thirdButton.setOnClickListener(onClickListener);
        fourthButton.setOnClickListener(onClickListener);
        fifthButton.setOnClickListener(onClickListener);
        sixthButton.setOnClickListener(onClickListener);
        seventhButton.setOnClickListener(onClickListener);
        eighthButton.setOnClickListener(onClickListener);
        ninthButton.setOnClickListener(onClickListener);
    }
        public void play(View v) {
            boolean play = true;
            int id = v.getId();
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
                switch (v.getId()) {
//            switch (gridLayout.getChildAt()) {
                    case R.id.firstButton:
                        mediaPlayer = MediaPlayer.create(this, R.raw.idontlikefish);
                        break;
                    case R.id.secondButton:
                        mediaPlayer = MediaPlayer.create(this, R.raw.whoontheshpals);
                        break;
                    case R.id.thirdButton:
                        mediaPlayer = MediaPlayer.create(this, R.raw.youarestupid);
                        break;
                    case R.id.fourthButton:
                        mediaPlayer = MediaPlayer.create(this, R.raw.zaebal);
                        break;
                    case R.id.fifthButton:
                        mediaPlayer = MediaPlayer.create(this, R.raw.sanya);
                        break;
                    case R.id.sixthButton:
                        mediaPlayer = MediaPlayer.create(this, R.raw.iamdrunk);
                        break;
                    case R.id.seventhButton:
                        mediaPlayer = MediaPlayer.create(this, R.raw.youaredegrod);
                        break;
                    case R.id.eithButton:
                        mediaPlayer = MediaPlayer.create(this, R.raw.narodmudr);
                        break;
                    case R.id.ninthButton:
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
//                Toast.makeText(this, "MediaPlayer is null", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onStop() {
            super.onStop();
            stopPlayer();
        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("NewBoiler");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }
}