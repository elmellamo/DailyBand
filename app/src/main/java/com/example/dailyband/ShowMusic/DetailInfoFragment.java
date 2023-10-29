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
import androidx.fragment.app.FragmentTransaction;

import com.example.dailyband.Library.MyCollect;
import com.example.dailyband.Library.MyLove;
import com.example.dailyband.R;

public class DetailInfoFragment extends Fragment {

    private View view;
    private ConstraintLayout downloadlayout, artistlayout, creditlayout;
    private boolean isLiked;
    private String title, artist;
    private ImageView heartbtn;
    private TextView songtitle, songwriter;
    public DetailInfoFragment() {
    }

    public void setDetailInfo(boolean isLiked, String title, String artist){
        this.isLiked = isLiked;
        this.title = title;
        this.artist = artist;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.detail_info, container, false);

        downloadlayout = view.findViewById(R.id.downloadlayout);
        artistlayout = view.findViewById(R.id.artistlayout);
        creditlayout = view.findViewById(R.id.creditlayout);

        heartbtn = view.findViewById(R.id.heartbtn);
        songtitle = view.findViewById(R.id.songtitle);
        songwriter = view.findViewById(R.id.songwriter);

        if(isLiked){
            heartbtn.setImageResource(R.drawable.dark_heart_full);
        }else{
            heartbtn.setImageResource(R.drawable.dark_heart_empty);
        }

        songtitle.setText(title);
        songwriter.setText(artist);

        downloadlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //다운로드 할 수 있게 해야 한다!!
            }
        });
        artistlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //완전 새로운 액티비티를 열어야 한다.
            }
        });
        creditlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //해당 액티비티 관련 인포 뜨게 만들어야 한다.
                if(getActivity() instanceof PickMusic){
                    PickMusic pickMusic = (PickMusic) getActivity();
                    pickMusic.showUpInfo();
                }
            }
        });
        return view;
    }
}
