package com.example.dailyband.MusicFragment;

import android.graphics.Color;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.dailyband.MusicAdd.AddMusic;
import com.example.dailyband.R;
public class DrumFragment extends Fragment implements View.OnClickListener{
    private View view;
    private ConstraintLayout basslayout, crash_cymbol_cardview, mid_tom_cardview, ride_cymbal_cardview,
            smare_cardview, high_tom_cardview, low_tom_cardview, open_hihat_layout, close_hihat_layout;
    private int bass, crash_cymbal, low_tom, close_hi_hat, open_hi_hat, mid_tom, snare, ride_cymbal,
    high_tom;


    private SoundPool soundPool;

    public DrumFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.drum_main, container, false);
        basslayout = view.findViewById(R.id.basslayout);
        crash_cymbol_cardview = view.findViewById(R.id.crash_cymbol_cardview);
        mid_tom_cardview = view.findViewById(R.id.mid_tom_cardview);
        ride_cymbal_cardview = view.findViewById(R.id.ride_cymbal_cardview);
        smare_cardview = view.findViewById(R.id.smare_cardview);
        high_tom_cardview = view.findViewById(R.id.high_tom_cardview);
        low_tom_cardview = view.findViewById(R.id.low_tom_cardview);
        open_hihat_layout = view.findViewById(R.id.open_hihat_layout);
        close_hihat_layout = view.findViewById(R.id.close_hihat_layout);

        intilizeAllDrum();
        soundPool = new SoundPool.Builder().setMaxStreams(6).build();

        bass = soundPool.load(requireContext(), R.raw.bass, 1);
        crash_cymbal = soundPool.load(requireContext(), R.raw.crash_cymbal, 1);
        low_tom = soundPool.load(requireContext(), R.raw.low_tom, 1);
        close_hi_hat = soundPool.load(requireContext(), R.raw.close_hi_hat, 1);
        open_hi_hat = soundPool.load(requireContext(), R.raw.open_hi_hat, 1);
        mid_tom = soundPool.load(requireContext(), R.raw.mid_tom, 1);
        snare = soundPool.load(requireContext(), R.raw.snare, 1);
        ride_cymbal = soundPool.load(requireContext(), R.raw.ride_cymbal, 1);
        high_tom = soundPool.load(requireContext(), R.raw.high_tom, 1);

        return view;
    }

    void intilizeAllDrum(){
        crash_cymbol_cardview.setOnClickListener(this);
        mid_tom_cardview.setOnClickListener(this);
        ride_cymbal_cardview.setOnClickListener(this);
        smare_cardview.setOnClickListener(this);
        high_tom_cardview.setOnClickListener(this);
        low_tom_cardview.setOnClickListener(this);
        open_hihat_layout.setOnClickListener(this);
        close_hihat_layout.setOnClickListener(this);
        basslayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id==R.id.basslayout) {
            soundPool.play(bass, 1, 1, 0, 0, 1);
        }else if(id==R.id.mid_tom_cardview){
            soundPool.play(mid_tom, 1, 1, 0, 0, 1);
        }else if(id==R.id.ride_cymbal_cardview){
            soundPool.play(ride_cymbal, 1, 1, 0, 0, 1);
        }else if(id==R.id.smare_cardview){
            soundPool.play(snare, 1, 1, 0, 0, 1);
        }else if(id==R.id.high_tom_cardview){
            soundPool.play(high_tom, 1, 1, 0, 0, 1);
        }else if(id==R.id.low_tom_cardview){
            soundPool.play(low_tom, 1, 1, 0, 0, 1);
        }else if(id==R.id.open_hihat_layout){
            soundPool.play(open_hi_hat, 1, 1, 0, 0, 1);
        }else if(id==R.id.close_hihat_layout){
            soundPool.play(close_hi_hat, 1, 1, 0, 0, 1);
        }else if(id==R.id.crash_cymbol_cardview){
            soundPool.play(crash_cymbal, 1, 1, 0, 0, 1);
        }
    }
}
