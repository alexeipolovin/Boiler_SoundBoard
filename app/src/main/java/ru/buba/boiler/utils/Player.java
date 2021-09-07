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

package ru.buba.boiler.utils;

import android.content.Context;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.view.View;

import java.io.IOException;

public class Player {

    public void pause() {
        mediaPlayer.pause();
    }

    public void start() {
        mediaPlayer.start();
    }

    public enum PlayType {
        LOCAL,
        STREAM
    }

    private int lastId;

    private MediaPlayer mediaPlayer;

    public Player(PlayType playType, String dataSource) {
        if (playType == PlayType.STREAM) {
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(dataSource);
                mediaPlayer.prepare();
                mediaPlayer.setOnCompletionListener(mp -> mediaPlayer.release());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void playAsync(String dataSource) {
        try {
            if(mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
            }
            mediaPlayer.setDataSource(dataSource);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(mp -> mediaPlayer.start());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void playLocal(int id, Context context) {
        boolean play = true;

        if (mediaPlayer != null)
            if (mediaPlayer.isPlaying()) {
                if (lastId == id) {
                    mediaPlayer.pause();
                    play = false;
                } else {
                    lastId = id;
                    stop();
                }
            } else {
                if (lastId != id) {
                    lastId = id;
                    stop();
                }
            }

        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, id);
        }


        mediaPlayer.setOnCompletionListener(mp -> stop());
        if (play)
            mediaPlayer.start();
    }

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void pause(View v) {
        if (mediaPlayer != null)
            mediaPlayer.pause();
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }
}
