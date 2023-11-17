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
import com.example.dailyband.R;

public class CategoryAddMusic extends Fragment {
    private View view;
    private ImageView clearbtn;
    private ConstraintLayout keyslayout, drumlayout, importlayout, voicelayout, popularlayout,ocarinalayout;
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
        ocarinalayout = view.findViewById(R.id.ocarinalayout);

        ocarinalayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getActivity() instanceof AddMusic){
                    AddMusic addmusic = (AddMusic) getActivity();
                    addmusic.hideAddCategoryFrameLayout();
                    addmusic.startOcarina();
                    addmusic.showUpOcarina();
                }
            }
        });

        popularlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getActivity() instanceof AddMusic){
                    AddMusic addmusic = (AddMusic) getActivity();
                    addmusic.hideAddCategoryFrameLayout();
                    addmusic.showUpPopular();
                }
            }
        });

        voicelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getActivity() instanceof AddMusic){
                    AddMusic addmusic = (AddMusic) getActivity();
                    addmusic.hideAddCategoryFrameLayout();
                    addmusic.showUpRecording();
                }
            }
        });
        clearbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //AddMusic 액티비티의 addCategoryFrameLayout을 숨김
                if(getActivity() instanceof AddMusic){
                    AddMusic addmusic = (AddMusic) getActivity();
                    addmusic.clearAddCategory();
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
                    addmusic.showUpPiano();
                }
            }
        });
        drumlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getActivity() instanceof AddMusic){
                    AddMusic addmusic = (AddMusic) getActivity();
                    addmusic.hideAddCategoryFrameLayout();
                    addmusic.showUpDrum();
                }
            }
        });
        importlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getActivity() instanceof AddMusic){
                    AddMusic addmusic = (AddMusic) getActivity();
                    addmusic.hideAddCategoryFrameLayout();
                    addmusic.getPathFromStorage();
                }
            }
        });

        return view;
    }
}
