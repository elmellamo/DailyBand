package com.example.dailyband.adapter;

import android.content.Context;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import com.example.dailyband.Models.TestSong;
import com.example.dailyband.MusicAdd.AddMusic;
import com.example.dailyband.MusicAdd.CollabAddMusic;
import com.example.dailyband.R;
import com.example.dailyband.Utils.OnCollaborationClickListener;
import com.example.dailyband.Utils.OnRecordingCompletedListener;
import com.example.dailyband.Utils.OnSongUrlListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class PPExpandableListAdapter extends BaseExpandableListAdapter {
    private ArrayList<TestSong> data;
    public static Context mContext;
    private Handler handler = new Handler(Looper.getMainLooper());
    private MediaPlayer[] mediaPlayer;
    private SeekBar[] seekBars;
    private AddMusic mActivity;
    private ImageView[] playbtns;
    private OnCollaborationClickListener onCollaborationClickListener;
    private int[] pausedPositions;

    public PPExpandableListAdapter(ArrayList<TestSong> data, Context mContext, AddMusic activity) {
        this.data = data;
        this.mContext = mContext;
        this.mediaPlayer = new MediaPlayer[data.size()];
        this.seekBars = new SeekBar[data.size()];
        this.playbtns = new ImageView[data.size()];
        this.pausedPositions = new int[data.size()];
        this.mActivity = activity;
        Arrays.fill(pausedPositions, 0);

        // MediaPlayer 배열의 각 요소를 초기화합니다.
        for (int i = 0; i < mediaPlayer.length; i++) {
            mediaPlayer[i] = new MediaPlayer();
        }
        if (activity instanceof OnCollaborationClickListener) {
            this.onCollaborationClickListener = (OnCollaborationClickListener) activity;
        }
    }

    // Getter 메서드: 배열의 복사본을 반환하여 원본 배열 보호
    public MediaPlayer[] getMediaPlayerArray() {
        return Arrays.copyOf(mediaPlayer, mediaPlayer.length);
    }

    // Setter 메서드: 배열을 새로운 배열로 대체하도록 설정
    public void setMediaPlayerArray(MediaPlayer[] newMediaPlayerArray) {
        mediaPlayer = Arrays.copyOf(newMediaPlayerArray, newMediaPlayerArray.length);
    }
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        // 자식 항목 반환 (Firebase 스토리지에서 음악 파일을 다운로드하기 위한 정보를 여기에 추가)
        return data.get(groupPosition).getPost_id();
    }
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    // MediaPlayer 초기화 및 해제를 위한 메소드 추가
    private void initializeMediaPlayer(int position, ImageView btnplay, SeekBar seek) {

        mediaPlayer[position].setOnCompletionListener(mp -> {
            // 재생이 끝나면 처리
            seek.setProgress(0);
            btnplay.setImageDrawable(mContext.getResources().getDrawable(R.drawable.my_basic_play));
            Drawable drawable = btnplay.getDrawable();
            if (drawable instanceof AnimatedVectorDrawableCompat) {
                AnimatedVectorDrawableCompat avd = (AnimatedVectorDrawableCompat) drawable;
                avd.start();
            } else if (drawable instanceof AnimatedVectorDrawable) {
                AnimatedVectorDrawable avd2 = (AnimatedVectorDrawable) drawable;
                avd2.start();
            }
            // MediaPlayer 해제
            releaseMediaPlayer(position);
        });
    }

    private void releaseMediaPlayer(int position) {
        if (mediaPlayer[position] != null) {
            if (mediaPlayer[position].isPlaying()) {
                mediaPlayer[position].stop();
            }
            mediaPlayer[position].reset();
            mediaPlayer[position].release();
            mediaPlayer[position] = null;
        }
    }
    public void releaseAllMediaPlayers() {
        for (int i = 0; i < mediaPlayer.length; i++) {
            releaseMediaPlayer(i);
        }
    }

    // updateSeekbar 동작을 중지하는 메서드
    public void stopUpdateSeekbars() {
        handler.removeCallbacksAndMessages(null);
    }
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.pick_popular_child, parent, false);
        Context context = view.getContext();

        AnimatedVectorDrawableCompat avd;
        AnimatedVectorDrawable avd2;

        TestSong headerItem = data.get(groupPosition);
        String postId = headerItem.getPost_id();

        ImageView playbtn = view.findViewById(R.id.playbtn);
        SeekBar seekBar = view.findViewById(R.id.seek_bar);
        ImageView collaboration = view.findViewById(R.id.collaboration);

        playbtns[groupPosition] = playbtn;
        seekBars[groupPosition] = seekBar;

        MediaPlayer currentMediaPlayer = getMediaPlayerArray()[groupPosition];
        ImageView currentPlaybtn = playbtns[groupPosition];
        SeekBar currentSeekBar = seekBars[groupPosition];

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(b){
                    currentMediaPlayer.seekTo(i);
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
                if (currentMediaPlayer == null || !currentMediaPlayer.isPlaying()) {
                    initializeMediaPlayer(groupPosition, currentPlaybtn, currentSeekBar);
                    // Firebase Storage에서 WAV 파일 다운로드 및 재생 코드
                    StorageReference songRef = FirebaseStorage.getInstance().getReference().child("songs/"+postId+"/song");
                    songRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String downloadUrl = uri.toString();

                            try {
                                currentMediaPlayer.setDataSource(downloadUrl);
                                currentMediaPlayer.prepareAsync();
                                currentMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                    @Override
                                    public void onPrepared(MediaPlayer mp) {
                                        // 재생 준비 완료 시 처리

                                        mp.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                                            @Override
                                            public void onBufferingUpdate(MediaPlayer mp, int i) {
                                                double ratio = i / 100.0;
                                                int bufferingLevel = (int) (mp.getDuration() * ratio);
                                                currentSeekBar.setSecondaryProgress(bufferingLevel);
                                            }
                                        });

                                        currentSeekBar.setMax(mp.getDuration());
                                        mp.seekTo(pausedPositions[groupPosition]);
                                        mp.start();
                                        updateSeekbar(mp, currentSeekBar);
                                        currentPlaybtn.setImageDrawable(context.getResources().getDrawable(R.drawable.my_basic_pause));
                                        Drawable drawable = currentPlaybtn.getDrawable();
                                        if(drawable instanceof AnimatedVectorDrawableCompat){
                                            AnimatedVectorDrawableCompat avd = (AnimatedVectorDrawableCompat) drawable;
                                            avd.start();
                                        }else if(drawable instanceof AnimatedVectorDrawable){
                                            AnimatedVectorDrawable avd2 = (AnimatedVectorDrawable) drawable;
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
                        }
                    });
                } else {
                    // 이미 재생 중인 경우 일시정지 또는 재생 중지
                    if (currentMediaPlayer.isPlaying()) {
                        pausedPositions[groupPosition] = currentMediaPlayer.getCurrentPosition();
                        currentMediaPlayer.pause();
                        // 일시정지된 지점을 저장
                        currentPlaybtn.setImageDrawable(mContext.getResources().getDrawable(R.drawable.my_basic_play));
                        Drawable drawable = currentPlaybtn.getDrawable();
                        if(drawable instanceof AnimatedVectorDrawableCompat){
                            AnimatedVectorDrawableCompat avd = (AnimatedVectorDrawableCompat) drawable;
                            avd.start();
                        }else if(drawable instanceof AnimatedVectorDrawable){
                            AnimatedVectorDrawable avd2 = (AnimatedVectorDrawable) drawable;
                            avd2.start();
                        }
                    } else {
                        // 일시정지된 지점부터 재생
                        currentMediaPlayer.seekTo(pausedPositions[groupPosition]);
                        currentMediaPlayer.start();
                        //updateSeekbar(currentMediaPlayer, currentSeekBar);
                        currentPlaybtn.setImageDrawable(context.getResources().getDrawable(R.drawable.my_basic_pause));
                        Drawable drawable = currentPlaybtn.getDrawable();
                        if(drawable instanceof AnimatedVectorDrawableCompat){
                            AnimatedVectorDrawableCompat avd = (AnimatedVectorDrawableCompat) drawable;
                            avd.start();
                        }else if(drawable instanceof AnimatedVectorDrawable){
                            AnimatedVectorDrawable avd2 = (AnimatedVectorDrawable) drawable;
                            avd2.start();
                        }
                    }
                }


            }
        });

        collaboration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onCollaborationClickListener != null){
                    onCollaborationClickListener.onCollaborationClick(postId);
                    currentSeekBar.setProgress(0);
                    currentPlaybtn.setImageDrawable(mContext.getResources().getDrawable(R.drawable.my_basic_play));
                    Drawable drawable = currentPlaybtn.getDrawable();
                    if (drawable instanceof AnimatedVectorDrawableCompat) {
                        AnimatedVectorDrawableCompat avd = (AnimatedVectorDrawableCompat) drawable;
                        avd.start();
                    } else if (drawable instanceof AnimatedVectorDrawable) {
                        AnimatedVectorDrawable avd2 = (AnimatedVectorDrawable) drawable;
                        avd2.start();
                    }
                    // MediaPlayer 해제
                    //releaseMediaPlayer(groupPosition);
                    // 모든 MediaPlayer 해제
                    releaseAllMediaPlayers();
                    // updateSeekbar 중지
                    stopUpdateSeekbars();

                    mActivity.hideGray();
                }
            }
        });

        return view;
    }

    public void updateSeekbar(MediaPlayer mp, SeekBar sb) {
        if (mp != null && sb != null) {
            if (mp.isPlaying()) {
                int curPos = mp.getCurrentPosition();
                sb.setProgress(curPos);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        updateSeekbar(mp, sb);
                    }
                }, 100);
            }
        }
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        // 각 부모 항목에 대한 자식 항목의 개수를 반환 (이 경우 항상 1)
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return data.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return data.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        TestSong headerItem = data.get(groupPosition);
        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // 커스텀 헤더 뷰를 인플레이트합니다.
        View view = inflater.inflate(R.layout.pick_popular_head, parent, false);

        // 헤더에 표시할 데이터 설정
        TextView numRanking = view.findViewById(R.id.numRanking);
        TextView songname = view.findViewById(R.id.songname);
        ImageView btn_expand_toggle = view.findViewById(R.id.btn_expand_toggle);
        numRanking.setText(String.valueOf(groupPosition+1));
        songname.setText(headerItem.getTitle());
        if (isExpanded) {
            // 펼쳐진 상태에서는 화살표 위로 변경
            btn_expand_toggle.setImageResource(R.drawable.arrow_up);
        } else {
            // 닫혀진 상태에서는 화살표 아래로 변경
            btn_expand_toggle.setImageResource(R.drawable.arrow_below);
        }

        return view;
    }

    @Override
    public boolean hasStableIds() {
            return true;
    }
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}


