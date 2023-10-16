package com.example.dailyband.MusicFragment;

import android.graphics.Color;
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
public class DrumFragment extends Fragment {
    private View view;
    private ImageView clearbtn;
    private ConstraintLayout keyslayout;
    CardView crash_cymbol_cardview, mid_tom_cardview, ride_cymbal_cardview,smare_cardview,
            high_tom_cardview, low_tom_cardview, open_hihat_layout, close_hihat_layout;

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

        crash_cymbol_cardview = view.findViewById(R.id.crash_cymbol_cardview);
        mid_tom_cardview = view.findViewById(R.id.mid_tom_cardview);
        ride_cymbal_cardview = view.findViewById(R.id.ride_cymbal_cardview);
        smare_cardview = view.findViewById(R.id.smare_cardview);
        high_tom_cardview = view.findViewById(R.id.high_tom_cardview);
        low_tom_cardview = view.findViewById(R.id.low_tom_cardview);
        open_hihat_layout = view.findViewById(R.id.open_hihat_layout);
        close_hihat_layout = view.findViewById(R.id.close_hihat_layout);

        crash_cymbol_cardview.setCardBackgroundColor(Color.parseColor("#99A98F"));
        mid_tom_cardview.setCardBackgroundColor(Color.parseColor("#99A98F"));
        ride_cymbal_cardview.setCardBackgroundColor(Color.parseColor("#99A98F"));
        smare_cardview.setCardBackgroundColor(Color.parseColor("#99A98F"));
        high_tom_cardview.setCardBackgroundColor(Color.parseColor("#99A98F"));
        low_tom_cardview.setCardBackgroundColor(Color.parseColor("#99A98F"));
        open_hihat_layout.setCardBackgroundColor(Color.parseColor("#99A98F"));
        close_hihat_layout.setCardBackgroundColor(Color.parseColor("#99A98F"));
        return view;
    }
}
