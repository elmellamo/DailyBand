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
import com.example.dailyband.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class PPExpandableListAdapter extends BaseExpandableListAdapter {
    private ArrayList<TestSong> data;
    public static Context mContext;
    private Handler handler = new Handler(Looper.getMainLooper());
    private MediaPlayer[] mediaPlayer;
    private SeekBar[] seekBars;

    private int[] pausedPositions;

    public PPExpandableListAdapter(ArrayList<TestSong> data, Context mContext) {
        this.data = data;
        this.mContext = mContext;
        this.mediaPlayer = new MediaPlayer[data.size()];
        this.seekBars = new SeekBar[data.size()];
        this.pausedPositions = new int[data.size()];
        Arrays.fill(pausedPositions, 0);

        // MediaPlayer 배열의 각 요소를 초기화합니다.
        for (int i = 0; i < mediaPlayer.length; i++) {
            mediaPlayer[i] = new MediaPlayer();
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
        seekBars[groupPosition] = seekBar;
        ImageView collaboration = view.findViewById(R.id.collaboration);

        MediaPlayer currentMediaPlayer = getMediaPlayerArray()[groupPosition];
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
                // 현재 MediaPlayer가 null이거나 재생 중이지 않은 경우
                if (currentMediaPlayer == null || !currentMediaPlayer.isPlaying()) {
                    // Firebase Storage에서 WAV 파일 다운로드 및 재생 코드
                    StorageReference songRef = FirebaseStorage.getInstance().getReference().child("songs/"+postId+"/song");
                    songRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String downloadUrl = uri.toString();


                            try {
                                currentMediaPlayer.setDataSource(downloadUrl);
                                currentMediaPlayer.prepareAsync(); //비동기로 준비
                                currentMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
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
                                        mediaPlayer.seekTo(pausedPositions[groupPosition]);
                                        mediaPlayer.start();

                                        //여기에서 isPlaying하지 않음
                                        updateSeekbar(currentMediaPlayer, seekBar);
                                        playbtn.setImageDrawable(context.getResources().getDrawable(R.drawable.my_basic_pause));
                                        Drawable drawable = playbtn.getDrawable();
                                        if(drawable instanceof AnimatedVectorDrawableCompat){
                                            AnimatedVectorDrawableCompat avd = (AnimatedVectorDrawableCompat) drawable;
                                            avd.start();
                                        }else if(drawable instanceof AnimatedVectorDrawable){
                                            AnimatedVectorDrawable avd2 = (AnimatedVectorDrawable) drawable;
                                            avd2.start();
                                        }
                                    }
                                });

                                // 재생이 끝날 때의 콜백 처리
                                currentMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mp) {
                                        // 재생이 끝나면 seekBar를 초기 위치로 이동하고 playbtn 이미지를 다시 Play 이미지로 변경
                                        //blobVisualizer.setVisibility(View.GONE);
                                        seekBar.setProgress(0);
                                        //playbtn.setImageResource(R.drawable.playbtn);
                                        playbtn.setImageDrawable(context.getResources().getDrawable(R.drawable.my_basic_play));
                                        Drawable drawable = playbtn.getDrawable();
                                        if(drawable instanceof AnimatedVectorDrawableCompat){
                                            AnimatedVectorDrawableCompat avd = (AnimatedVectorDrawableCompat) drawable;
                                            avd.start();
                                        }else if(drawable instanceof AnimatedVectorDrawable){
                                            AnimatedVectorDrawable avd2 = (AnimatedVectorDrawable) drawable;
                                            avd2.start();
                                        }

                                        currentMediaPlayer.release();
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
                    if (currentMediaPlayer.isPlaying()) {
                        // 일시정지된 지점을 저장
                        pausedPositions[groupPosition] = currentMediaPlayer.getCurrentPosition();
                        currentMediaPlayer.pause();
                        playbtn.setImageDrawable(context.getResources().getDrawable(R.drawable.my_basic_play));
                        Drawable drawable = playbtn.getDrawable();
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
                        playbtn.setImageDrawable(context.getResources().getDrawable(R.drawable.my_basic_pause));
                        Drawable drawable = playbtn.getDrawable();
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

        return view;
    }

    public void updateSeekbar(MediaPlayer mp, SeekBar sb) {
        if (mp != null && mp.isPlaying()) {
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


