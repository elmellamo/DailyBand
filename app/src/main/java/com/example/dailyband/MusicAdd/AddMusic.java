package com.example.dailyband.MusicAdd;

import static android.Manifest.permission;

import android.Manifest;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
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

import com.example.dailyband.Collection.CollectionActivity;
import com.example.dailyband.Home.HomeMain;
import com.example.dailyband.Love.LoveActivity;
import com.example.dailyband.Models.ComplexName;
import com.example.dailyband.MusicFragment.CategoryAddMusic;
import com.example.dailyband.MusicFragment.DrumFragment;
import com.example.dailyband.MusicFragment.PianoFragment;
import com.example.dailyband.MusicFragment.PopularFragment;
import com.example.dailyband.MusicFragment.RecordingMain;
import com.example.dailyband.R;
import com.example.dailyband.Setting.NewSettingActivity;
import com.example.dailyband.Utils.FirebaseMethods;
import com.example.dailyband.Utils.MergeWav;
import com.example.dailyband.Utils.OnCollaborationClickListener;
import com.example.dailyband.Utils.OnRecordingCompletedListener;
import com.example.dailyband.adapter.MusicTrackAdapter;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularfillableloaders.CircularFillableLoaders;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class AddMusic extends AppCompatActivity implements OnCollaborationClickListener {
    public static class MusicTrack {
        public Uri uri;
        public String title = "";
        public int length; // 초단위

        public String original_postid = null;

        public boolean isSpeaking = true; // mute / unmute
        public boolean isStarted = false;
        public boolean isEnded = false;
    }

    private boolean doubleBackToExitPressedOnce = false;
    int mSampleRate = 44100;
    int mChannelCount = AudioFormat.CHANNEL_IN_STEREO;
    int mAudioFormat = AudioFormat.ENCODING_PCM_16BIT;
    int mBufferSize = AudioTrack.getMinBufferSize(mSampleRate, mChannelCount, mAudioFormat);

    private static final int REQUEST_PERMISSION_CODE = 1000;

    private FirebaseMethods mFirebaseMethods;

    private Uri uri;
    List<MusicTrack> tracks;
    Thread audioThread;
    private boolean isPlaying = false;
    private long playingLocation = 0;
    private long max_len = 0;

    private ImageView plusbtn, playbtn, stopbtn, backcontext;
    private TextView nextmenu, music_length;
    private SeekBar seekBar;

    private RecyclerView musicTrackView;
    private LinearLayoutManager llm;
    private RecyclerView.Adapter adapter;
    private ConstraintLayout detail_pickup_layout, gray_screen;
    private FrameLayout detail_instrument_frame;
    private List<ComplexName> parents;
    public ArrayList<String> parentPostId;

    private FrameLayout addCategoryFrameLayout;
    PianoFragment pianoFragment;
    CategoryAddMusic categoryAddMusic;
    DrumFragment drumFragment;
    RecordingMain recordingMain;
    PopularFragment popularFragment;
    private ImageButton homeBtn, setbtn, myInfobtn, librarybtn, addbtn;
    private String parent_Id;
    private CircularFillableLoaders circularFillableLoaders;
    private ConstraintLayout circularlayout;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addmusic);

        myPermissions();

        mFirebaseMethods = new FirebaseMethods(AddMusic.this);

        nextmenu = findViewById(R.id.nextmenu);
        plusbtn = findViewById(R.id.plusbtn);
        detail_pickup_layout = findViewById(R.id.detail_pickup_layout);
        detail_instrument_frame = findViewById(R.id.detail_instrument_frame);
        addCategoryFrameLayout = findViewById(R.id.add_category_framelayout);
        homeBtn = findViewById(R.id.homeBtn);
        myInfobtn = findViewById(R.id.myInfobtn);
        librarybtn = findViewById(R.id.librarybtn);
        setbtn = findViewById(R.id.setbtn);
        addbtn = findViewById(R.id.addbtn);
        playbtn = findViewById(R.id.playbtn2);
        stopbtn = findViewById(R.id.stopbtn);
        backcontext = findViewById(R.id.backcontext);
        music_length = findViewById(R.id.music_length);
        seekBar = findViewById(R.id.seekBar);
        circularlayout = findViewById(R.id.circularlayout);
        circularFillableLoaders = (CircularFillableLoaders)findViewById(R.id.circularFillableLoaders);
        circularlayout.bringToFront();

        Intent intent = getIntent();
        parent_Id = intent.getStringExtra("parent_Id"); //콜라보레이션할 원곡의 uid postid!!!
        if(!parent_Id.equals("ori")){
            //콜라보 있게 addmusic에 왔음!
            onCollaborationClick(parent_Id);
        }
        backcontext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        pianoFragment = new PianoFragment();
        drumFragment = new DrumFragment();
        recordingMain = new RecordingMain();
        gray_screen = findViewById(R.id.gray_screen);

        getSupportFragmentManager().beginTransaction().replace(R.id.add_category_framelayout, new CategoryAddMusic()).commit();

        parents = new ArrayList<>();
        parentPostId = new ArrayList<>();

        //리사이클러 뷰 설정
        musicTrackView = (RecyclerView) findViewById(R.id.tracklist);
        llm = new LinearLayoutManager(this);
        musicTrackView.setLayoutManager(llm);

        tracks = new ArrayList<>();

        adapter = new MusicTrackAdapter(tracks);
        musicTrackView.setAdapter(adapter);

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myStartActivity(HomeMain.class);
            }
        });
        myInfobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myStartActivity(LoveActivity.class);
            }
        });
        librarybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myStartActivity(CollectionActivity.class);
            }
        });
        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddMusic.this, AddMusic.class);
                intent.putExtra("parent_Id", "ori");
                startActivity(intent);
            }
        });
        setbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { myStartActivity(NewSettingActivity.class);    }
        });

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

        plusbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCategoryFrameLayout.setVisibility(View.VISIBLE);
                popularFragment = new PopularFragment();
            }
        });
        playbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPlaying) {
                    // 재생중이었으면 경우 이미지를 Play로 변경
                    playbtn.setImageResource(R.drawable.testbtn);

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
                playbtn.setImageResource(R.drawable.final_pause);

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
                long location = (long)(((float)seekBar.getProgress() / 10000) * max_len / 1000 * 44100 * 4);
                long mlocation = location - (location % mBufferSize);
                if(isPlaying)
                    startNewAudioThread((int)mlocation);
                else {
                    playingLocation = (int)mlocation;
                    updateMusicPosition();
                }
            }
        });

    }
    public void showUpPopular(){
        detail_pickup_layout.setVisibility(View.VISIBLE);
        getSupportFragmentManager().beginTransaction().replace(R.id.detail_instrument_frame, popularFragment).commit();
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
    public void hideGray(){
        if (detail_pickup_layout.getVisibility() == View.VISIBLE) {
            detail_pickup_layout.setVisibility(View.GONE);
        }
    }

    private void hideProgressBar() {
        // 프로그레스바를 숨기는 코드
        circularlayout.animate()
                .alpha(0f) // 투명도를 0으로 설정하여 페이드 아웃 애니메이션 적용
                .setDuration(500) // 애니메이션 지속 시간 (밀리초)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        // 애니메이션 종료 후에 실행할 작업 (예: 뷰를 숨기거나 제거하는 등)
                        circularlayout.setVisibility(View.GONE);
                    }
                })
                .start();
    }
    public void onCollaborationClick(String addPostId) {
        circularlayout.setVisibility(View.VISIBLE);
        long currentTimeMillis = System.currentTimeMillis();
        // 시간을 원하는 포맷으로 변환합니다.
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String formattedTime = sdf.format(new Date(currentTimeMillis));

        ContentValues values = new ContentValues();

        values.put(MediaStore.Audio.Media.TITLE, "firebase");
        values.put(MediaStore.Audio.Media.DISPLAY_NAME, formattedTime);
        values.put(MediaStore.Audio.Media.DATE_ADDED, (int) (System.currentTimeMillis() / 1000));
        values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/x-wav");
        values.put(MediaStore.Audio.Media.RELATIVE_PATH, "Music/Daily Band/Songs/");

        Uri uri = getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);

        try {
            OutputStream outputStream = getContentResolver().openOutputStream(uri);

            StorageReference songRef = FirebaseStorage.getInstance().getReference().child("songs/" + addPostId + "/song");
            songRef.getStream((state, inputStream) -> {

                long totalBytes = state.getTotalByteCount();
                long bytesDownloaded = 0;

                byte[] buffer = new byte[1024];
                int size;
                while ((size = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, size);
                    bytesDownloaded += size;
                }

                inputStream.close();

            }).addOnSuccessListener(taskSnapshot -> {
                Toast.makeText(AddMusic.this, "콜라보레이션 노래가 등록되었습니다.", Toast.LENGTH_SHORT).show();
                Log.e("로그", "지금 되고 있는거야?" + uri.toString());
                addTrack(uri, addPostId, addPostId);
                hideProgressBar();
            }).addOnFailureListener(e -> {
                Log.w("asdf", "download:FAILURE", e);
            });
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void addTrack(Uri uri, String title) {
        MusicTrack track = new MusicTrack();
        track.uri = uri;
        track.title = title;
        track.original_postid = null;

        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(getApplicationContext(), uri);
        String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        int millSecond = Integer.parseInt(durationStr);
        track.length = millSecond;

        tracks.add(track);
        adapter.notifyDataSetChanged();
        updateMaxMusicLength();
    }
    private void addTrack(Uri uri, String title, String orig_post_id) {
        MusicTrack track = new MusicTrack();
        track.uri = uri;
        track.title = title;
        track.original_postid = orig_post_id;

        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(getApplicationContext(), uri);
        String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        int millSecond = Integer.parseInt(durationStr);
        track.length = millSecond;

        tracks.add(track);
        adapter.notifyDataSetChanged();
        updateMaxMusicLength();
    }

    private void updateMaxMusicLength() {
        max_len = 0;
        for(int i=0;i<tracks.size();i++) {
            max_len = Math.max(max_len, tracks.get(i).length);
        }

        updateMusicPosition();
    }

    public void updateMusicPosition() {
        long max_minutes = (max_len / 1000)  / 60;
        int max_seconds = (int)((max_len / 1000) % 60);

        float f_cur_len = (float)playingLocation / 44100 / 4;
        long cur_len = playingLocation / 44100 / 4;
        long cur_minutes = cur_len / 60;
        int cur_seconds = (int)(cur_len % 60);

        String musicLength = String.format("%02d:%02d / %02d:%02d", cur_minutes, cur_seconds, max_minutes, max_seconds);
        music_length.setText(musicLength);

        seekBar.setProgress((int)(f_cur_len / max_len * 10000000));
    }

    public void getPathFromStorage() {
        Intent intent_upload = new Intent();
        intent_upload.setType("audio/x-wav"); // wav파일만
        // intent_upload.setType("audio/*");
        intent_upload.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent_upload, 1);
    }

    @SuppressLint("Range")
    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    // 폴더에서 노래 가져온 결과
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        if(requestCode == 1){
            if(resultCode == RESULT_OK){
                // audio 위치
                Uri uri = data.getData();
//                String path = uri.getPath();
                String path = getFileName(uri);
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

        uri = mergeTracks(title);

        String temp;
        for(int i=0; i<tracks.size(); i++){
            temp = tracks.get(i).original_postid;
            if(temp != null) {
                parentPostId.add(temp);
                Log.d("asdf", "parent id : "+temp);
            }
        }

        Intent intent = new Intent(this, AddCaption.class);
        intent.putExtra("title", title);
        intent.putExtra("uri", uri.toString());
        intent.putExtra("original", parentPostId);
        startActivity(intent);
    }

    private Uri mergeTracks(String title) {
        ContentValues values = new ContentValues();
        long currentTimeMillis = System.currentTimeMillis();
        // 시간을 원하는 포맷으로 변환합니다.
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String formattedTime = sdf.format(new Date(currentTimeMillis));


        values.put(MediaStore.Audio.Media.TITLE, title);
        values.put(MediaStore.Audio.Media.DISPLAY_NAME, title+".wav");
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

                // 데이터를 처리하기 위한 배열
                byte[] readData = new byte[mBufferSize];
                byte[] writeData = new byte[mBufferSize];

                short[] readShorts = new short[mBufferSize/2];
                short[] writeShorts = new short[mBufferSize/2];

                int[] readInt = new int[mBufferSize/2];

                at.play();
                while (isPlaying) {
                    try {
                        // 버퍼 초기화
                        for(int j=0;j<mBufferSize/2;j++) {
                            readInt[j] = 0;
                        }

                        // 각 파일별로 읽어와 다 더해줌
                        int ret=0;
                        for(int i=0;i<trackSize;i++) {
                            if(tracks.get(i).isEnded) continue;

                            int tmp = is[i].read(readData, 0, mBufferSize);
                            if(tmp <= 0) tracks.get(i).isEnded = true;

                            if(!tracks.get(i).isSpeaking)
                                continue;

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

                        // 오디오 출력
                        at.write(writeData, 0, ret);

                        // 현재 재생위치 업데이트
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

                boolean isAllPlayed = true;
                for(int i=0;i<trackSize;i++) {
                    if(tracks.get(i).isEnded == false) {
                        isAllPlayed = false;
                        break;
                    }
                }
                // 전부 끝까지 재생했으면 재생위치 0으로 초기화
                if(isAllPlayed) {
                    playingLocation = 0;
                    runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            updateMusicPosition();
                        }
                    });
                }

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
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity(); // 현재 액티비티와 관련된 모든 액티비티 종료
        return;
    }
}