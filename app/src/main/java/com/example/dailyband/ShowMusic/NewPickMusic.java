package com.example.dailyband.ShowMusic;

import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dailyband.Models.ComplexName;
import com.example.dailyband.Models.TestSong;
import com.example.dailyband.R;
import com.example.dailyband.Utils.FirebaseMethods;
import com.example.dailyband.adapter.CollabAdapter;
import com.gauravk.audiovisualizer.visualizer.BlobVisualizer;
import com.gauravk.audiovisualizer.visualizer.CircleLineVisualizer;
import com.gauravk.audiovisualizer.visualizer.WaveVisualizer;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewPickMusic extends AppCompatActivity {
    private ImageView playbtn, stopbtn, optionmenu;
    private CompoundButton heartbtn;
    private boolean isLiked = false;
    private TextView picksongname;
    private FirebaseMethods mFirebaseMethods;
    private String postId, title, writer_uid;
    private RecyclerView recyclerView;
    private ConstraintLayout detail_info_layout, gray_screen;
    private LinearLayout emptytxt;
    private MediaPlayer mediaPlayer;
    private FrameLayout detail_info_frame;
    private ShowMusicInfoFragment showMusicInfoFragment;
    private DetailInfoFragment detailInfoFragment;
    private StorageReference songRef;
    SeekBar seekBar;
    Runnable runnable;
    Handler handler;
    private boolean isPlaying = false;
    // MediaPlayer 일시정지된 지점을 저장하는 변수
    private int pausedPosition = 0;
    private String writer, explain, singer, play, artist;
    private WaveVisualizer waveVisualizer;
    private CircleLineVisualizer visualizer;
    private BlobVisualizer blobVisualizer;

    protected void onDestroy(){
        if(blobVisualizer != null){
            blobVisualizer.release();
        }
        super.onDestroy();
    }

    //이건 맨 처음 체크할 때만, 그냥 애니메이션 효과 없이 할 것!
    private void updateHeartButton(boolean like) {
        if (like) {
            //heartbtn.setImageResource(R.drawable.heart_full);
            //heartbtn.setBackgroundResource( R.drawable.heart_full);
            isLiked = true;
            heartbtn.setChecked(true);
        } else {
            //animateHeartButton(false);
            //heartbtn.setImageResource(R.drawable.heart_outline_new);
            //heartbtn.setBackgroundResource( R.drawable.heart_outline_new);
            isLiked = false;
            heartbtn.setChecked(false);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_pickmusic);

        Intent intent = getIntent();
        TestSong selectedSong = (TestSong) intent.getParcelableExtra("selectedSong");
        postId = selectedSong.getPost_id();
        title = selectedSong.getTitle();
        writer_uid = selectedSong.getUser_id();
        writer = selectedSong.getWriter();
        explain = selectedSong.getExplain();
        singer = selectedSong.getSinger();
        play = selectedSong.getPlay();
        artist = selectedSong.getUser_id();
        mFirebaseMethods = new FirebaseMethods(NewPickMusic.this);
        mediaPlayer = new MediaPlayer();
        detailInfoFragment = new DetailInfoFragment();
        showMusicInfoFragment = new ShowMusicInfoFragment();
        handler = new Handler();
        seekBar = findViewById(R.id.seek_bar);
        playbtn = findViewById(R.id.playbtn);
        stopbtn = findViewById(R.id.stopbtn);
        picksongname = findViewById(R.id.pick_songname);
        picksongname.setText(title);
        heartbtn = findViewById(R.id.heartbtn);
        optionmenu = findViewById(R.id.optionmenu);

        gray_screen = findViewById(R.id.gray_screen);
        detail_info_layout = findViewById(R.id.detail_info_layout);
        detail_info_frame = findViewById(R.id.detail_info_frame);
        //waveVisualizer = findViewById(R.id.wave);
        //visualizer = findViewById(R.id.blob);
        blobVisualizer = findViewById(R.id.blob);
        //visualizer.setDrawLine(true);
        ScaleAnimation scaleAnimation = new ScaleAnimation(0.7f, 1.0f, 0.7f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.7f, Animation.RELATIVE_TO_SELF, 0.7f);
        scaleAnimation.setDuration(500);
        BounceInterpolator bounceInterpolator = new BounceInterpolator();
        scaleAnimation.setInterpolator(bounceInterpolator);

        getInfo();
        setInfo();
        setDetail();


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(b){
                    mediaPlayer.seekTo(i);
                    seekBar.setProgress(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        optionmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // detail_info_layout을 보이도록 변경합니다.
                detail_info_layout.setVisibility(View.VISIBLE);
                Bundle bundle = new Bundle();
                bundle.putBoolean("isLiked", isLiked);

                // 프래그먼트에 Bundle을 전달
                detailInfoFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.detail_info_frame, detailInfoFragment).commit();
            }
        });

        gray_screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (detail_info_layout.getVisibility() == View.VISIBLE) {
                    detail_info_layout.setVisibility(View.GONE);
                }
            }
        });

        mFirebaseMethods.chkIsLiked(postId, new FirebaseMethods.OnLikeCheckListener() {
            @Override
            public void onLikeChecked(boolean isLiked) {
                // 좋아요 상태를 받아왔을 때의 처리
                //updateHeartButton(isLiked);
                heartbtn.setChecked(isLiked);
            }

            @Override
            public void onLikeCheckFailed(String errorMessage) {
                // 좋아요 상태 확인에 실패했을 때의 처리
                Log.e("로그", "좋아요 확인 실패: " + errorMessage);
            }
        });
        heartbtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                compoundButton.startAnimation(scaleAnimation);
                isLiked = !isLiked;
                // 여기서 해당 postId를 사용자의 좋아요 리스트에 추가 또는 삭제하기
                mFirebaseMethods.addOrRemoveLike(title, postId, isLiked, writer_uid, new FirebaseMethods.OnLikeActionListener() {
                    @Override
                    public void onLikeAdded() {
                        // 좋아요가 추가되었을 때의 처리
                        updateHeartButton(true);
                    }

                    @Override
                    public void onLikeRemoved() {
                        // 좋아요가 제거되었을 때의 처리
                        updateHeartButton(false);
                    }

                    @Override
                    public void onFailed(String errorMessage) {
                        // 좋아요 추가 또는 제거에 실패한 경우의 처리
                        // errorMessage를 이용하여 실패 이유를 확인할 수 있음
                        Log.e("로그", "좋아요 처리 실패: " + errorMessage);
                    }
                });
            }
        });
        // playBtn 클릭 이벤트 처리
        playbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 현재 MediaPlayer가 null이거나 재생 중이지 않은 경우
                if (mediaPlayer == null || !mediaPlayer.isPlaying()) {
                    // Firebase Storage에서 WAV 파일 다운로드 및 재생 코드
                    StorageReference songRef = FirebaseStorage.getInstance().getReference().child("songs/"+postId+"/song");
                    songRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String downloadUrl = uri.toString();

                            mediaPlayer = new MediaPlayer();
                            try {
                                mediaPlayer.setDataSource(downloadUrl);
                                mediaPlayer.prepareAsync(); //비동기로 준비
                                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                    @Override
                                    public void onPrepared(MediaPlayer mediaPlayer) {
                                        mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                                            @Override
                                            public void onBufferingUpdate(MediaPlayer mp, int i) {
                                                double ratio = i/100.0;
                                                int bufferingLevel = (int)(mp.getDuration()*ratio);
                                                seekBar.setSecondaryProgress(bufferingLevel);
                                            }
                                        });

                                        // 일시정지된 지점부터 재생
                                        seekBar.setMax(mediaPlayer.getDuration());
                                        mediaPlayer.seekTo(pausedPosition);
                                        mediaPlayer.start();
                                        //if(waveVisualizer != null){
                                        //    waveVisualizer.hide();
                                        //}
                                        int audioSessionId = mediaPlayer.getAudioSessionId();
                                        if(audioSessionId != -1){
                                            //waveVisualizer.setAudioSessionId(audioSessionId);
                                            //visualizer.setAudioSessionId(audioSessionId);
                                            blobVisualizer.setAudioSessionId(audioSessionId);
                                        }

                                        isPlaying = true;
                                        updateSeekbar();
                                        // 재생 중인 경우 Play 버튼 이미지를 Pause로 변경
                                        playbtn.setImageResource(R.drawable.pause);
                                    }
                                });

                                // 재생이 끝날 때의 콜백 처리
                                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mp) {
                                        // 재생이 끝나면 seekBar를 초기 위치로 이동하고 playbtn 이미지를 다시 Play 이미지로 변경
                                        seekBar.setProgress(0);
                                        playbtn.setImageResource(R.drawable.playbtn);
                                    }
                                });
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // 다운로드 실패 시 처리
                        }
                    });
                } else {
                    // MediaPlayer가 재생 중인 경우 일시정지
                    if (isPlaying) {
                        // 일시정지된 지점을 저장
                        pausedPosition = mediaPlayer.getCurrentPosition();
                        mediaPlayer.pause();
                        isPlaying = false;
                        // Pause 버튼 이미지를 Play로 변경

                        playbtn.setImageResource(R.drawable.playbtn);
                    } else {
                        // 일시정지된 지점부터 재생
                        mediaPlayer.seekTo(pausedPosition);
                        int audioSessionId = mediaPlayer.getAudioSessionId();
                        //if(audioSessionId != -1){
                        //    waveVisualizer.setAudioSessionId(audioSessionId);
                        //}
                        mediaPlayer.start();
                        isPlaying = true;
                        // 재생 중인 경우 Play 버튼 이미지를 Pause로 변경
                        playbtn.setImageResource(R.drawable.pause);

                    }
                }
            }
        });

        stopbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                    pausedPosition = 0;
                    seekBar.setProgress(0); // seekBar를 맨 처음 위치로 되돌립니다.

                    playbtn.setImageResource(R.drawable.playbtn);
                }
            }
        });
    }
    public void updateSeekbar() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            int curPos = mediaPlayer.getCurrentPosition();
            seekBar.setProgress(curPos);
            runnable = new Runnable() {
                @Override
                public void run() {
                    updateSeekbar();
                }
            };
            handler.postDelayed(runnable, 100);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        /* 액티비티가 일시정지될 때 MediaPlayer를 중지하고 해제합니다.
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        */
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public void showUpInfo(){
        getSupportFragmentManager().beginTransaction().replace(R.id.detail_info_frame, showMusicInfoFragment).commit();
    }
    public void blindFrame(){
        if (detail_info_layout.getVisibility() == View.VISIBLE) {
            detail_info_layout.setVisibility(View.GONE);
        }
    }

    private void getInfo(){
        DatabaseReference userAccountRef = FirebaseDatabase.getInstance().getReference().child("UserAccount").child(writer_uid);
        userAccountRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot userAccountDataSnapshot) {
                if (userAccountDataSnapshot.exists()) {
                    // UserAccount 카테고리에서 name 값을 가져와서 사용하거나 처리할 수 있습니다.
                    artist = userAccountDataSnapshot.child("name").getValue(String.class);
                    setDetail();
                    setInfo();
                    // 가져온 데이터를 사용하거나 처리할 수 있습니다.
                } else {
                    // "UserAccount" 카테고리에서 해당 데이터가 없는 경우에 대한 처리
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 데이터 가져오기가 실패한 경우에 대한 처리
            }
        });
    }

    public void setDetail(){
        detailInfoFragment.setDetailInfo(isLiked, title, artist);
    }
    public void setInfo(){
        showMusicInfoFragment.setSongInfo(artist, writer, play, singer, explain);
    }

    private void getCollab(String postId){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        List<ComplexName> collabsongs = new ArrayList<>();
        DatabaseReference collabRef = databaseReference.child("my_children").child(postId);
        collabRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                collabsongs.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Map<String, Object> objectMap = (HashMap<String, Object>) dataSnapshot.getValue();
                    ComplexName song = new ComplexName();
                    song.setWriteruid(objectMap.get("writeruid").toString());
                    song.setTitle(objectMap.get("title").toString());
                    song.setSongid(objectMap.get("songid").toString());
                    collabsongs.add(song);
                }

                CollabAdapter adapter = new CollabAdapter(NewPickMusic.this, collabsongs);

                adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                    @Override
                    public void onChanged() {
                        super.onChanged();
                        checkEmpty();
                    }

                    void checkEmpty(){
                        emptytxt.setVisibility(adapter.getItemCount()==0?View.VISIBLE:View.GONE);
                    }
                });

                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
