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
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.dailyband.MusicAdd.AddMusic;
import com.example.dailyband.R;
import com.example.dailyband.Utils.OnRecordingCompletedListener;
import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.github.angads25.toggle.model.ToggleableView;
import com.github.angads25.toggle.widget.LabeledSwitch;
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
    private HorizontalScrollView scrollView;
    private OnRecordingCompletedListener recordingCompletedListener;
    private ConstraintLayout bottomlayout;
    private String directory_name = "Daily Band";
    private File externalDir;
    private boolean isRecording = false;
    private boolean isPaused = false;
    private LabeledSwitch instrumnet_switch;
    private String filePath; // 녹음된 파일의 경로를 저장할 변수

    private Button buttonA4, buttonA4black, buttonB4, buttonC5, buttonC5black, buttonD5,
            buttonD5black, buttonE5, buttonF5, buttonF5black, buttonG5, buttonG5black, buttonA5, buttonA5black,
            buttonB5, buttonC6,buttonC6black, buttonD6, buttonD6black, buttonE6;
    private TextView ta4,tb4,tc5, td5, te5, tf5, tg5, ta5, tb5, tc6, td6, te6;

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
        instrumnet_switch = view.findViewById(R.id.instrumnet_switch);
        bottomlayout = view.findViewById(R.id.bottomlayout);
        scrollView = view.findViewById(R.id.scrollView);

        bt_record.setOnClickListener(v -> {
            if(!isRecording){
                startRecording();
            }
        });

        stop_record.setOnClickListener(v -> {
            stopRecording();
        });

        instrumnet_switch.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                if(isOn){
                    bottomlayout.setVisibility(View.GONE);
                    scrollView.setVisibility(View.VISIBLE);

                }else{
                    scrollView.setVisibility(View.GONE);
                    bottomlayout.setVisibility(View.VISIBLE);
                }
            }
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

        buttonA4 = (Button) getActivity().findViewById(R.id.oca_p13);
        buttonB4 = (Button) getActivity().findViewById(R.id.oca_p14);
        buttonC5 = (Button) getActivity().findViewById(R.id.oca_p15);
        buttonD5 = (Button) getActivity().findViewById(R.id.oca_p16);
        buttonE5 = (Button) getActivity().findViewById(R.id.oca_p17);
        buttonF5 = (Button) getActivity().findViewById(R.id.oca_p18);
        buttonG5 = (Button) getActivity().findViewById(R.id.oca_p19);
        buttonA5 = (Button) getActivity().findViewById(R.id.oca_p20);
        buttonB5 = (Button) getActivity().findViewById(R.id.oca_p21);
        buttonC6 = (Button) getActivity().findViewById(R.id.oca_p22);
        buttonD6 = (Button) getActivity().findViewById(R.id.oca_p23);
        buttonE6 = (Button) getActivity().findViewById(R.id.oca_p24);

        buttonA4.setOnTouchListener(AddMusic.getOcaPianoTouchListener());
        buttonB4.setOnTouchListener(AddMusic.getOcaPianoTouchListener());
        buttonC5.setOnTouchListener(AddMusic.getOcaPianoTouchListener());
        buttonD5.setOnTouchListener(AddMusic.getOcaPianoTouchListener());
        buttonE5.setOnTouchListener(AddMusic.getOcaPianoTouchListener());
        buttonF5.setOnTouchListener(AddMusic.getOcaPianoTouchListener());
        buttonG5.setOnTouchListener(AddMusic.getOcaPianoTouchListener());
        buttonA5.setOnTouchListener(AddMusic.getOcaPianoTouchListener());
        buttonB5.setOnTouchListener(AddMusic.getOcaPianoTouchListener());
        buttonC6.setOnTouchListener(AddMusic.getOcaPianoTouchListener());
        buttonD6.setOnTouchListener(AddMusic.getOcaPianoTouchListener());
        buttonE6.setOnTouchListener(AddMusic.getOcaPianoTouchListener());

        buttonA4black = (Button) getActivity().findViewById(R.id.oca_b10);
        buttonC5black = (Button) getActivity().findViewById(R.id.oca_b11);
        buttonD5black = (Button) getActivity().findViewById(R.id.oca_b12);
        buttonF5black = (Button) getActivity().findViewById(R.id.oca_b13);
        buttonG5black = (Button) getActivity().findViewById(R.id.oca_b14);
        buttonA5black = (Button) getActivity().findViewById(R.id.oca_b15);
        buttonC6black = (Button) getActivity().findViewById(R.id.oca_b16);
        buttonD6black = (Button) getActivity().findViewById(R.id.oca_b17);


        buttonA4black.setOnTouchListener(AddMusic.getOcaPianoTouchListener());
        buttonC5black.setOnTouchListener(AddMusic.getOcaPianoTouchListener());
        buttonD5black.setOnTouchListener(AddMusic.getOcaPianoTouchListener());
        buttonF5black.setOnTouchListener(AddMusic.getOcaPianoTouchListener());
        buttonG5black.setOnTouchListener(AddMusic.getOcaPianoTouchListener());
        buttonA5black.setOnTouchListener(AddMusic.getOcaPianoTouchListener());
        buttonC6black.setOnTouchListener(AddMusic.getOcaPianoTouchListener());
        buttonD6black.setOnTouchListener(AddMusic.getOcaPianoTouchListener());


        ta4 = (TextView) getActivity().findViewById(R.id.oca_ta4);
        tb4 = (TextView) getActivity().findViewById(R.id.oca_tb4);
        tc5 = (TextView) getActivity().findViewById(R.id.oca_tc5);
        td5 = (TextView) getActivity().findViewById(R.id.oca_td5);
        te5 = (TextView) getActivity().findViewById(R.id.oca_te5);
        tf5 = (TextView) getActivity().findViewById(R.id.oca_tf5);
        tg5 = (TextView) getActivity().findViewById(R.id.oca_tg5);
        ta5 = (TextView) getActivity().findViewById(R.id.oca_ta5);
        tb5 = (TextView) getActivity().findViewById(R.id.oca_tb5);
        tc6 = (TextView) getActivity().findViewById(R.id.oca_tc6);
        td6 = (TextView) getActivity().findViewById(R.id.oca_td6);
        te6 = (TextView) getActivity().findViewById(R.id.oca_te6);

        ta4.setOnTouchListener(AddMusic.getOcaPianoTouchListener());
        tb4.setOnTouchListener(AddMusic.getOcaPianoTouchListener());
        tc5.setOnTouchListener(AddMusic.getOcaPianoTouchListener());
        td5.setOnTouchListener(AddMusic.getOcaPianoTouchListener());
        te5.setOnTouchListener(AddMusic.getOcaPianoTouchListener());
        tf5.setOnTouchListener(AddMusic.getOcaPianoTouchListener());
        tg5.setOnTouchListener(AddMusic.getOcaPianoTouchListener());
        ta5.setOnTouchListener(AddMusic.getOcaPianoTouchListener());
        tb5.setOnTouchListener(AddMusic.getOcaPianoTouchListener());
        tc6.setOnTouchListener(AddMusic.getOcaPianoTouchListener());
        td6.setOnTouchListener(AddMusic.getOcaPianoTouchListener());
        te6.setOnTouchListener(AddMusic.getOcaPianoTouchListener());

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

            AddMusic addMusic = (AddMusic) getActivity();
            addMusic.updateIsFragmentOpen(false);

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