package com.example.dailyband.MusicAdd;

import static android.Manifest.permission;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.example.dailyband.R;
import com.example.dailyband.Setting.SettingActivity;
import com.example.dailyband.Utils.FirebaseMethods;
import com.example.dailyband.adapter.MusicTrackAdapter;
import com.google.android.material.textfield.TextInputLayout;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class AddMusic extends AppCompatActivity {
    public class MusicTrack {
        public Uri uri;
        public String title = "";
        public boolean isSpeaking = true;
    }

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    //private MediaRecorder mediaRecorder;
    private MediaRecorder audioRecorder;
    //private String outputFile;

    private ImageView plusbtn, playbtn2, uploadbtn;
    private FirebaseMethods mFirebaseMethods;
    private String postId;
    private TextView nextmenu;
    private EditText pathTextView;

    private Uri uri, audiouri;

    private boolean isPlaying = false;
    private FrameLayout addCategoryFrameLayout;

    int mAudioSource = MediaRecorder.AudioSource.MIC;
    int mSampleRate = 44100;
    int mChannelCount = AudioFormat.CHANNEL_IN_STEREO;
    int mAudioFormat = AudioFormat.ENCODING_PCM_16BIT;
    int mBufferSize = AudioTrack.getMinBufferSize(mSampleRate, mChannelCount, mAudioFormat);

    List<MusicTrack> tracks;

    private RecyclerView musicTrackView;
    private LinearLayoutManager llm;
    private RecyclerView.Adapter adapter;
    private ConstraintLayout detail_pickup_layout;
    private FrameLayout detail_instrument_frame;
    private List<ComplexName> parents;
    PianoFragment pianoFragment;
    CategoryAddMusic categoryAddMusic;
    DrumFragment drumFragment;
    private ImageButton homeBtn, setbtn;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addmusic);

        chkStoragePermission();
        mFirebaseMethods = new FirebaseMethods(AddMusic.this);

        //녹음 파일 경로 저장
//        String storagePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/record.3gp";
//        outputFile = storagePath;

        nextmenu = findViewById(R.id.nextmenu);
        plusbtn = findViewById(R.id.plusbtn);
        detail_pickup_layout = findViewById(R.id.detail_pickup_layout);
        detail_instrument_frame = findViewById(R.id.detail_instrument_frame);
        addCategoryFrameLayout = findViewById(R.id.add_category_framelayout);
        homeBtn = findViewById(R.id.homeBtn);
        setbtn = findViewById(R.id.setbtn);
        playbtn2 = findViewById(R.id.playbtn2);
        pianoFragment = new PianoFragment();
        drumFragment = new DrumFragment();

        //CategoryAddMusicFragment 추가
        getSupportFragmentManager().beginTransaction().replace(R.id.add_category_framelayout, new CategoryAddMusic()).commit();

        parents = new ArrayList<>();

        //리사이클러 뷰 설정
        musicTrackView = (RecyclerView) findViewById(R.id.tracklist);
        llm = new LinearLayoutManager(this);
        musicTrackView.setLayoutManager(llm);

//        paths = new ArrayList<>();
//        isSpeaking = new ArrayList<>();
        tracks = new ArrayList<>();

        adapter = new MusicTrackAdapter(tracks);
        musicTrackView.setAdapter(adapter);

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
        detail_pickup_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (detail_pickup_layout.getVisibility() == View.VISIBLE) {
                    detail_pickup_layout.setVisibility(View.GONE);
                }
            }
        });
        plusbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCategoryFrameLayout.setVisibility(View.VISIBLE);
            }
        });
        playbtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 쓰레드에서 AudioTrack으로 선택된 uri 재생
                for(int i=0;i<tracks.size();i++) {
                    if(tracks.get(i).isSpeaking == false) continue;
                    Runnable r = new AudioTrackRunnable(tracks.get(i).uri);
                    new Thread(r).start();
                }
            }
        });

