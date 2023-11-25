package com.example.dailyband.MusicFragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import com.example.dailyband.Models.TestSong;
import com.example.dailyband.MusicAdd.AddMusic;
import com.example.dailyband.R;
import com.example.dailyband.Utils.FirebaseMethods;
import com.example.dailyband.Utils.PopUpClickListener;
import com.example.dailyband.adapter.PopUp_New_PopularAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class NewPopularFragment extends Fragment implements PopUpClickListener {
    private RecyclerView popup_recyclerview;
    private PopUp_New_PopularAdapter adapter;
    private ArrayList<TestSong> songs;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;
    private FirebaseAuth mAuth;
    private AddMusic addMusicActivity;
    private ImageView playbtn, collaboration;
    private SeekBar seek_bar;
    private TextView songnametxt;
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;
    private AnimatedVectorDrawableCompat avd;
    private AnimatedVectorDrawable avd2;
    private Runnable runnable;
    private Handler handler;
    private int pausedPosition = 0;
    private String selectedPostId, selectedSongname;
    private ConstraintLayout showseekbar;
    public NewPopularFragment() {
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.popup_new_popular, container, false);
        mFirebaseMethods = new FirebaseMethods(getContext());
        // 리사이클러뷰 초기화
        popup_recyclerview = view.findViewById(R.id.popup_recyclerview);
        playbtn = view.findViewById(R.id.playbtn);
        collaboration = view.findViewById(R.id.collaboration);
        seek_bar = view.findViewById(R.id.seek_bar);
        songnametxt = view.findViewById(R.id.songnametxt);
        showseekbar = view.findViewById(R.id.showseekbar);
        popup_recyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        handler = new Handler();
        mediaPlayer = new MediaPlayer();
        songs = new ArrayList<>();
        addMusicActivity = (AddMusic) getActivity();

        getSongs();

        collaboration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                    pausedPosition = 0;
                    seek_bar.setProgress(0);
                }
                showseekbar.setVisibility(View.GONE);
                addMusicActivity.onCollaborationClick(selectedPostId);
                //여기에 addMusicActivity에 넣는거 고려해봐야겟음 postId? oncollaborationclick
                addMusicActivity.hideGray();
            }
        });

        seek_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
        playbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 현재 MediaPlayer가 null이거나 재생 중이지 않은 경우
                if (mediaPlayer == null || !mediaPlayer.isPlaying()) {
                    //showProgressBar();
                    addMusicActivity.showProgressBar_addmusic();
                    // Firebase Storage에서 WAV 파일 다운로드 및 재생 코드
                    StorageReference songRef = FirebaseStorage.getInstance().getReference().child("songs/"+selectedPostId+"/song");
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
                                        addMusicActivity.hideProgressBar_addmusic();
                                        //hideProgressBar();
                                        mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                                            @Override
                                            public void onBufferingUpdate(MediaPlayer mp, int i) {
                                                double ratio = i/100.0;
                                                int bufferingLevel = (int)(mp.getDuration()*ratio);
                                                seek_bar.setSecondaryProgress(bufferingLevel);
                                            }
                                        });

                                        // 일시정지된 지점부터 재생
                                        seek_bar.setMax(mediaPlayer.getDuration());
                                        mediaPlayer.seekTo(pausedPosition);
                                        mediaPlayer.start();

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
                                        seek_bar.setProgress(0);
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
                            //hideProgressBar();
                            addMusicActivity.hideProgressBar_addmusic();
                        }
                    });
                } else {
                    // MediaPlayer가 재생 중인 경우 일시정지
                    if (isPlaying) {
                        // 일시정지된 지점을 저장
                        pausedPosition = mediaPlayer.getCurrentPosition();
                        mediaPlayer.pause();
                        isPlaying = false;
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
                        mediaPlayer.start();
                        isPlaying = true;
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


        return view;
    }

    private void getSongs(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("songs");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                songs.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    TestSong song = snapshot.getValue(TestSong.class);
                    if (song != null) {
                        songs.add(song);
                    }
                }

                // songs 리스트를 love를 기준으로 내림차순으로 정렬하고, love가 같을 경우에는 시간을 기준으로 내림차순으로 정렬
                Collections.sort(songs, new Comparator<TestSong>() {
                    @Override
                    public int compare(TestSong song1, TestSong song2) {
                        // love가 큰 순서대로 정렬
                        int loveComparison = Integer.compare(song2.getLove(), song1.getLove());
                        if (loveComparison != 0) {
                            return loveComparison;
                        }
                        // love가 같은 경우 시간을 큰 순서대로 정렬
                        return song2.getDate_created().compareTo(song1.getDate_created());
                    }
                });
                Log.d("로그", "여기 되나요????");
                adapter = new PopUp_New_PopularAdapter(getContext(), songs, NewPopularFragment.this, popup_recyclerview);
                popup_recyclerview.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 데이터를 가져오는데 실패한 경우 처리하는 코드
                Toast.makeText(getContext(), "인기 순위를 불러오는 것에 실패하였습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void onStop() {
        super.onStop();
    }

    private void updateSeekbar() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            int curPos = mediaPlayer.getCurrentPosition();
            seek_bar.setProgress(curPos);
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

    @Override
    public void onPause() {
        super.onPause();

        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            pausedPosition = 0;
            seek_bar.setProgress(0);
        }
    }


    @Override
    public void onPopUpItemClicked(String postId, String songname) {
        //mediaPlayer를 null로 만들고 싶어. 모든 리소스 해제하기
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            pausedPosition = 0;
            seek_bar.setProgress(0);
        }
        showseekbar.setVisibility(View.VISIBLE);
        selectedPostId = postId;
        selectedSongname = songname;

        playbtn.setImageResource(R.drawable.testbtn);
        songnametxt.setText(selectedSongname);
    }
}
