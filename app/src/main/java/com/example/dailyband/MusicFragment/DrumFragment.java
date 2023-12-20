package com.example.dailyband.MusicFragment;

import android.content.ContentValues;
import android.media.AudioFormat;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.dailyband.MusicAdd.AddMusic;
import com.example.dailyband.R;
import com.example.dailyband.Setting.NewSettingActivity;
import com.example.dailyband.Utils.OnGrayTouchListener;
import com.example.dailyband.Utils.OnRecordingCompletedListener;
import com.github.squti.androidwaverecorder.WaveConfig;
import com.github.squti.androidwaverecorder.WaveRecorder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DrumFragment extends Fragment implements View.OnClickListener, OnGrayTouchListener {
    private View view;
    private ConstraintLayout basslayout, crash_cymbol_cardview, mid_tom_cardview, ride_cymbal_cardview,
            smare_cardview, high_tom_cardview, low_tom_cardview, open_hihat_layout, close_hihat_layout;
    private int bass, crash_cymbal, low_tom, close_hi_hat, open_hi_hat, mid_tom, snare, ride_cymbal,
    high_tom;
    private Button bt_record, stop_record;
    private WaveRecorder waveRecorder;
    private OnRecordingCompletedListener recordingCompletedListener;

    private String directory_name = "Daily Band";
    private File externalDir;
    private boolean isRecording = false;
    private boolean isPaused = false;
    private String filePath; // 녹음된 파일의 경로를 저장할 변수

    private SoundPool soundPool;

    public void setOnRecordingCompletedListener(OnRecordingCompletedListener listener) {
        this.recordingCompletedListener = listener;
    }

    public DrumFragment() {
        filePath = Environment.getExternalStorageDirectory().getPath() + "/audioFile.wav";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.drum_main, container, false);
        basslayout = view.findViewById(R.id.basslayout);
        crash_cymbol_cardview = view.findViewById(R.id.crash_cymbol_cardview);
        mid_tom_cardview = view.findViewById(R.id.mid_tom_cardview);
        ride_cymbal_cardview = view.findViewById(R.id.ride_cymbal_cardview);
        smare_cardview = view.findViewById(R.id.smare_cardview);
        high_tom_cardview = view.findViewById(R.id.high_tom_cardview);
        low_tom_cardview = view.findViewById(R.id.low_tom_cardview);
        open_hihat_layout = view.findViewById(R.id.open_hihat_layout);
        close_hihat_layout = view.findViewById(R.id.close_hihat_layout);


        bt_record = view.findViewById(R.id.bt_record);
        stop_record = view.findViewById(R.id.stop_record);
        waveRecorder = new WaveRecorder(filePath);
        externalDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), directory_name);
        if (!externalDir.exists()) {
            externalDir.mkdirs();
        }
        filePath = new File(externalDir, "audioFile.wav").getAbsolutePath();


        intilizeAllDrum();
        soundPool = new SoundPool.Builder().setMaxStreams(6).build();

        bass = soundPool.load(requireContext(), R.raw.bass, 1);
        crash_cymbal = soundPool.load(requireContext(), R.raw.crash_cymbal, 1);
        low_tom = soundPool.load(requireContext(), R.raw.low_tom, 1);
        close_hi_hat = soundPool.load(requireContext(), R.raw.close_hi_hat, 1);
        open_hi_hat = soundPool.load(requireContext(), R.raw.open_hi_hat, 1);
        mid_tom = soundPool.load(requireContext(), R.raw.mid_tom, 1);
        snare = soundPool.load(requireContext(), R.raw.snare, 1);
        ride_cymbal = soundPool.load(requireContext(), R.raw.ride_cymbal, 1);
        high_tom = soundPool.load(requireContext(), R.raw.high_tom, 1);

        bt_record.setOnClickListener(v -> {
            if(!isRecording){
                startRecording();
            }
        });

        stop_record.setOnClickListener(v -> {
            stopRecording();
        });

        return view;
    }
    private void startRecording() {
        String fileName = "audioFile.wav";
        File internalStorageDir = requireContext().getFilesDir();
        filePath = new File(internalStorageDir, fileName).getAbsolutePath();

        waveRecorder = new WaveRecorder(filePath);
        waveRecorder.setWaveConfig(new WaveConfig(44100, AudioFormat.CHANNEL_IN_STEREO,  AudioFormat.ENCODING_PCM_16BIT));
        waveRecorder.setNoiseSuppressorActive(true);
        waveRecorder.startRecording();
        isRecording = true;
        isPaused = false;

        Toast.makeText(requireContext(), "녹음을 시작합니다.", Toast.LENGTH_LONG).show();
    }

    // 녹음 중지 상태로 UI 업데이트
    private void stopRecording() {
        if (isRecording) {
            waveRecorder.stopRecording();
            isRecording = false;
            isPaused = false;

            AddMusic addMusic = (AddMusic) getActivity();
            addMusic.updateIsFragmentOpen(false);

            Toast.makeText(requireContext(), "녹음을 멈추고 저장합니다.", Toast.LENGTH_LONG).show();

            long currentTimeMillis = System.currentTimeMillis();
            // 시간을 원하는 포맷으로 변환합니다.
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String formattedTime = sdf.format(new Date(currentTimeMillis));
            String fileName = formattedTime + ".wav";

            //MediaStore를 이용해서 녹음 팡리 미디어 데이터베이스에 추가
            ContentValues values = new ContentValues();
            values.put(MediaStore.Audio.Media.DISPLAY_NAME, fileName);
            values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/wav");
            values.put(MediaStore.Audio.Media.RELATIVE_PATH, Environment.DIRECTORY_MUSIC+"/"+directory_name);
            values.put(MediaStore.Audio.Media.IS_PENDING, 1);

            Uri contentUri = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
            Uri itemUri = requireContext().getContentResolver().insert(contentUri, values);

            if (itemUri != null) {
                try {
                    // 파일 복사 또는 이동 작업 수행 (이 코드에서는 파일을 복사함)
                    try (ParcelFileDescriptor descriptor = requireContext().getContentResolver().openFileDescriptor(itemUri, "w")) {
                        if (descriptor != null) {
                            FileInputStream inputStream = new FileInputStream(filePath);
                            FileOutputStream outputStream = new FileOutputStream(descriptor.getFileDescriptor());
                            byte[] buffer = new byte[1024];
                            int bytesRead;
                            while ((bytesRead = inputStream.read(buffer)) > 0) {
                                outputStream.write(buffer, 0, bytesRead);
                            }
                            inputStream.close();
                            outputStream.close();
                        }
                    }

                    // MediaStore에 파일 정보 업데이트
                    ContentValues value = new ContentValues();
                    value.put(MediaStore.Audio.Media.IS_PENDING, 0); // 파일이 완전히 쓰여짐을 표시
                    requireContext().getContentResolver().update(itemUri, value, null, null);

                    //Uri recordingUri = Uri.parse("file://" + filePath);
                    Uri recordingUri = itemUri;
                    if (recordingCompletedListener != null) {
                        recordingCompletedListener.onRecordingCompleted(recordingUri);
                        if(getActivity() instanceof AddMusic){
                            AddMusic addmusic = (AddMusic) getActivity();
                            addmusic.hideDetailPickupLayout();
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Toast.makeText(requireContext(), "아직 녹음을 시작하지 않았습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    void intilizeAllDrum(){
        crash_cymbol_cardview.setOnClickListener(this);
        mid_tom_cardview.setOnClickListener(this);
        ride_cymbal_cardview.setOnClickListener(this);
        smare_cardview.setOnClickListener(this);
        high_tom_cardview.setOnClickListener(this);
        low_tom_cardview.setOnClickListener(this);
        open_hihat_layout.setOnClickListener(this);
        close_hihat_layout.setOnClickListener(this);
        basslayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id==R.id.basslayout) {
            soundPool.play(bass, 1, 1, 0, 0, 1);
        }else if(id==R.id.mid_tom_cardview){
            soundPool.play(mid_tom, 1, 1, 0, 0, 1);
        }else if(id==R.id.ride_cymbal_cardview){
            soundPool.play(ride_cymbal, 1, 1, 0, 0, 1);
        }else if(id==R.id.smare_cardview){
            soundPool.play(snare, 1, 1, 0, 0, 1);
        }else if(id==R.id.high_tom_cardview){
            soundPool.play(high_tom, 1, 1, 0, 0, 1);
        }else if(id==R.id.low_tom_cardview){
            soundPool.play(low_tom, 1, 1, 0, 0, 1);
        }else if(id==R.id.open_hihat_layout){
            soundPool.play(open_hi_hat, 1, 1, 0, 0, 1);
        }else if(id==R.id.close_hihat_layout){
            soundPool.play(close_hi_hat, 1, 1, 0, 0, 1);
        }else if(id==R.id.crash_cymbol_cardview){
            soundPool.play(crash_cymbal, 1, 1, 0, 0, 1);
        }
    }

    @Override
    public void onGrayClicked() {
        stopRecording();
    }
}
