package com.example.dailyband.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ToggleButton;

import androidx.recyclerview.widget.RecyclerView;

import com.example.dailyband.MusicAdd.AddMusic;
import com.example.dailyband.R;

import java.util.List;

public class MusicTrackAdapter extends RecyclerView.Adapter<MusicTrackAdapter.TrackViewHolder>{

//    private List<String> items;
//    public List<Boolean> isSpeaking;
    private List<AddMusic.MusicTrack> tracks;

    public static class TrackViewHolder extends RecyclerView.ViewHolder {
        public final EditText tv_path;
        public final ToggleButton mute_btn;
        public final ImageButton closebtn;

        public TrackViewHolder(View itemView) {
            super(itemView);

            tv_path = itemView.findViewById(R.id.path_text);
            mute_btn = itemView.findViewById(R.id.mutebtn);
            closebtn = itemView.findViewById(R.id.closebtn);
        }
    }

    public MusicTrackAdapter(List<AddMusic.MusicTrack> tracks) {
        this.tracks = tracks;
    }


    @Override
    public TrackViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.track_item, viewGroup, false);

        return new TrackViewHolder(view);
    }


    @Override
    public void onBindViewHolder(TrackViewHolder holder, int position) {
        holder.tv_path.setText(tracks.get(position).title);

        holder.mute_btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean status) {
                //isSpeaking.set( holder.getAdapterPosition() , status);
                //isSpeaking.set( position , status);
                tracks.get( holder.getBindingAdapterPosition()).isSpeaking = status;

                //Log.d("뷰홀더","위치"+holder.getAdapterPosition()+"상태바뀜"+status);
            }
        });

        holder.closebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getBindingAdapterPosition();
                Log.d("asdf", "아이템 제거 : "+pos);
                tracks.remove( pos );
                notifyItemRemoved(pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.tracks.size();
    }

}
