package com.example.dailyband.MusicFragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.media.AudioFormat;
import android.media.MediaRecorder;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

public class PianoFragment extends Fragment implements View.OnClickListener {
    // 여기에 필요한 모든 변수 및 상수 선언
    private View view;
    boolean mStartRecording = true;
    public int recordingno;

    private SoundPool soundPool;
    Button recordbutton;


    private int c3, c3black, d3, d3black, e3, f3, f3black, g3, g3black, a3, a3black, b3;
    private int c4, c4black, d4, d4black, e4, f4, f4black, g4, g4black, a4, a4black, b4;
    private int c5, c5black, d5, d5black, e5, f5, f5black, g5, g5black, a5, a5black, b5;
    private int c6, c6black, d6, d6black, e6, f6, f6black, g6, g6black, a6, a6black, b6;
    private int c7, c7black, d7, d7black, e7, f7, f7black, g7, g7black, a7, a7black, b7;


    private HorizontalScrollView scrollView;

    private Button buttonC3, buttonC3black, buttonD3, buttonD3black, buttonE3, buttonF3,
            buttonF3black, buttonG3, buttonG3black, buttonA3, buttonA3black, buttonB3,
            buttonC4, buttonC4black, buttonD4, buttonD4black, buttonE4, buttonF4, buttonF4black,
            buttonG4, buttonG4black, buttonA4, buttonA4black, buttonB4, buttonC5, buttonC5black, buttonD5,
            buttonD5black, buttonE5, buttonF5, buttonF5black, buttonG5, buttonG5black, buttonA5, buttonA5black,
            buttonB5, buttonC6,buttonC6black, buttonD6, buttonD6black, buttonE6, buttonF6, buttonF6black,
            buttonG6, buttonG6black, buttonA6, buttonA6black, buttonB6, buttonC7, buttonC7black, buttonD7,
            buttonD7black,  buttonE7,  buttonF7, buttonF7black, buttonG7, buttonG7black, buttonA7, buttonA7black, buttonB7;

    private TextView tc3;
    private TextView td3;
    private TextView te3;
    private TextView tf3;
    private TextView tg3;
    private TextView ta3;
    private TextView tb3;

    private TextView tc4;
    private TextView td4;
    private TextView te4;
    private TextView tf4;
    private TextView tg4;
    private TextView ta4;
    private TextView tb4;

    private TextView tc5;
    private TextView td5;
    private TextView te5;
    private TextView tf5;
    private TextView tg5;
    private TextView ta5;
    private TextView tb5;

    private TextView tc6;
    private TextView td6;
    private TextView te6;
    private TextView tf6;
    private TextView tg6;
    private TextView ta6;
    private TextView tb6;

    private TextView tc7;
    private TextView td7;
    private TextView te7;
    private TextView tf7;
    private TextView tg7;
    private TextView ta7;
    private TextView tb7;


    private Button bt_record, stop_record;
    private WaveRecorder waveRecorder;
    private OnRecordingCompletedListener recordingCompletedListener;

    private String directory_name = "dailyband";
    private File externalDir;
    private boolean isRecording = false;
    private boolean isPaused = false;
    private String filePath; // 녹음된 파일의 경로를 저장할 변수

    public void setOnRecordingCompletedListener(OnRecordingCompletedListener listener) {
        this.recordingCompletedListener = listener;
    }
    public PianoFragment() {
        filePath = Environment.getExternalStorageDirectory().getPath() + "/audioFile.wav";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.piano_main, container, false);

        // Intilize the scrool view
        scrollView = view.findViewById(R.id.scrollView);

        // Intilize the navigations button


        // Method for all PianoKey Buttons
        intilizeAllPianoKeys();

        // Method for all PianoKey Lables TextViews
        intilizeTextViewLablesOnPianoKeys();

        // code for the soundpool

        soundPool = new SoundPool.Builder()
                .setMaxStreams(6)
                .build();


