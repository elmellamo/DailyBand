package com.example.dailyband.MusicFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.dailyband.MusicAdd.AddMusic;
import com.example.dailyband.MusicAdd.CollabAddMusic;
import com.example.dailyband.R;

public class CategoryAddMusic extends Fragment {
    private View view;
    private ImageView clearbtn;
    private ConstraintLayout keyslayout, drumlayout, importlayout, voicelayout, popularlayout;
    public CategoryAddMusic() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.category_add_music, container, false);
        keyslayout = view.findViewById(R.id.keyslayout);
        clearbtn = view.findViewById(R.id.clearbtn);
        drumlayout = view.findViewById(R.id.drumlayout);
        importlayout = view.findViewById(R.id.importlayout);
        voicelayout = view.findViewById(R.id.voicelayout);
        popularlayout = view.findViewById(R.id.popularlayout);

        popularlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getActivity() instanceof AddMusic){
                    AddMusic addmusic = (AddMusic) getActivity();
                    addmusic.hideAddCategoryFrameLayout();
                    //그 다음에 아래와 같은 동작을 하게 만들어야 한다. 디테일 나와야한다...
                    addmusic.showUpPopular();
                }else if(getActivity() instanceof CollabAddMusic){
                    CollabAddMusic collabAddMusic = (CollabAddMusic) getActivity();
                    collabAddMusic.CollabhideAddCategoryFrameLayout();
                    //그 다음에 아래와 같은 동작을 하게 만들어야 한다. 디테일 나와야한다...
                    collabAddMusic.CollabshowUpPopular();
                }
            }
        });

        voicelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getActivity() instanceof AddMusic){
                    AddMusic addmusic = (AddMusic) getActivity();
                    addmusic.hideAddCategoryFrameLayout();
                    //그 다음에 아래와 같은 동작을 하게 만들어야 한다. 디테일 나와야한다...
                    addmusic.showUpRecording();
                }else if(getActivity() instanceof CollabAddMusic){
                    CollabAddMusic collabAddMusic = (CollabAddMusic) getActivity();
                    collabAddMusic.CollabhideAddCategoryFrameLayout();
                    //그 다음에 아래와 같은 동작을 하게 만들어야 한다. 디테일 나와야한다...
                    collabAddMusic.CollabshowUpRecording();
                }
            }
        });
        clearbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //AddMusic 액티비티의 addCategoryFrameLayout을 숨김
                if(getActivity() instanceof AddMusic){
                    AddMusic addmusic = (AddMusic) getActivity();
                    addmusic.hideAddCategoryFrameLayout();
                }else if(getActivity() instanceof CollabAddMusic){
                    CollabAddMusic collabAddMusic = (CollabAddMusic) getActivity();
                    collabAddMusic.CollabhideAddCategoryFrameLayout();
                    //그 다음에 아래와 같은 동작을 하게 만들어야 한다. 디테일 나와야한다...
                }
            }
        });
        keyslayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //AddMusic 액티비티의 addCategoryFrameLayout을 숨김
                if(getActivity() instanceof AddMusic){
                    AddMusic addmusic = (AddMusic) getActivity();
                    addmusic.hideAddCategoryFrameLayout();
                    //그 다음에 아래와 같은 동작을 하게 만들어야 한다. 디테일 나와야한다...
                    addmusic.showUpPiano();
                }else if(getActivity() instanceof CollabAddMusic){
                    CollabAddMusic collabAddMusic = (CollabAddMusic) getActivity();
                    collabAddMusic.CollabhideAddCategoryFrameLayout();
                    collabAddMusic.CollabshowUpPiano();
                    //그 다음에 아래와 같은 동작을 하게 만들어야 한다. 디테일 나와야한다...
                }
            }
        });
        drumlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getActivity() instanceof AddMusic){
                    AddMusic addmusic = (AddMusic) getActivity();
                    addmusic.hideAddCategoryFrameLayout();
                    //그 다음에 아래와 같은 동작을 하게 만들어야 한다. 디테일 나와야한다...
                    addmusic.showUpDrum();
                }else if(getActivity() instanceof CollabAddMusic){
                    CollabAddMusic collabAddMusic = (CollabAddMusic) getActivity();
                    collabAddMusic.CollabhideAddCategoryFrameLayout();
                    collabAddMusic.CollabshowUpDrum();
                    //그 다음에 아래와 같은 동작을 하게 만들어야 한다. 디테일 나와야한다...
                }
            }
        });
        importlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getActivity() instanceof AddMusic){
                    AddMusic addmusic = (AddMusic) getActivity();
                    addmusic.hideAddCategoryFrameLayout();
                    //그 다음에 아래와 같은 동작을 하게 만들어야 한다. 디테일 나와야한다...
                    addmusic.getPathFromStorage();
                }else if(getActivity() instanceof CollabAddMusic){
                    CollabAddMusic collabAddMusic = (CollabAddMusic) getActivity();
                    collabAddMusic.CollabhideAddCategoryFrameLayout();
                    collabAddMusic.CollabgetPathFromStorage();
                    //그 다음에 아래와 같은 동작을 하게 만들어야 한다. 디테일 나와야한다...
                }
            }
        });

        return view;
    }
}
