package com.example.dailyband;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;

public class MusicService extends Service {
    private MediaPlayer mediaPlayer;
    private AudioManager audioManager;
    private int startTimeMillis = 6650;
    private int endTimeMillis = 20750;
    private boolean isAppInBackground = false;

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = MediaPlayer.create(this, R.raw.start_music);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!mediaPlayer.isPlaying()) {
            int result = audioManager.requestAudioFocus(
                    afChangeListener,
                    AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN
            );

            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                mediaPlayer.seekTo(startTimeMillis);
                mediaPlayer.start();

                if (endTimeMillis > 0) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            stopMusic();
                        }
                    }, endTimeMillis - startTimeMillis);
                }
            }
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void stopMusic() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            //mediaPlayer.seekTo(startTimeMillis);
        }
    }

    private AudioManager.OnAudioFocusChangeListener afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_LOSS:
                    if (!isAppInBackground && mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                    }
                    break;
            }
        }
    };

    @Override
    public void onDestroy() {
        stopMusic(); // 서비스가 종료될 때 음악을 멈춤
        super.onDestroy();
    }
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        isAppInBackground = true;
        stopMusic();
        stopSelf();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level == TRIM_MEMORY_UI_HIDDEN) {
            isAppInBackground = true;
            stopMusic();
            stopSelf();
        }
    }
}