        c3 = soundPool.load(requireContext(), R.raw.c3, 1);
        c3black = soundPool.load(requireContext(), R.raw.c3black, 1);
        d3 = soundPool.load(requireContext(), R.raw.d3, 1);
        d3black = soundPool.load(requireContext(), R.raw.d3black, 1);
        e3 = soundPool.load(requireContext(), R.raw.e3, 1);
        f3 = soundPool.load(requireContext(), R.raw.f3, 1);
        f3black = soundPool.load(requireContext(), R.raw.f3black, 1);
        g3 = soundPool.load(requireContext(), R.raw.g3, 1);
        g3black = soundPool.load(requireContext(), R.raw.g3black, 1);
        a3 = soundPool.load(requireContext(), R.raw.a3, 1);
        a3black = soundPool.load(requireContext(), R.raw.a3black, 1);
        b3 = soundPool.load(requireContext(), R.raw.b3, 1);


        c4 = soundPool.load(requireContext(), R.raw.c4, 1);
        c4black = soundPool.load(requireContext(), R.raw.c4black, 1);
        d4 = soundPool.load(requireContext(), R.raw.d4, 1);
        d4black = soundPool.load(requireContext(), R.raw.d4black, 1);
        e4 = soundPool.load(requireContext(), R.raw.e4, 1);
        f4 = soundPool.load(requireContext(), R.raw.f4, 1);
        f4black = soundPool.load(requireContext(), R.raw.f4black, 1);
        g4 = soundPool.load(requireContext(), R.raw.c4, 1);
        g4black = soundPool.load(requireContext(), R.raw.g4black, 1);
        a4 = soundPool.load(requireContext(), R.raw.a4, 1);
        a4black = soundPool.load(requireContext(), R.raw.a4black, 1);
        b4 = soundPool.load(requireContext(), R.raw.b4, 1);

        c5 = soundPool.load(requireContext(), R.raw.c5, 1);
        c5black = soundPool.load(requireContext(), R.raw.c5black, 1);
        d5 = soundPool.load(requireContext(), R.raw.d5, 1);
        d5black = soundPool.load(requireContext(), R.raw.d5black, 1);
        e5 = soundPool.load(requireContext(), R.raw.e5, 1);
        f5 = soundPool.load(requireContext(), R.raw.f5, 1);
        f5black = soundPool.load(requireContext(), R.raw.f5black, 1);
        g5 = soundPool.load(requireContext(), R.raw.c5, 1);
        g5black = soundPool.load(requireContext(), R.raw.g5black, 1);
        a5 = soundPool.load(requireContext(), R.raw.a5, 1);
        a5black = soundPool.load(requireContext(), R.raw.a5black, 1);
        b5 = soundPool.load(requireContext(), R.raw.b5, 1);

        c6 = soundPool.load(requireContext(), R.raw.c6, 1);
        c6black = soundPool.load(requireContext(), R.raw.c6black, 1);
        d6 = soundPool.load(requireContext(), R.raw.d6, 1);
        d6black = soundPool.load(requireContext(), R.raw.d6black, 1);
        e6 = soundPool.load(requireContext(), R.raw.e6, 1);
        f6 = soundPool.load(requireContext(), R.raw.f6, 1);
        f6black = soundPool.load(requireContext(), R.raw.f6black, 1);
        g6 = soundPool.load(requireContext(), R.raw.g6, 1);
        g6black = soundPool.load(requireContext(), R.raw.g6black, 1);
        a6 = soundPool.load(requireContext(), R.raw.a6, 1);
        a6black = soundPool.load(requireContext(), R.raw.a6black, 1);
        b6 = soundPool.load(requireContext(), R.raw.b6, 1);

