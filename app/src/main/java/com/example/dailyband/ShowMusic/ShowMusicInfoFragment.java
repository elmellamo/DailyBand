package com.example.dailyband.ShowMusic;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.dailyband.MusicAdd.AddMusic;
import com.example.dailyband.R;
import com.scwang.wave.MultiWaveHeader;

public class ShowMusicInfoFragment extends Fragment {
    private MultiWaveHeader waveHeader;
    private View view;
    private ImageView clearimg;
    private TextView artisttxt, writertxt, playtxt, singtxt, infotxt;
    private String artist, writer, play, singer, explain;
    public ShowMusicInfoFragment() {
    }

    // songName과 artist를 설정하는 메서드
    public void setSongInfo(String artist, String writer, String play, String singer, String explain) {
        this.artist = artist;
        this.writer = writer;
        this.singer = singer;
        this.play = play;
        this.explain = explain;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.show_music_info, container, false);

        clearimg = view.findViewById(R.id.clearimg);
        artisttxt = view.findViewById(R.id.artisttxt); // 예시로 텍스트뷰의 ID는 R.id.songNameTextView로 가정
        writertxt = view.findViewById(R.id.writertxt); // 예시로 텍스트뷰의 ID는 R.id.artistTextView로 가정
        playtxt = view.findViewById(R.id.playtxt); // 예시로 텍스트뷰의 ID는 R.id.songNameTextView로 가정
        singtxt = view.findViewById(R.id.singtxt); // 예시로 텍스트뷰의 ID는 R.id.artistTextView로 가정
        infotxt = view.findViewById(R.id.infotxt); // 예시로 텍스트뷰의 ID는 R.id.songNameTextView로 가정


        artisttxt.setText(artist);
        writertxt.setText(writer);
        playtxt.setText(play);
        singtxt.setText(singer);
        infotxt.setText(explain);

        waveHeader = view.findViewById(R.id.wave_header);
        waveHeader.setVelocity(1);
        waveHeader.setProgress(1);
        waveHeader.isRunning();
        waveHeader.setGradientAngle(45);
        waveHeader.setWaveHeight(40);

        clearimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //해당 프레임 다 안 보이게 해야 함.
                if(getActivity() instanceof NewPickMusic){
                    NewPickMusic newPickMusic = (NewPickMusic) getActivity();
                    newPickMusic.blindFrame();
                }
            }
        });
        return view;
    }
}