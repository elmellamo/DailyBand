package com.example.dailyband.OcarinaTest;
import android.content.ContentValues;
import android.media.AudioFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.dailyband.MusicAdd.AddMusic;
import com.example.dailyband.R;
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

public class Ocarina4HoleFragment extends Fragment {
    private View view;
    private Button bt_record, stop_record;
    private WaveRecorder waveRecorder;
    private OnRecordingCompletedListener recordingCompletedListener;

    private String directory_name = "Daily Band";
    private File externalDir;
    private boolean isRecording = false;
    private boolean isPaused = false;
    private String filePath; // 녹음된 파일의 경로를 저장할 변수
    public void setOnRecordingCompletedListener(OnRecordingCompletedListener listener) {
        this.recordingCompletedListener = listener;
    }

    public Ocarina4HoleFragment() {
        filePath = Environment.getExternalStorageDirectory().getPath() + "/audioFile.wav";
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container != null) {
            container.removeAllViews();
        }
        view = inflater.inflate(R.layout.ocarina_new, container, false);
        bt_record = view.findViewById(R.id.bt_record);
        stop_record = view.findViewById(R.id.stop_record);


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

    @Override

    public void onStart() {
        super.onStart();
        ImageButton b1 = (ImageButton) getActivity().findViewById(R.id.button1);
        ImageButton b2 = (ImageButton) getActivity().findViewById(R.id.button2);
        ImageButton b3 = (ImageButton) getActivity().findViewById(R.id.button3);
        ImageButton b4 = (ImageButton) getActivity().findViewById(R.id.button4);

        b1.setOnTouchListener(AddMusic.getTouchListener());
        b2.setOnTouchListener(AddMusic.getTouchListener());
        b3.setOnTouchListener(AddMusic.getTouchListener());
        b4.setOnTouchListener(AddMusic.getTouchListener());
    }

    private void startRecording() {
        waveRecorder = new WaveRecorder(filePath);
        externalDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), directory_name);
        if (!externalDir.exists()) {
            externalDir.mkdirs();
        }
        filePath = new File(externalDir, "audioFile.wav").getAbsolutePath();
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

            Toast.makeText(requireContext(), "녹음을 멈추고 저장합니다.", Toast.LENGTH_LONG).show();

            long currentTimeMillis = System.currentTimeMillis();
            // 시간을 원하는 포맷으로 변환합니다.
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String formattedTime = sdf.format(new Date(currentTimeMillis));
            String fileName = formattedTime + ".wav";

            //MediaStore를 이용해서 녹음 파일 미디어 데이터베이스에 추가
            ContentValues values = new ContentValues();
            values.put(MediaStore.Audio.Media.DISPLAY_NAME, fileName);
            values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/wav");
            values.put(MediaStore.Audio.Media.RELATIVE_PATH, Environment.DIRECTORY_MUSIC+"/"+directory_name);
            values.put(MediaStore.Audio.Media.IS_PENDING, 1);

            Uri contentUri = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
            Uri itemUri = requireContext().getContentResolver().insert(contentUri, values);

            if (itemUri != null) {
                try {
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
}