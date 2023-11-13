package com.example.dailyband.OcarinaTest;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ToggleButton;

import androidx.fragment.app.Fragment;

import com.example.dailyband.MusicAdd.AddMusic;
import com.example.dailyband.R;

public class Ocarina12HoleFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container != null) {
            container.removeAllViews();
        }

        return inflater.inflate(R.layout.ocarina_12_hole, container, false);
    }
    public void onStart() {
        super.onStart();
        ImageButton b1 = (ImageButton) getActivity().findViewById(R.id.button1);
        ImageButton b2 = (ImageButton) getActivity().findViewById(R.id.button2);
        ImageButton b3 = (ImageButton) getActivity().findViewById(R.id.button3);
        ImageButton b4 = (ImageButton) getActivity().findViewById(R.id.button4);
        ImageButton b5 = (ImageButton) getActivity().findViewById(R.id.button5);
        ImageButton b6 = (ImageButton) getActivity().findViewById(R.id.button6);
        ImageButton b7 = (ImageButton) getActivity().findViewById(R.id.button7);
        ImageButton b8 = (ImageButton) getActivity().findViewById(R.id.button8);
        //Buttons 9 and 10 are volume buttons
        ImageButton b11 = (ImageButton) getActivity().findViewById(R.id.button11);
        ImageButton b12 = (ImageButton) getActivity().findViewById(R.id.button12);

        ToggleButton volLock = (ToggleButton) getActivity().findViewById(R.id.vol_lock);

        volLock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    AddMusic.setVolumeLock(true);
                } else {
                    AddMusic.setVolumeLock(false);
                }
            }
        });

        b1.setOnTouchListener(AddMusic.getTouchListener());
        b2.setOnTouchListener(AddMusic.getTouchListener());
        b3.setOnTouchListener(AddMusic.getTouchListener());
        b4.setOnTouchListener(AddMusic.getTouchListener());
        b5.setOnTouchListener(AddMusic.getTouchListener());
        b6.setOnTouchListener(AddMusic.getTouchListener());
        b7.setOnTouchListener(AddMusic.getTouchListener());
        b8.setOnTouchListener(AddMusic.getTouchListener());
        //Buttons 9 and 10 are volume buttons
        b11.setOnTouchListener(AddMusic.getTouchListener());
        b12.setOnTouchListener(AddMusic.getTouchListener());

    }

}
