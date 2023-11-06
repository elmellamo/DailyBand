package com.example.dailyband.MusicAdd;

import static android.Manifest.permission;

import android.Manifest;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dailyband.Home.HomeMain;
import com.example.dailyband.Models.ComplexName;
import com.example.dailyband.MusicFragment.CategoryAddMusic;
import com.example.dailyband.MusicFragment.DrumFragment;
import com.example.dailyband.MusicFragment.PianoFragment;
import com.example.dailyband.MusicFragment.RecordingMain;
import com.example.dailyband.R;
import com.example.dailyband.Setting.SettingActivity;
import com.example.dailyband.Utils.FirebaseMethods;
import com.example.dailyband.Utils.MergeWav;
import com.example.dailyband.Utils.OnRecordingCompletedListener;
import com.example.dailyband.adapter.MusicTrackAdapter;
import com.google.android.material.textfield.TextInputLayout;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class AddMusic extends AppCompatActivity {
    public static class MusicTrack {
        public Uri uri;
        public String title = "";
        public int length; // 초단위

        public boolean isSpeaking = true; // mute / unmute
        public boolean isStarted = false;
        public boolean isEnded = false;
    }

    private static final int REQUEST_PERMISSION_CODE = 1000;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    private FirebaseMethods mFirebaseMethods;

    private Uri uri;

    List<MusicTrack> tracks;

    Thread audioThread;
    private boolean isPlaying = false;
    private int playingLocation = 0;
    private int max_len = 0;

    private ImageView plusbtn, playbtn, stopbtn;
    private TextView nextmenu, music_length;
    private SeekBar seekBar;

    private RecyclerView musicTrackView;
    private LinearLayoutManager llm;
    private RecyclerView.Adapter adapter;
    private ConstraintLayout detail_pickup_layout, gray_screen;
    private FrameLayout detail_instrument_frame;
    private List<ComplexName> parents;

    private FrameLayout addCategoryFrameLayout;
    PianoFragment pianoFragment;
    CategoryAddMusic categoryAddMusic;
    DrumFragment drumFragment;
    RecordingMain recordingMain;
    private ImageButton homeBtn, setbtn;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addmusic);

        mFirebaseMethods = new FirebaseMethods(AddMusic.this);
        myPermissions();

        nextmenu = findViewById(R.id.nextmenu);
        plusbtn = findViewById(R.id.plusbtn);
        detail_pickup_layout = findViewById(R.id.detail_pickup_layout);
        detail_instrument_frame = findViewById(R.id.detail_instrument_frame);
        addCategoryFrameLayout = findViewById(R.id.add_category_framelayout);
        homeBtn = findViewById(R.id.homeBtn);
        setbtn = findViewById(R.id.setbtn);
        playbtn = findViewById(R.id.playbtn2);
        stopbtn = findViewById(R.id.stopbtn);
        music_length = findViewById(R.id.music_length);
        seekBar = findViewById(R.id.seekBar);

        pianoFragment = new PianoFragment();
        drumFragment = new DrumFragment();
        recordingMain = new RecordingMain();
        gray_screen = findViewById(R.id.gray_screen);

        getSupportFragmentManager().beginTransaction().replace(R.id.add_category_framelayout, new CategoryAddMusic()).commit();

        parents = new ArrayList<>();

        //리사이클러 뷰 설정
        musicTrackView = (RecyclerView) findViewById(R.id.tracklist);
        llm = new LinearLayoutManager(this);
        musicTrackView.setLayoutManager(llm);

        tracks = new ArrayList<>();

        adapter = new MusicTrackAdapter(tracks);
        musicTrackView.setAdapter(adapter);

        pianoFragment.setOnRecordingCompletedListener(new OnRecordingCompletedListener() {
            @Override
            public void onRecordingCompleted(Uri recordingUri) {
                // 현재 시간을 얻어옵니다.
                long currentTimeMillis = System.currentTimeMillis();
                // 시간을 원하는 포맷으로 변환합니다.
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                String formattedTime = sdf.format(new Date(currentTimeMillis));

                addTrack(recordingUri, formattedTime);
            }
        });

        drumFragment.setOnRecordingCompletedListener(new OnRecordingCompletedListener() {
            @Override
            public void onRecordingCompleted(Uri recordingUri) {
                // 현재 시간을 얻어옵니다.
                long currentTimeMillis = System.currentTimeMillis();
                // 시간을 원하는 포맷으로 변환합니다.
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                String formattedTime = sdf.format(new Date(currentTimeMillis));

                addTrack(recordingUri, formattedTime);
            }
        });

        recordingMain.setOnRecordingCompletedListener(new OnRecordingCompletedListener() {
            @Override
            public void onRecordingCompleted(Uri recordingUri) {
                // 현재 시간을 얻어옵니다.
                long currentTimeMillis = System.currentTimeMillis();
                // 시간을 원하는 포맷으로 변환합니다.
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                String formattedTime = sdf.format(new Date(currentTimeMillis));

                addTrack(recordingUri, formattedTime);
            }
        });

        gray_screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (detail_pickup_layout.getVisibility() == View.VISIBLE) {
                    detail_pickup_layout.setVisibility(View.GONE);
                }
            }
        });
        nextmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadToFirebase();
                //myStartActivity(AddCaption.class);
            }
        });
        setbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myStartActivity(SettingActivity.class);
            }
        });
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myStartActivity(HomeMain.class);
            }
        });
        plusbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCategoryFrameLayout.setVisibility(View.VISIBLE);
            }
        });
        playbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPlaying) {
                    // 재생중이었으면 경우 이미지를 Play로 변경
                    playbtn.setImageResource(R.drawable.playbtn);

                    if(audioThread != null)
                        audioThread.interrupt();
                    audioThread = null;
                    isPlaying = false;
                    return;
                }

                // 쓰레드에서 AudioTrack으로 uri 재생
                isPlaying = true;
                Runnable r = new AudioTrackRunnable();
                audioThread = new Thread(r);
                audioThread.start();

                // Play 버튼 이미지를 Pause로 변경
                playbtn.setImageResource(R.drawable.pause);

            }
        });
        stopbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(audioThread != null)
                    audioThread.interrupt();
                audioThread = null;
                isPlaying = false;
                playingLocation = 0;

                updateMusicPosition();
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                startNewAudioThread(seekBar.getProgress());
            }
        });

    }
    public void showUpRecording(){
        detail_pickup_layout.setVisibility(View.VISIBLE);
        getSupportFragmentManager().beginTransaction().replace(R.id.detail_instrument_frame, recordingMain).commit();
    }
    //피아노 보이게 하기
    public void showUpPiano(){
        detail_pickup_layout.setVisibility(View.VISIBLE);
        getSupportFragmentManager().beginTransaction().replace(R.id.detail_instrument_frame, pianoFragment).commit();
    }
    //드럼 보이게 하기
    public void showUpDrum(){
        detail_pickup_layout.setVisibility(View.VISIBLE);
        getSupportFragmentManager().beginTransaction().replace(R.id.detail_instrument_frame, drumFragment).commit();
    }
    //addCategoryFrameLayout을 숨기는 메서드
    public void hideAddCategoryFrameLayout(){
        addCategoryFrameLayout.setVisibility(View.GONE);
    }
    public void hideDetailPickupLayout(){
        detail_pickup_layout.setVisibility(View.GONE);
    }

    private void addTrack(Uri uri, String title) {
        MusicTrack track = new MusicTrack();
        track.uri = uri;
        track.title = title;

        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(getApplicationContext(), uri);
        String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        int millSecond = Integer.parseInt(durationStr);
        track.length = millSecond;
        Log.d("asdf",durationStr + ", milsec:"+millSecond);

        tracks.add(track);
        adapter.notifyDataSetChanged();
        updateMaxMusicLength();
    }

    private void updateMaxMusicLength() {
        max_len = 0;
        for(int i=0;i<tracks.size();i++) {
            Log.d("asdf", i+" len : "+tracks.get(i).length);
            max_len = Math.max(max_len, tracks.get(i).length);
        }

        Log.d("asdf", "max leen :"+max_len);

        updateMusicPosition();
//        long minutes = (max_len / 1000)  / 60;
//        int seconds = (int)((max_len / 1000) % 60);
    }

    public void updateMusicPosition() {
        int max_minutes = (max_len / 1000)  / 60;
        int max_seconds = (int)((max_len / 1000) % 60);

        int cur_len = playingLocation / 44100 / 4;
        int cur_minutes = cur_len / 60;
        int cur_seconds = cur_len % 60;

        String musicLength = String.format("%02d:%02d / %02d:%02d", cur_minutes, cur_seconds, max_minutes, max_seconds);
        music_length.setText(musicLength);
    }

    public void getPathFromStorage() {
        Intent intent_upload = new Intent();
        intent_upload.setType("audio/x-wav"); // wav파일만
        // intent_upload.setType("audio/*");
        intent_upload.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent_upload, 1);
    }

    // 폴더에서 노래 가져온 결과
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        if(requestCode == 1){
            if(resultCode == RESULT_OK){
                // audio 위치
                Uri uri = data.getData();
                String path = uri.getPath();
                addTrack(uri, path);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            boolean allPermissionsGranted = true;
            int j=0;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;

                    //권한을 하나라도 허용하지 않는다면 앱 종료
                    Toast.makeText(getApplicationContext(), permissions[j] + " 권한을 설정하세요", Toast.LENGTH_LONG).show();
                    myPermissions();
                    break;
                }
                j++;
            }

            if (allPermissionsGranted) {
                // 모든 권한이 허용된 경우 원하는 작업 수행
                // 예: 녹음 시작 또는 다른 작업 수행
            } else {
                // 사용자에게 알림을 표시하거나 적절한 조치를 취하세요.
                //Toast.makeText(getApplicationContext(), "모든 권한을 허용해야 사용할 수 있습니다.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void myPermissions(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            List<String> permissionsTORequest = new ArrayList<>();
            String[] permissions = new String[]{
                    permission.READ_MEDIA_AUDIO,
                    permission.READ_MEDIA_VIDEO,
                    permission.READ_MEDIA_IMAGES,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.MODIFY_AUDIO_SETTINGS
            };
            for (String permission : permissions){
                if (ContextCompat.checkSelfPermission(this,permission) != PackageManager.PERMISSION_GRANTED){
                    permissionsTORequest.add(permission);
                    //startToast("여기 이상해요."+permission);
                }
            }

            if (permissionsTORequest.isEmpty()){
                // All permissions are already granted
                //Toast.makeText(this, "All permissions are already granted", Toast.LENGTH_SHORT).show();


            } else {
                String[] permissionsArray = permissionsTORequest.toArray(new String[0]);
                boolean shouldShowRationale = false;

                for (String permission : permissionsArray){
                    if (shouldShowRequestPermissionRationale(permission)){
                        shouldShowRationale = true;
                        break;
                    }
                }

                if (shouldShowRationale){
                    new AlertDialog.Builder(this)
                            .setMessage("Please allow all permissions")
                            .setCancelable(false)
                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //requestPermissionLauncher.launch(permissionsArray);
                                    ActivityCompat.requestPermissions(AddMusic.this, permissionsArray, REQUEST_PERMISSION_CODE);
                                }
                            })

                            .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            })
                            .show();

                } else {
                    //requestPermissionLauncher.launch(permissionsArray);

                    ActivityCompat.requestPermissions(AddMusic.this, permissionsArray, REQUEST_PERMISSION_CODE);
                }


            }


        } else{
            String[] allPermissions = {
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.MODIFY_AUDIO_SETTINGS
            };

            List<String> permissionsTORequest = new ArrayList<>();
            for (String permission : allPermissions){
                if (ContextCompat.checkSelfPermission(this,permission) != PackageManager.PERMISSION_GRANTED){
                    permissionsTORequest.add(permission);
                }
            }

            if (permissionsTORequest.isEmpty()){
                // All permissions are already granted
                //Toast.makeText(this, "All permissions are already granted", Toast.LENGTH_SHORT).show();


            } else {
                String[] permissionsArray = permissionsTORequest.toArray(new String[0]);
                boolean shouldShowRationale = false;

                for (String permission : permissionsArray){
                    if (shouldShowRequestPermissionRationale(permission)){
                        shouldShowRationale = true;
                        break;
                    }
                }

                if (shouldShowRationale){
                    new AlertDialog.Builder(this)
                            .setMessage("Please allow all permissions")
                            .setCancelable(false)
                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //requestPermissionLauncher.launch(permissionsArray);

                                    ActivityCompat.requestPermissions(AddMusic.this, permissionsArray, REQUEST_PERMISSION_CODE);
                                }
                            })

                            .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            })
                            .show();

                } else {
                    //requestPermissionLauncher.launch(permissionsArray);
                    ActivityCompat.requestPermissions(AddMusic.this, permissionsArray, REQUEST_PERMISSION_CODE);
                }
            }
        }
    }


    private void uploadToFirebase(){
        TextInputLayout textInputLayout = findViewById(R.id.songname_edit_layout);
        final String title = textInputLayout.getEditText().getText().toString();

        if(title.length()<=0) {
            startToast("곡명을 정해주세요.");
            return ;
        }

        if(tracks.size()<=0){
            startToast("음악을 만들어주세요.");
            return;
        }

        uri = mergeTracks();

        Intent intent = new Intent(this, AddCaption.class);
        intent.putExtra("title", title);
        intent.putExtra("uri", uri.toString());
        startActivity(intent);
    }

    private Uri mergeTracks() {
        ContentValues values = new ContentValues();
        long currentTimeMillis = System.currentTimeMillis();
        // 시간을 원하는 포맷으로 변환합니다.
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String formattedTime = sdf.format(new Date(currentTimeMillis));


        values.put(MediaStore.Audio.Media.TITLE, formattedTime+".wav");
        values.put(MediaStore.Audio.Media.DISPLAY_NAME, formattedTime+".wav");
        values.put(MediaStore.Audio.Media.DATE_ADDED, (int) (System.currentTimeMillis() / 1000));
        values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/x-wav");
        values.put(MediaStore.Audio.Media.RELATIVE_PATH, "Music/Daily Band/Result/");

        Uri result_uri = getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);

        ArrayList<Uri> uris = new ArrayList<>();
        for(int i=0;i<tracks.size();i++) {
            uris.add(tracks.get(i).uri);
        }

        MergeWav mw = new MergeWav(this);
        mw.mergeWavToWav(result_uri, uris);

        return result_uri;
    }

    private void startNewAudioThread(int startPoint) {
        if(audioThread != null)
            audioThread.interrupt();
        audioThread = null;

        // 쓰레드에서 AudioTrack으로 uri 재생
        isPlaying = true;
        playingLocation = startPoint;
        Runnable r = new AudioTrackRunnable();
        audioThread = new Thread(r);
        audioThread.start();

        // Play 버튼 이미지를 Pause로 변경
        playbtn.setImageResource(R.drawable.pause);
    }

    public class AudioTrackRunnable implements Runnable {

        int mSampleRate = 44100;
        int mChannelCount = AudioFormat.CHANNEL_IN_STEREO;
        int mAudioFormat = AudioFormat.ENCODING_PCM_16BIT;
        int mBufferSize = AudioTrack.getMinBufferSize(mSampleRate, mChannelCount, mAudioFormat);

        public AudioTrackRunnable() { }

        public void run() {
            try {
                // 데이터 스트림
                // InputStream is = getContentResolver().openInputStream(uri);
                int trackSize = tracks.size();
                InputStream[] is = new InputStream[trackSize];
                for(int i=0;i<trackSize;i++) {
                    is[i] = getContentResolver().openInputStream(tracks.get(i).uri);
                    is[i].skip(44 + playingLocation);

                    tracks.get(i).isStarted = false;
                    tracks.get(i).isEnded = false;
                }

                // AudioTrack 생성
                AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                String rate = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE);

                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build();

                AudioFormat audioFormat = new AudioFormat.Builder()
                        .setSampleRate(Integer.parseInt(rate))
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_STEREO)
                        .build();

                AudioTrack at = new AudioTrack(audioAttributes,
                        audioFormat,
                        mBufferSize * 2,
                        AudioTrack.MODE_STREAM,
                        0);


                byte[] readData = new byte[mBufferSize];
                byte[] writeData = new byte[mBufferSize];

                short[] readShorts = new short[mBufferSize/2];
                short[] writeShorts = new short[mBufferSize/2];

                int[] readInt = new int[mBufferSize/2];

                at.play();
                while (isPlaying) {
                    try {
                        for(int j=0;j<mBufferSize/2;j++) {
                            readInt[j] = 0;
                        }

                        int ret=0;
                        for(int i=0;i<trackSize;i++) {
                            if(tracks.get(i).isEnded) continue;

                            int tmp = is[i].read(readData, 0, mBufferSize);
                            if(tmp <= 0) tracks.get(i).isEnded = true;
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
                        at.write(writeData, 0, ret);
                        playingLocation += ret;

                        runOnUiThread(new Runnable(){
                            @Override
                            public void run() {
                                updateMusicPosition();
                            }
                        });

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                at.stop();
                at.release();
                at = null;

                playbtn.setImageResource(R.drawable.playbtn);

                // 전부 끝까지 재생했으면 재생위치 0으로 초기화
                boolean isAllPlayed = true;
                for(int i=0;i<trackSize;i++) {
                    if(tracks.get(i).isEnded == false) {
                        isAllPlayed = false;
                        break;
                    }
                }
                Log.d("asdf", ""+playingLocation);
                if(isAllPlayed) playingLocation = 0;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private void myStartActivity(Class c){
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    private void startToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}