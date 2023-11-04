package com.example.dailyband.Utils;

import android.content.ContentValues;
import android.content.res.AssetFileDescriptor;
import android.media.AudioFormat;
import android.media.AudioTrack;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

import android.content.ContentValues;
import android.content.res.AssetFileDescriptor;
import android.media.AudioFormat;
import android.media.AudioTrack;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

public class MergeWav {

    private AppCompatActivity activity;

    private static final int mSampleRate = 44100;
    private static final int mChannelCount = AudioFormat.CHANNEL_IN_STEREO;
    private static final int mAudioFormat = AudioFormat.ENCODING_PCM_16BIT;
    private static final int mBufferSize = AudioTrack.getMinBufferSize(mSampleRate, mChannelCount, mAudioFormat);
    private static final int bitsPerSample = 16;




    public MergeWav(AppCompatActivity activity) {
        this.activity = activity;
    }




    public void mergeWavToWav(Uri result, List<Uri> uris) {
        try {
            ContentValues values = new ContentValues();

            values.put(MediaStore.Audio.Media.TITLE, "temp");
            values.put(MediaStore.Audio.Media.DISPLAY_NAME, "temp");
            values.put(MediaStore.Audio.Media.DATE_ADDED, (int) (System.currentTimeMillis() / 1000));
            values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/x-wav");
            values.put(MediaStore.Audio.Media.RELATIVE_PATH, "Music/Daily Band/process files/");

            Uri temp = activity.getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);

            mergeWavToPCM(temp, uris);

            FileInputStream is = (FileInputStream) activity.getContentResolver().openInputStream(temp);
            FileOutputStream os = (FileOutputStream) activity.getContentResolver().openOutputStream(result);

            wavClass wc = new wavClass("", activity);
            wc.createWavFile(is, os);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }





    private int mergeWavToPCM(Uri output, List<Uri> uris) throws IOException {
        byte[] readData = new byte[mBufferSize];
        byte[] writeData = new byte[mBufferSize];

        short[] readShorts = new short[mBufferSize/2];
        short[] writeShorts = new short[mBufferSize/2];

        int[] readInt = new int[mBufferSize/2];

        OutputStream outputStream = activity.getContentResolver().openOutputStream(output);

        // 합칠 파일들 열기
        int trackSize = uris.size();
        InputStream[] is = new InputStream[trackSize];
        for(int i=0;i<trackSize;i++) {
            is[i] = activity.getContentResolver().openInputStream(uris.get(i));
            is[i].skip(44);
        }

        int fileSize = 0;

        // 루프 시작
        // boolean endOfFile = false;

        boolean[] endOfFile = new boolean[trackSize];
        for(int i=0;i<trackSize;i++)
            endOfFile[i] = false;

        while (true) {
            try {
                for(int j=0;j<mBufferSize/2;j++) {
                    readInt[j] = 0;
                }

                int ret=0;
                for(int i=0;i<trackSize;i++) {
                    if(endOfFile[i]) continue;

                    int tmp = is[i].read(readData, 0, mBufferSize);
                    if(tmp <= 0) endOfFile[i] = true;
                    ret = Math.max(ret, tmp);

                    // 읽어온 바이트들을 little endian으로 short로 변환
                    ByteBuffer.wrap(readData).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(readShorts);
                    for(int j=0;j<mBufferSize/2;j++) {
                        // 값을 다 더해줌
                        readInt[j] += readShorts[j];
                    }
                }
                if(ret <= 0) break;

                // short 범위 내의 값으로 바꾼 후에 다시 little endian으로 변환
                for(int j=0;j<mBufferSize/2;j++) {
                    if(readInt[j] > 32767) readInt[j] = 32767;
                    if(readInt[j] < -32768) readInt[j] = -32768;

                    writeShorts[j] = (short)readInt[j];
                }
                ByteBuffer.wrap(writeData).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(writeShorts);
                outputStream.write(writeData, 0, ret);
                fileSize += ret;

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        outputStream.close();

        return fileSize;
    }


}
