package com.example.dailyband.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dailyband.Models.TestSong;
import com.example.dailyband.R;
import com.example.dailyband.ShowMusic.NewPickMusic;
import com.example.dailyband.Utils.PopUpClickListener;

import java.util.List;
public class PopUp_New_PopularAdapter extends RecyclerView.Adapter<PopUp_New_PopularAdapter.PopUp_New_PopularViewHolder> {
    private Context context;
    private List<TestSong> songs;

    private PopUpClickListener clickListener;

    public PopUp_New_PopularAdapter(Context context, List<TestSong> songs, PopUpClickListener clickListener) {
        this.context = context;
        this.songs = songs;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public PopUp_New_PopularAdapter.PopUp_New_PopularViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.pick_popular_head, parent, false);
        PopUp_New_PopularViewHolder holder = new PopUp_New_PopularViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull PopUp_New_PopularAdapter.PopUp_New_PopularViewHolder holder, final int position) {
        TestSong song = songs.get(position);
        holder.numRanking.setText(String.valueOf(position + 1));
        holder.songName.setText(song.getTitle());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int adapterPosition = holder.getAbsoluteAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    TestSong selectedSong = songs.get(adapterPosition);

                    //여기에서 selectedSong 객체를 해당 어댑터가 있는 어댑터에 정보를 보내고 싶어
                    if(clickListener !=null){
                        clickListener.onPopUpItemClicked(selectedSong.getPost_id(), selectedSong.getTitle());
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return (null !=songs ? songs.size() : 0);
    }

    public class PopUp_New_PopularViewHolder extends RecyclerView.ViewHolder {
        public TextView numRanking;
        public TextView songName;

        public PopUp_New_PopularViewHolder(@NonNull View itemView) {
            super(itemView);
            numRanking = itemView.findViewById(R.id.numRanking);
            songName = itemView.findViewById(R.id.songname);
            //여기 click 넣어야 함 아이템 마다
        }
    }
}