        c7 = soundPool.load(requireContext(), R.raw.c7, 1);
        c7black = soundPool.load(requireContext(), R.raw.c7black, 1);
        d7 = soundPool.load(requireContext(), R.raw.d7, 1);
        d7black = soundPool.load(requireContext(), R.raw.d7black, 1);
        e7 = soundPool.load(requireContext(), R.raw.e7, 1);
        f7 = soundPool.load(requireContext(), R.raw.f7, 1);
        f3black = soundPool.load(requireContext(), R.raw.f3black, 1);
        g7 = soundPool.load(requireContext(), R.raw.g7, 1);
        g7black = soundPool.load(requireContext(), R.raw.g7black, 1);
        a7 = soundPool.load(requireContext(), R.raw.a7, 1);
        a7black = soundPool.load(requireContext(), R.raw.a7black, 1);
        b7 = soundPool.load(requireContext(), R.raw.b7, 1);


        bt_record = view.findViewById(R.id.bt_record);
        stop_record = view.findViewById(R.id.stop_record);
        waveRecorder = new WaveRecorder(filePath);
        externalDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), directory_name);
        if (!externalDir.exists()) {
            externalDir.mkdirs();
        }
        filePath = new File(externalDir, "audioFile.wav").getAbsolutePath();

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

    void intilizeTextViewLablesOnPianoKeys() {
        tc3 = (TextView) view.findViewById(R.id.tc3);
        tc3.setOnClickListener(this);
        td3 = (TextView) view.findViewById(R.id.td3);
        td3.setOnClickListener(this);
        te3 = (TextView) view.findViewById(R.id.te3);
        te3.setOnClickListener(this);
        tf3 = (TextView) view.findViewById(R.id.tf3);
        tf3.setOnClickListener(this);
        tg3 = (TextView) view.findViewById(R.id.tg3);
        tg3.setOnClickListener(this);
        ta3 = (TextView) view.findViewById(R.id.ta3);
        ta3.setOnClickListener(this);
        tb3 = (TextView) view.findViewById(R.id.tb3);
        tb3.setOnClickListener(this);

        tc4 = (TextView) view.findViewById(R.id.tc4);
        tc4.setOnClickListener(this);
        td4 = (TextView) view.findViewById(R.id.td4);
        td4.setOnClickListener(this);
        te4 = (TextView) view.findViewById(R.id.te4);
        te4.setOnClickListener(this);
        tf4 = (TextView) view.findViewById(R.id.tf4);
        tf4.setOnClickListener(this);
        tg4 = (TextView) view.findViewById(R.id.tg4);
        tg4.setOnClickListener(this);
        ta4 = (TextView) view.findViewById(R.id.ta4);
        ta4.setOnClickListener(this);
        tb4 = (TextView) view.findViewById(R.id.tb4);
        tb4.setOnClickListener(this);

        tc5 = (TextView) view.findViewById(R.id.tc5);
        tc5.setOnClickListener(this);
        td5 = (TextView) view.findViewById(R.id.td5);
        td5.setOnClickListener(this);
        te5 = (TextView) view.findViewById(R.id.te5);
        te5.setOnClickListener(this);
        tf5 = (TextView) view.findViewById(R.id.tf5);
        tf5.setOnClickListener(this);
        tg5 = (TextView) view.findViewById(R.id.tg5);
        tg5.setOnClickListener(this);
        ta5 = (TextView) view.findViewById(R.id.ta5);
        ta5.setOnClickListener(this);
        tb5 = (TextView) view.findViewById(R.id.tb5);
        tb5.setOnClickListener(this);


        tc6 = (TextView) view.findViewById(R.id.tc6);
        tc6.setOnClickListener(this);
        td6 = (TextView) view.findViewById(R.id.td6);
        td6.setOnClickListener(this);
        te6 = (TextView) view.findViewById(R.id.te6);
        te6.setOnClickListener(this);
        tf6 = (TextView) view.findViewById(R.id.tf6);
        tf6.setOnClickListener(this);
        tg6 = (TextView) view.findViewById(R.id.tg6);
        tg6.setOnClickListener(this);
        ta6 = (TextView) view.findViewById(R.id.ta6);
        ta6.setOnClickListener(this);
        tb6 = (TextView) view.findViewById(R.id.tb6);
        tb6.setOnClickListener(this);


        tc7 = (TextView) view.findViewById(R.id.tc7);
        tc7.setOnClickListener(this);
        td7 = (TextView) view.findViewById(R.id.td7);
        td7.setOnClickListener(this);
        te7 = (TextView) view.findViewById(R.id.te7);
        te7.setOnClickListener(this);
        tf7 = (TextView) view.findViewById(R.id.tf7);
        tf7.setOnClickListener(this);
        tg7 = (TextView) view.findViewById(R.id.tg7);
        tg7.setOnClickListener(this);
        ta7 = (TextView) view.findViewById(R.id.ta7);
        ta7.setOnClickListener(this);
        tb7 = (TextView) view.findViewById(R.id.tb7);
        tb7.setOnClickListener(this);
    }

    void intilizeAllPianoKeys() {
        buttonC3 = (Button) view.findViewById(R.id.p1);
        buttonC3.setOnClickListener(this);
        buttonC3black = (Button) view.findViewById(R.id.b1);
        buttonC3black.setOnClickListener(this);
        buttonD3 = (Button) view.findViewById(R.id.p2);
        buttonD3.setOnClickListener(this);
        buttonD3black = (Button) view.findViewById(R.id.b2);
        buttonD3black.setOnClickListener(this);
        buttonE3 = (Button) view.findViewById(R.id.p3);
        buttonE3.setOnClickListener(this);
        buttonF3 = (Button) view.findViewById(R.id.p4);
        buttonF3.setOnClickListener(this);
        buttonF3black = (Button) view.findViewById(R.id.b3);
        buttonF3black.setOnClickListener(this);
        buttonG3 = (Button) view.findViewById(R.id.p5);
        buttonG3.setOnClickListener(this);
        buttonG3black = (Button) view.findViewById(R.id.b4);
        buttonG3black.setOnClickListener(this);
        buttonA3 = (Button) view.findViewById(R.id.p6);
        buttonA3.setOnClickListener(this);
        buttonA3black = (Button) view.findViewById(R.id.b5);
        buttonA3black.setOnClickListener(this);
        buttonB3 = (Button) view.findViewById(R.id.p7);
        buttonB3.setOnClickListener(this);


        buttonC4 = (Button) view.findViewById(R.id.p8);
        buttonC4.setOnClickListener(this);
        buttonC4black = (Button) view.findViewById(R.id.b6);
        buttonC4black.setOnClickListener(this);
        buttonD4 = (Button) view.findViewById(R.id.p9);
        buttonD4.setOnClickListener(this);
        buttonD4black = (Button) view.findViewById(R.id.b7);
        buttonD4black.setOnClickListener(this);
        buttonE4 = (Button) view.findViewById(R.id.p10);
        buttonE4.setOnClickListener(this);
        buttonF4 = (Button) view.findViewById(R.id.p11);
        buttonF4.setOnClickListener(this);
        buttonF4black = (Button) view.findViewById(R.id.b8);
        buttonF4black.setOnClickListener(this);
        buttonG4 = (Button) view.findViewById(R.id.p12);
        buttonG4.setOnClickListener(this);
        buttonG4black = (Button) view.findViewById(R.id.b9);
        buttonG4black.setOnClickListener(this);
        buttonA4 = (Button) view.findViewById(R.id.p13);
        buttonA4.setOnClickListener(this);
        buttonA4black = (Button) view.findViewById(R.id.b10);
        buttonA4black.setOnClickListener(this);
        buttonB4 = (Button) view.findViewById(R.id.p14);
        buttonB4.setOnClickListener(this);


        buttonC5 = (Button) view.findViewById(R.id.p15);
        buttonC5.setOnClickListener(this);
        buttonC5black = (Button) view.findViewById(R.id.b11);
        buttonC5black.setOnClickListener(this);
        buttonD5 = (Button) view.findViewById(R.id.p16);
        buttonD5.setOnClickListener(this);
        buttonD5black = (Button) view.findViewById(R.id.b12);
        buttonD5black.setOnClickListener(this);
        buttonE5 = (Button) view.findViewById(R.id.p17);
        buttonE5.setOnClickListener(this);
        buttonF5 = (Button) view.findViewById(R.id.p18);
        buttonF5.setOnClickListener(this);
        buttonF5black = (Button) view.findViewById(R.id.b13);
        buttonF5black.setOnClickListener(this);
        buttonG5 = (Button) view.findViewById(R.id.p19);
        buttonG5.setOnClickListener(this);
        buttonG5black = (Button) view.findViewById(R.id.b14);
        buttonG5black.setOnClickListener(this);
        buttonA5 = (Button) view.findViewById(R.id.p20);
        buttonA5.setOnClickListener(this);
        buttonA5black = (Button) view.findViewById(R.id.b15);
        buttonA5black.setOnClickListener(this);
        buttonB5 = (Button) view.findViewById(R.id.p21);
        buttonB5.setOnClickListener(this);


        buttonC6 = (Button) view.findViewById(R.id.p22);
        buttonC6.setOnClickListener(this);
        buttonC6black = (Button) view.findViewById(R.id.b16);
        buttonC6black.setOnClickListener(this);
        buttonD6 = (Button) view.findViewById(R.id.p23);
        buttonD6.setOnClickListener(this);
        buttonD6black = (Button) view.findViewById(R.id.b17);
        buttonD6black.setOnClickListener(this);
        buttonE6 = (Button) view.findViewById(R.id.p24);
        buttonE6.setOnClickListener(this);
        buttonF6 = (Button) view.findViewById(R.id.p25);
        buttonF6.setOnClickListener(this);
        buttonF6black = (Button) view.findViewById(R.id.b18);
        buttonF6black.setOnClickListener(this);
        buttonG6 = (Button) view.findViewById(R.id.p26);
        buttonG6.setOnClickListener(this);
        buttonG6black = (Button) view.findViewById(R.id.b19);
        buttonG6black.setOnClickListener(this);
        buttonA6 = (Button) view.findViewById(R.id.p27);
        buttonA6.setOnClickListener(this);
        buttonA6black = (Button) view.findViewById(R.id.b20);
        buttonA6black.setOnClickListener(this);
        buttonB6 = (Button) view.findViewById(R.id.p28);
        buttonB6.setOnClickListener(this);


        buttonC7 = (Button) view.findViewById(R.id.p29);
        buttonC7.setOnClickListener(this);
        buttonC7black = (Button) view.findViewById(R.id.b21);
        buttonC7black.setOnClickListener(this);
        buttonD7 = (Button) view.findViewById(R.id.p30);
        buttonD7.setOnClickListener(this);
        buttonD7black = (Button) view.findViewById(R.id.b22);
        buttonD7black.setOnClickListener(this);
        buttonE7 = (Button) view.findViewById(R.id.p31);
        buttonE7.setOnClickListener(this);
        buttonF7 = (Button) view.findViewById(R.id.p32);
        buttonF7.setOnClickListener(this);
        buttonF7black = (Button) view.findViewById(R.id.b23);
        buttonF7black.setOnClickListener(this);
        buttonG7 = (Button) view.findViewById(R.id.p33);
        buttonG7.setOnClickListener(this);
        buttonG7black = (Button) view.findViewById(R.id.b24);
        buttonG7black.setOnClickListener(this);
        buttonA7 = (Button) view.findViewById(R.id.p34);
        buttonA7.setOnClickListener(this);
        buttonA7black = (Button) view.findViewById(R.id.b25);
        buttonA7black.setOnClickListener(this);
        buttonB7 = (Button) view.findViewById(R.id.p35);
        buttonB7.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tc3 || id == R.id.p1) {
            soundPool.play(c3, 1, 1, 0, 0, 1);
        } else if (id == R.id.b1) {
            soundPool.play(c3black, 1, 1, 0, 0, 1);
        } else if (id == R.id.td3 || id == R.id.p2) {
            soundPool.play(d3, 1, 1, 0, 0, 1);
        } else if (id == R.id.b2) {
            soundPool.play(d3black, 1, 1, 0, 0, 1);
        } else if (id == R.id.te3 || id == R.id.p3) {
            soundPool.play(e3, 1, 1, 0, 0, 1);
        } else if (id == R.id.tf3 || id == R.id.p4) {
            soundPool.play(f3, 1, 1, 0, 0, 1);
        } else if (id == R.id.b3) {
            soundPool.play(f3black, 1, 1, 0, 0, 1);
        } else if (id == R.id.tg3 || id == R.id.p5) {
            soundPool.play(g3, 1, 1, 0, 0, 1);
        } else if (id == R.id.b4) {
            soundPool.play(g3black, 1, 1, 0, 0, 1);
        } else if (id == R.id.ta3 || id == R.id.p6) {
            soundPool.play(a3, 1, 1, 0, 0, 1);
        } else if (id == R.id.b5) {
            soundPool.play(a3black, 1, 1, 0, 0, 1);
        } else if (id == R.id.tb3 || id == R.id.p7) {
            soundPool.play(b3, 1, 1, 0, 0, 1);
        } else if (id == R.id.tc4 || id == R.id.p8) {
            soundPool.play(c4, 1, 1, 0, 0, 1);
        } else if (id == R.id.b6) {
            soundPool.play(c4black, 1, 1, 0, 0, 1);
        } else if (id == R.id.td4 || id == R.id.p9) {
            soundPool.play(d4, 1, 1, 0, 0, 1);
        } else if (id == R.id.b7) {
            soundPool.play(d4black, 1, 1, 0, 0, 1);
        } else if (id == R.id.te4 || id == R.id.p10) {
            soundPool.play(e4, 1, 1, 0, 0, 1);
        } else if (id == R.id.tf4 || id == R.id.p11) {
            soundPool.play(f4, 1, 1, 0, 0, 1);
        } else if (id == R.id.b8) {
            soundPool.play(f4black, 1, 1, 0, 0, 1);
        } else if (id == R.id.tg4 || id == R.id.p12) {
            soundPool.play(g4, 1, 1, 0, 0, 1);
        } else if (id == R.id.b9) {
            soundPool.play(g4black, 1, 1, 0, 0, 1);
        } else if (id == R.id.ta4 || id == R.id.p13) {
            soundPool.play(a4, 1, 1, 0, 0, 1);
        } else if (id == R.id.b10) {
            soundPool.play(a4black, 1, 1, 0, 0, 1);
        } else if (id == R.id.tb4 || id == R.id.p14) {
            soundPool.play(b4, 1, 1, 0, 0, 1);
        } else if (id == R.id.tc5 || id == R.id.p15) {
            soundPool.play(c5, 1, 1, 0, 0, 1);
        } else if (id == R.id.b11) {
            soundPool.play(c5black, 1, 1, 0, 0, 1);
        } else if (id == R.id.td5 || id == R.id.p16) {
            soundPool.play(d5, 1, 1, 0, 0, 1);
        } else if (id == R.id.b12) {
            soundPool.play(d5black, 1, 1, 0, 0, 1);
        } else if (id == R.id.te5 || id == R.id.p17) {
            soundPool.play(e5, 1, 1, 0, 0, 1);
        } else if (id == R.id.tf5 || id == R.id.p18) {
            soundPool.play(f5, 1, 1, 0, 0, 1);
        } else if (id == R.id.b13) {
            soundPool.play(f5black, 1, 1, 0, 0, 1);
        } else if (id == R.id.tg5 || id == R.id.p19) {
            soundPool.play(g5, 1, 1, 0, 0, 1);
        } else if (id == R.id.b14) {
            soundPool.play(g5black, 1, 1, 0, 0, 1);
        } else if (id == R.id.ta5 || id == R.id.p20) {
            soundPool.play(a5, 1, 1, 0, 0, 1);
        } else if (id == R.id.b15) {
            soundPool.play(a5black, 1, 1, 0, 0, 1);
        } else if (id == R.id.tb5 || id == R.id.p21) {
            soundPool.play(b5, 1, 1, 0, 0, 1);
        } else if (id == R.id.tc6 || id == R.id.p22) {
            soundPool.play(c6, 1, 1, 0, 0, 1);
        } else if (id == R.id.b16) {
            soundPool.play(c6black, 1, 1, 0, 0, 1);
        } else if (id == R.id.td6 || id == R.id.p23) {
            soundPool.play(d6, 1, 1, 0, 0, 1);
        } else if (id == R.id.b17) {
            soundPool.play(d6black, 1, 1, 0, 0, 1);
        } else if (id == R.id.te6 || id == R.id.p24) {
            soundPool.play(e6, 1, 1, 0, 0, 1);
        } else if (id == R.id.tf6 || id == R.id.p25) {
            soundPool.play(f6, 1, 1, 0, 0, 1);
        } else if (id == R.id.b18) {
            soundPool.play(f6black, 1, 1, 0, 0, 1);
        } else if (id == R.id.tg6 || id == R.id.p26) {
            soundPool.play(g6, 1, 1, 0, 0, 1);
        } else if (id == R.id.b19) {
            soundPool.play(g6black, 1, 1, 0, 0, 1);
        } else if (id == R.id.ta6 || id == R.id.p27) {
            soundPool.play(a6, 1, 1, 0, 0, 1);
        } else if (id == R.id.b20) {
            soundPool.play(a6black, 1, 1, 0, 0, 1);
        } else if (id == R.id.tb6 || id == R.id.p28) {
            soundPool.play(b6, 1, 1, 0, 0, 1);
        } else if (id == R.id.tc7 || id == R.id.p29) {
            soundPool.play(c7, 1, 1, 0, 0, 1);
        } else if (id == R.id.b21) {
            soundPool.play(c7black, 1, 1, 0, 0, 1);
        } else if (id == R.id.td7 || id == R.id.p30) {
            soundPool.play(d7, 1, 1, 0, 0, 1);
        } else if (id == R.id.b22) {
            soundPool.play(d7black, 1, 1, 0, 0, 1);
        } else if (id == R.id.te7 || id == R.id.p31) {
            soundPool.play(e7, 1, 1, 0, 0, 1);
        } else if (id == R.id.tf7 || id == R.id.p32) {
            soundPool.play(f7, 1, 1, 0, 0, 1);
        } else if (id == R.id.b23) {
            soundPool.play(f7black, 1, 1, 0, 0, 1);
        } else if (id == R.id.tg7 || id == R.id.p33) {
            soundPool.play(g7, 1, 1, 0, 0, 1);
        } else if (id == R.id.b24) {
            soundPool.play(g7black, 1, 1, 0, 0, 1);
        } else if (id == R.id.ta7 || id == R.id.p34) {
            soundPool.play(a7, 1, 1, 0, 0, 1);
        } else if (id == R.id.b25) {
            soundPool.play(a7black, 1, 1, 0, 0, 1);
        } else if (id == R.id.tb7 || id == R.id.p35) {
            soundPool.play(b7, 1, 1, 0, 0, 1);
        }
    }

    // 녹음 시작 상태로 UI 업데이트
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

        Toast.makeText(requireContext(), "녹음을 시작합니다!", Toast.LENGTH_LONG).show();
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

                    Uri recordingUri = Uri.parse("file://" + filePath);
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

