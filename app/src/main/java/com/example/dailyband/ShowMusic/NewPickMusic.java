package com.example.dailyband.ShowMusic;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import com.example.dailyband.Collection.CollectionActivity;
import com.example.dailyband.Home.HomeMain;
import com.example.dailyband.Love.LoveActivity;
import com.example.dailyband.Models.CommentItem;
import com.example.dailyband.Models.ComplexName;
import com.example.dailyband.Models.TestSong;
import com.example.dailyband.MusicAdd.AddMusic;
import com.example.dailyband.R;
import com.example.dailyband.Setting.NewSettingActivity;
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
import com.mikhaellopez.circularfillableloaders.CircularFillableLoaders;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewPickMusic extends AppCompatActivity {
    private ImageView playbtn, stopbtn, optionmenu, birdimg;
    private CompoundButton heartbtn;
    private boolean isLiked = false;
    private TextView picksongname;
    private boolean doubleBackToExitPressedOnce = false;
    private FirebaseMethods mFirebaseMethods;
    private String postId, title, writer_uid;
    private RecyclerView recyclerView;
    private ConstraintLayout detail_info_layout, gray_screen;
    private LinearLayout emptytxt;
    private MediaPlayer mediaPlayer;
    private FrameLayout detail_info_frame;
    private ShowMusicInfoFragment showMusicInfoFragment;
    private ShowCollaborationFragment showCollaborationFragment;
    private ShowOrigianlFragment showOrigianlFragment;
    private CommentDetailFragment commentDetailFragment;
    private DetailInfoFragment detailInfoFragment;
    private CommentMainFragment commentMainFragment;
    private StorageReference songRef;
    SeekBar seekBar;
    Runnable runnable;
    Handler handler;
    private boolean isPlaying = false;
    public ConstraintLayout menubar;
    private ImageButton addbtn, setbtn, librarybtn, myInfobtn, homeBtn;
    // MediaPlayer 일시정지된 지점을 저장하는 변수
    private int pausedPosition = 0;
    private String writer, explain, singer, play, artist;
    private BlobVisualizer blobVisualizer;
    private AnimatedVectorDrawableCompat avd;
    private AnimatedVectorDrawable avd2;
    private int switchnum = 0;
    private boolean isScaleUp = true;
    private CircularFillableLoaders circularFillableLoaders;
    private ConstraintLayout circularlayout;
    private boolean is_Fragment_Open = false;
    private boolean is_Re_Comment_Open = false;
    private boolean is_Second_Open = false;
    private CardView detail_cardview;

    protected void onDestroy(){
        if(blobVisualizer != null){
            blobVisualizer.release();
        }
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            pausedPosition = 0;
            seekBar.setProgress(0);
        }
        super.onDestroy();
    }
    protected void onStop(){
        super.onStop();
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
        showMusicInfoFragment = new ShowMusicInfoFragment();
        showCollaborationFragment = new ShowCollaborationFragment();
        showOrigianlFragment = new ShowOrigianlFragment();
        handler = new Handler();
        seekBar = findViewById(R.id.seek_bar);
        birdimg = findViewById(R.id.birdimg);
        playbtn = findViewById(R.id.playbtn);
        stopbtn = findViewById(R.id.stopbtn);
        picksongname = findViewById(R.id.pick_songname);
        picksongname.setText(title);
        heartbtn = findViewById(R.id.heartbtn);
        optionmenu = findViewById(R.id.optionmenu);
        detail_cardview = findViewById(R.id.detail_cardview);
        menubar = findViewById(R.id.menu_bar);
        homeBtn = findViewById(R.id.homeBtn);
        myInfobtn = findViewById(R.id.myInfobtn);
        librarybtn = findViewById(R.id.librarybtn);
        setbtn = findViewById(R.id.setbtn);
        addbtn = findViewById(R.id.addbtn);
        circularlayout = findViewById(R.id.circularlayout);
        circularFillableLoaders = (CircularFillableLoaders)findViewById(R.id.circularFillableLoaders);
        circularlayout.bringToFront();

        picksongname.setSingleLine(true);    // 한줄로 표시하기
        picksongname.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        picksongname.setSelected(true);

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
        setSize();
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

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
                // detail_info_layout을 보이도록 변경합니다.\
                if(detail_info_layout.getVisibility()==View.GONE){
                    is_Fragment_Open = true;
                    detailInfoFragment = new DetailInfoFragment();
                    detailInfoFragment.setDetailInfo(isLiked, title, artist, postId, writer_uid);
                    getSupportFragmentManager().beginTransaction().replace(R.id.detail_info_frame, detailInfoFragment).commit();
                    detail_info_layout.setVisibility(View.VISIBLE);
                    slideUp(detail_cardview);
                }
            }
        });

        gray_screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (detail_info_layout.getVisibility() == View.VISIBLE) {
                    slideDown(detail_cardview);
                    hideProgressBar();
                    is_Fragment_Open = false;
                    is_Second_Open = false;
                    is_Re_Comment_Open = false;
                }
            }
        });

        mFirebaseMethods.chkIsLiked(postId, new FirebaseMethods.OnLikeCheckListener() {
            @Override
            public void onLikeChecked(boolean mfirebaselike) {
                // 좋아요 상태를 받아왔을 때의 처리
                //updateHeartButton(isLiked);
                isLiked = mfirebaselike;
                heartbtn.setChecked(isLiked);

                Log.e("로그", "좋아요 체크 한번만 되는지 확인");
            }

            @Override
            public void onLikeCheckFailed(String errorMessage) {
                // 좋아요 상태 확인에 실패했을 때의 처리
                Log.e("로그", "좋아요 확인 실패: " + errorMessage);
            }
        });
        
        heartbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                heartbtn.startAnimation(scaleAnimation);
                isLiked = !isLiked;
                heartbtn.setChecked(isLiked);
                Log.e("로그", "좋아요 확인해볼게요 >> "+isLiked);

                // 여기서 해당 postId를 사용자의 좋아요 리스트에 추가 또는 삭제하기
                mFirebaseMethods.addOrRemoveLike(title, postId, isLiked, writer_uid, new FirebaseMethods.OnLikeActionListener() {
                    @Override
                    public void onLikeAdded() {
                        // 좋아요가 추가되었을 때의 처리
                        updateHeartButton(true);
                        Log.e("로그", "좋아요 ");
                    }

                    @Override
                    public void onLikeRemoved() {
                        // 좋아요가 제거되었을 때의 처리
                        updateHeartButton(false);
                        Log.e("로그", "싫어요!! ");
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
                    showProgressBar();
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
                                        hideProgressBar();
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

                                        //blobVisualizer.setVisibility(View.VISIBLE);
                                        int audioSessionId = mediaPlayer.getAudioSessionId();
                                        if(audioSessionId != -1){
                                            //waveVisualizer.setAudioSessionId(audioSessionId);
                                            //visualizer.setAudioSessionId(audioSessionId);
                                            blobVisualizer.setAudioSessionId(audioSessionId);
                                        }

                                        isPlaying = true;
                                        updateSeekbar();
                                        // 재생 중인 경우 Play 버튼 이미지를 Pause로 변경
                                        //playbtn.setImageResource(R.drawable.pause);
                                        playbtn.setImageDrawable(getResources().getDrawable(R.drawable.my_basic_pause));
                                        Drawable drawable = playbtn.getDrawable();
                                        if(drawable instanceof AnimatedVectorDrawableCompat){
                                            avd = (AnimatedVectorDrawableCompat) drawable;
                                            avd.start();
                                        }else if(drawable instanceof AnimatedVectorDrawable){
                                            avd2 = (AnimatedVectorDrawable) drawable;
                                            avd2.start();
                                        }
                                    }
                                });

                                // 재생이 끝날 때의 콜백 처리
                                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mp) {
                                        // 재생이 끝나면 seekBar를 초기 위치로 이동하고 playbtn 이미지를 다시 Play 이미지로 변경
                                        //blobVisualizer.setVisibility(View.GONE);
                                        seekBar.setProgress(0);
                                        //playbtn.setImageResource(R.drawable.playbtn);
                                        playbtn.setImageDrawable(getResources().getDrawable(R.drawable.my_basic_play));
                                        Drawable drawable = playbtn.getDrawable();
                                        if(drawable instanceof AnimatedVectorDrawableCompat){
                                            avd = (AnimatedVectorDrawableCompat) drawable;
                                            avd.start();
                                        }else if(drawable instanceof AnimatedVectorDrawable){
                                            avd2 = (AnimatedVectorDrawable) drawable;
                                            avd2.start();
                                        }
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
                            hideProgressBar();
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
                        //blobVisualizer.setVisibility(View.GONE);
                        //playbtn.setImageResource(R.drawable.playbtn);
                        playbtn.setImageDrawable(getResources().getDrawable(R.drawable.my_basic_play));
                        Drawable drawable = playbtn.getDrawable();
                        if(drawable instanceof AnimatedVectorDrawableCompat){
                            avd = (AnimatedVectorDrawableCompat) drawable;
                            avd.start();
                        }else if(drawable instanceof AnimatedVectorDrawable){
                            avd2 = (AnimatedVectorDrawable) drawable;
                            avd2.start();
                        }
                    } else {
                        // 일시정지된 지점부터 재생
                        mediaPlayer.seekTo(pausedPosition);
                        int audioSessionId = mediaPlayer.getAudioSessionId();
                        //if(audioSessionId != -1){
                        //    waveVisualizer.setAudioSessionId(audioSessionId);
                        //}

                        mediaPlayer.start();
                        //blobVisualizer.setVisibility(View.VISIBLE);
                        isPlaying = true;
                        // 재생 중인 경우 Play 버튼 이미지를 Pause로 변경
                        //playbtn.setImageResource(R.drawable.pause);
                        playbtn.setImageDrawable(getResources().getDrawable(R.drawable.my_basic_pause));
                        Drawable drawable = playbtn.getDrawable();
                        if(drawable instanceof AnimatedVectorDrawableCompat){
                            avd = (AnimatedVectorDrawableCompat) drawable;
                            avd.start();
                        }else if(drawable instanceof AnimatedVectorDrawable){
                            avd2 = (AnimatedVectorDrawable) drawable;
                            avd2.start();
                        }
                    }
                }
            }
        });

        stopbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation scaleDown = AnimationUtils.loadAnimation(NewPickMusic.this, R.anim.scale_down);
                Animation scaleUp = AnimationUtils.loadAnimation(NewPickMusic.this, R.anim.scale_up);
                if(isScaleUp){
                    stopbtn.startAnimation(scaleDown);
                }else{
                    stopbtn.startAnimation(scaleUp);
                }
                isScaleUp = !isScaleUp;

                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                    pausedPosition = 0;
                    seekBar.setProgress(0); // seekBar를 맨 처음 위치로 되돌립니다.
                    //if(blobVisualizer != null){
                    //    blobVisualizer.release();
                    //}
                    //blobVisualizer.setVisibility(View.GONE);
                    //playbtn.setImageResource(R.drawable.playbtn);
                    playbtn.setImageDrawable(getResources().getDrawable(R.drawable.my_basic_play));
                    Drawable drawable = playbtn.getDrawable();
                    if(drawable instanceof AnimatedVectorDrawableCompat){
                        avd = (AnimatedVectorDrawableCompat) drawable;
                        avd.start();
                    }else if(drawable instanceof AnimatedVectorDrawable){
                        avd2 = (AnimatedVectorDrawable) drawable;
                        avd2.start();
                    }
                }
            }
        });

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
                Intent intent = new Intent(NewPickMusic.this, AddMusic.class);
                intent.putExtra("parent_Id", "ori");
                startActivity(intent);
            }
        });
        setbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { myStartActivity(NewSettingActivity.class);    }
        });
    }
    private void updateSeekbar() {
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
        }else{
            handler.removeCallbacks(runnable);
        }
    }

    public void showProgressBarWithDelay() {
        showProgressBar();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                hideProgressBar();
            }
        }, 700); // 1초 대신 원하는 시간(밀리초) 설정 가능
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public void showUpInfo(){
        getSupportFragmentManager().beginTransaction().replace(R.id.detail_info_frame, showMusicInfoFragment).commit();
        slideUp(detail_cardview);
        is_Second_Open = true;
    }

    public void showUpComment(){
        commentMainFragment = new CommentMainFragment();
        commentMainFragment.setCommentMain(postId);
        getSupportFragmentManager().beginTransaction().replace(R.id.detail_info_frame, commentMainFragment).commit();
        slideUp(detail_cardview);
        //showProgressBarWithDelay();
        is_Second_Open = true;
    }
    public void showUpColla(){
        showCollaborationFragment = new ShowCollaborationFragment();
        showCollaborationFragment.setCollaborationInfo(postId);
        getSupportFragmentManager().beginTransaction().replace(R.id.detail_info_frame, showCollaborationFragment).commit();
        slideUp(detail_cardview);
        is_Second_Open = true;
    }
    public void showUpParent(){
        showOrigianlFragment = new ShowOrigianlFragment();
        showOrigianlFragment.setOriginalInfo(postId);
        getSupportFragmentManager().beginTransaction().replace(R.id.detail_info_frame, showOrigianlFragment).commit();
        slideUp(detail_cardview);
        is_Second_Open = true;
    }

    public void deleteActivity(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        Toast.makeText(getApplicationContext(), "해당 음악 삭제 완료", Toast.LENGTH_LONG).show();
        Intent delete_intent = new Intent(NewPickMusic.this, HomeMain.class);
        startActivity(delete_intent);
        Log.d("테스트", "삭제되나요?");
        if (detail_info_layout.getVisibility() == View.VISIBLE) {
            slideDown(detail_cardview);
            hideProgressBar();
            is_Fragment_Open = false;
            is_Second_Open = false;
            is_Re_Comment_Open = false;
        }
        finish();
    }

    public void closeActivity(String close_postid, String close_writeruid){
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        Intent close_intent = new Intent(NewPickMusic.this, ArtistInfo.class);
        //isLiked, title, artist, postId;

        //intent.putExtra("isLiked_intent", isLiked);
        close_intent.putExtra("title_intent", close_postid);
        close_intent.putExtra("postId_intent", close_postid);
        close_intent.putExtra("userUid_intent",close_writeruid);

        DatabaseReference close_userAccountRef = FirebaseDatabase.getInstance().getReference().child("UserAccount").child(close_writeruid);
        close_userAccountRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot userAccountDataSnapshot) {
                if (userAccountDataSnapshot.exists()) {
                    // UserAccount 카테고리에서 name 값을 가져와서 사용하거나 처리할 수 있습니다.
                    String close_artist = userAccountDataSnapshot.child("name").getValue(String.class);
                    close_intent.putExtra("artist_intent", close_artist);
                    startActivity(close_intent);
                    Log.d("테스트", "제발...");
                    if (detail_info_layout.getVisibility() == View.VISIBLE) {
                        slideDown(detail_cardview);
                        hideProgressBar();
                        is_Fragment_Open = false;
                        is_Second_Open = false;
                        is_Re_Comment_Open = false;
                    }
                    //finish();
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

    public void changeDetail(CommentItem nextItem){
        commentDetailFragment = new CommentDetailFragment();
        commentDetailFragment.setCommentDetail(nextItem);
        getSupportFragmentManager().beginTransaction().replace(R.id.detail_info_frame, commentDetailFragment).commit();
        is_Re_Comment_Open = true;
    }
    public void blindFrame(){
        slideDown(detail_cardview);
        is_Fragment_Open = false;
        is_Re_Comment_Open = false;
        is_Second_Open = false;
    }
    private void getInfo(){
        DatabaseReference userAccountRef = FirebaseDatabase.getInstance().getReference().child("UserAccount").child(writer_uid);
        userAccountRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot userAccountDataSnapshot) {
                if (userAccountDataSnapshot.exists()) {
                    // UserAccount 카테고리에서 name 값을 가져와서 사용하거나 처리할 수 있습니다.
                    artist = userAccountDataSnapshot.child("name").getValue(String.class);
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
    public void setInfo(){
        showMusicInfoFragment.setSongInfo(artist, writer, play, singer, explain);
    }

    private void setSize() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int blobPixels = (int) (metrics.widthPixels * 0.78); // 화면 너비의 78%
        int birdPixels = (int) (metrics.widthPixels * 0.52); // 화면 너비의 52%

        ConstraintLayout.LayoutParams paramsBlob = (ConstraintLayout.LayoutParams) blobVisualizer.getLayoutParams();
        paramsBlob.width = blobPixels;
        paramsBlob.height = blobPixels;
        blobVisualizer.setLayoutParams(paramsBlob);

        ConstraintLayout.LayoutParams paramsBird = (ConstraintLayout.LayoutParams) birdimg.getLayoutParams();
        paramsBird.width = birdPixels;
        paramsBird.height = birdPixels;
        birdimg.setLayoutParams(paramsBird);
    }


    private void myStartActivity(Class c){
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    private void slideDown(final View view) {
        TranslateAnimation animate = new TranslateAnimation(0, 0, 0, view.getHeight());
        animate.setDuration(500);
        animate.setFillAfter(true);
        animate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE); // 애니메이션 종료 후 뷰를 숨김
                detail_info_layout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) { }
        });
        view.startAnimation(animate);
    }
    private void slideUp(View view) {
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(0, 0, view.getHeight(), 0);
        animate.setDuration(500);
        view.startAnimation(animate);
    }

    public void showProgressBar() {
        // 프로그레스바를 보여주는 코드
        circularlayout.setAlpha(1f);
        circularlayout.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
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
    @Override
    public void onBackPressed() {
        if (is_Fragment_Open) {
            if(is_Second_Open && !is_Re_Comment_Open){
                //아티스트, 노래소개, 원곡, 콜라보 노래 보기, 댓글 들어가서 뒤로 갔을 때
                detailInfoFragment = new DetailInfoFragment();
                detailInfoFragment.setDetailInfo(isLiked, title, artist, postId, writer_uid);
                getSupportFragmentManager().beginTransaction().replace(R.id.detail_info_frame, detailInfoFragment).commit();
                is_Second_Open = false;
                hideProgressBar();
            }else if(is_Re_Comment_Open){
                //대댓글 들어가서 뒤로 가기 할 때 -> 다시 댓글보기가 나와야 한다.
                is_Re_Comment_Open = false;
                commentMainFragment = new CommentMainFragment();
                commentMainFragment.setCommentMain(postId);
                getSupportFragmentManager().beginTransaction().replace(R.id.detail_info_frame, commentMainFragment).commit();
                is_Second_Open = true;
                showProgressBarWithDelay();
            }else{
                //그냥 완전히 모든 걸 없애야 한다.
                slideDown(detail_cardview);
                is_Fragment_Open = false;
                is_Re_Comment_Open = false;
                is_Second_Open = false;
                hideProgressBar();
            }
        }else{
            hideProgressBar();
            super.onBackPressed();
        }
    }
}