//
//        uploadbtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // firebase에 선택된 uri 업로드
//                uploadToFirebase();
//            }
//        });

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
    private void addTrack(Uri uri, String title) {
        MusicTrack track = new MusicTrack();
        track.uri = uri;
        track.title = title;
        tracks.add(track);
        adapter.notifyDataSetChanged();
    }

    private void chkStoragePermission() {
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.TIRAMISU) {
            int permission = ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.READ_MEDIA_AUDIO);

            if (permission == PackageManager.PERMISSION_DENIED) {
                Log.d("테스트","권한 없음 : READ_MEDIA_IMAGES");
                requestPermissions(
                        new String[]{android.Manifest.permission.READ_MEDIA_AUDIO},
                        1000);
            }
        } else{
            int permission = ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int permission2 = ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE);
            if (permission == PackageManager.PERMISSION_DENIED || permission2 == PackageManager.PERMISSION_DENIED ) {
                Log.d("테스트","권한 없음 : WRITE_EXTERNAL_STORAGE || READ_EXTERNAL_STORAGE");
                requestPermissions(
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        1000);
            }
        }
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
//                uri = data.getData();
//                pathTextView.setText(uri.getPath().toString());

                Uri uri = data.getData();
                String path = uri.getPath();
                addTrack(uri, path);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void startRecording(){
        // 권한 체크
        int permission = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.RECORD_AUDIO);

        if (permission == PackageManager.PERMISSION_DENIED) {
            Log.d("테스트","권한 없음 : READ_MEDIA_IMAGES");
            requestPermissions(
                    new String[]{android.Manifest.permission.RECORD_AUDIO},
                    1000);
            return;
        }

        // 이미 녹음 중이면 거부
        if(audioRecorder != null) {
            startToast("이미 녹음중 입니다!");
            return;
        }

        // MediaStore로 파일 생성
        String fileName = "record";

        ContentValues values = new ContentValues(4);
        values.put(MediaStore.Audio.Media.TITLE, fileName);
        values.put(MediaStore.Audio.Media.DATE_ADDED, (int) (System.currentTimeMillis() / 1000));
        values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/mp3");
        values.put(MediaStore.Audio.Media.RELATIVE_PATH, "Music/Recordings/");

        audiouri = getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
        ParcelFileDescriptor file;
        try {
            file = getContentResolver().openFileDescriptor(audiouri, "w");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        // MediaRecorder 로 녹음 시작
        if(file == null) {
            Log.e("에러발생", "파일이 null 임");
            return ;
        }

        audioRecorder = new MediaRecorder();
        audioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        audioRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        audioRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        audioRecorder.setOutputFile(file.getFileDescriptor());
        audioRecorder.setAudioChannels(1);

        try {
            audioRecorder.prepare();
            audioRecorder.start();
            startToast("녹음 시작!");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("에러발생", "audioRecorder prepare 불가능 한 오류");
            return;
        }
    }

    //권한 확인하고 startRecording()으로
    private void chkPermission(){
        if (ContextCompat.checkSelfPermission(this, permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            // 녹음 권한이 허용되지 않은 경우 런타임 권한 요청
            ActivityCompat.requestPermissions(this, new String[]{permission.RECORD_AUDIO},
                    REQUEST_RECORD_AUDIO_PERMISSION);
        } else {
            startRecording();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_RECORD_AUDIO_PERMISSION){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //녹음 권한이 허용된 경우 녹음 시작 코드 실행
                chkPermission();
            }else{
                //녹음 권한이 거부된 경우 사용자에게 설명하기 혹은 다시 요청하기
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

        //여기 아무것도 녹음하지 않았을 때 안 함. 테스트니까..!
        //  : 업로드 할 파일이 없는 예외 처리

        uri = tracks.get(0).uri;

        postId = mFirebaseMethods.addSongToDatabase(title, parents);
        mFirebaseMethods.uploadNewStorage(title, uri, postId);
    }


    public class AudioTrackRunnable implements Runnable {
        //int position;
        //public AudioTrackRunnable(int position) {this.position = position;}

        private Uri uri;
        public AudioTrackRunnable(Uri uri) {
            this.uri = uri;
        }

        public void run() {
            try {
                // 데이터 주고받을 배열
                byte[] writeData = new byte[mBufferSize];

                // 데이터 스트림
                InputStream is = getContentResolver().openInputStream(uri);

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

                // isPlaying으로 정지
                isPlaying = true;
                at.play();
                while (isPlaying) {
                    try {
                        int ret = is.read(writeData, 0, mBufferSize);
                        if (ret <= 0) {
                            (AddMusic.this).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    isPlaying = false;
                                    //mBtPlay.setText("Play");
                                }
                            });

                            break;
                        }
                        at.write(writeData, 0, ret);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                at.stop();
                at.release();
                at = null;

                is.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
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
