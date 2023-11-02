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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.example.dailyband.ShowMusic.PickMusic;
import com.example.dailyband.Utils.FirebaseMethods;
import com.example.dailyband.Utils.OnRecordingCompletedListener;
import com.example.dailyband.adapter.MusicTrackAdapter;
import com.google.android.material.textfield.TextInputLayout;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class AddMusic extends AppCompatActivity {
    public class MusicTrack {
        public Uri uri;
        public String title = "";
        public boolean isSpeaking = true;
    }
    private static final int REQUEST_PERMISSION_CODE = 1000;
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

    int mSampleRate = 44100;
    int mChannelCount = AudioFormat.CHANNEL_IN_STEREO;
    int mAudioFormat = AudioFormat.ENCODING_PCM_16BIT;
    int mBufferSize = AudioTrack.getMinBufferSize(mSampleRate, mChannelCount, mAudioFormat);

    List<MusicTrack> tracks;

    private RecyclerView musicTrackView;
    private LinearLayoutManager llm;
    private RecyclerView.Adapter adapter;
    private ConstraintLayout detail_pickup_layout, gray_screen;
    private FrameLayout detail_instrument_frame;
    private List<ComplexName> parents;
    PianoFragment pianoFragment;
    CategoryAddMusic categoryAddMusic;
    DrumFragment drumFragment;
    RecordingMain recordingMain;
    private ImageButton homeBtn, setbtn;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addmusic);


        //chkAllPermissions();
        //chkStoragePermission();
        mFirebaseMethods = new FirebaseMethods(AddMusic.this);
        myPermissions();

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
        recordingMain = new RecordingMain();
        gray_screen = findViewById(R.id.gray_screen);

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

    private void addTrack(Uri uri, String title) {
        MusicTrack track = new MusicTrack();
        track.uri = uri;
        track.title = title;
        tracks.add(track);
        adapter.notifyDataSetChanged();
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

        uri = tracks.get(0).uri;
        if(uri==null){
            startToast("음악을 만들어주세요.");
            return;
        }

        Intent intent = new Intent(this, AddCaption.class);
        intent.putExtra("title", title);
        intent.putExtra("uri", uri.toString());
        startActivity(intent);
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