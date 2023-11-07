package com.example.dailyband.ShowMusic;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.example.dailyband.MusicAdd.CollabAddMusic;
import com.example.dailyband.R;

public class DetailInfoFragment extends Fragment {

    private View view;
    private ConstraintLayout downloadlayout, artistlayout, creditlayout, collablayout;
    private boolean isLiked;
    private String title, artist, postId, userUid;
    private ImageView heartbtn;
    private TextView songtitle, songwriter;
    public DetailInfoFragment() {
    }

    public void setDetailInfo(boolean isLiked, String title, String artist, String postId, String userUid){
        this.isLiked = isLiked;
        this.title = title;
        this.artist = artist;
        this.postId = postId;
        this.userUid = userUid;
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
        collablayout = view.findViewById(R.id.collablayout);

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
                //ArtistInfo 열어야함, intent 보내면서
                Intent intent = new Intent(getActivity(), ArtistInfo.class);
                //isLiked, title, artist, postId;

                //intent.putExtra("isLiked_intent", isLiked);
                intent.putExtra("title_intent", title);
                intent.putExtra("artist_intent", artist);
                intent.putExtra("postId_intent", postId);
                startActivity(intent);
                getActivity().finish();
            }
        });
        creditlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //해당 액티비티 관련 인포 뜨게 만들어야 한다.
                if(getActivity() instanceof NewPickMusic){
                    NewPickMusic newPickMusic = (NewPickMusic) getActivity();
                    newPickMusic.showUpInfo();
                }
            }
        });
        collablayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CollabAddMusic.class);

                // 정보를 전달하기 위해 Intent에 데이터 추가
                intent.putExtra("parent_Id", postId); // 원하는 정보의 키와 값을 넣어주세요
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.e("로그", "프레그먼트에서 좋아요 확인 >>>"+isLiked );
        // isLiked 값에 따라 heartbtn의 이미지 설정
        if (isLiked) {
            heartbtn.setImageResource(R.drawable.dark_heart_full);
        } else {
            heartbtn.setImageResource(R.drawable.dark_heart_empty);
        }
        // 기타 로직 수행
    }
}