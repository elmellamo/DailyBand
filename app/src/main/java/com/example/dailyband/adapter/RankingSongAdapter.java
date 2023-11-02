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

import java.util.List;

public class RankingSongAdapter extends RecyclerView.Adapter<RankingSongAdapter.RankingViewHolder> {
    private Context context;
    private List<TestSong> songs;

    public RankingSongAdapter(Context context, List<TestSong> songs) {
        this.context = context;
        this.songs = songs;
    }

    @NonNull
    @Override
    public RankingSongAdapter.RankingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.popular_item, parent, false);
        RankingViewHolder holder = new RankingViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RankingSongAdapter.RankingViewHolder holder, final int position) {
        TestSong song = songs.get(position);
        holder.numRanking.setText(String.valueOf(position + 1));
        holder.songName.setText(song.getTitle());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int adapterPosition = holder.getAbsoluteAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    TestSong selectedSong = songs.get(adapterPosition);
                    Intent intent = new Intent(context, NewPickMusic.class);
                    intent.putExtra("selectedSong", selectedSong);

                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return (null !=songs ? songs.size() : 0);
    }

    public class RankingViewHolder extends RecyclerView.ViewHolder {
        public TextView numRanking;
        public TextView songName;

        public RankingViewHolder(@NonNull View itemView) {
            super(itemView);
            numRanking = itemView.findViewById(R.id.numRanking);
            songName = itemView.findViewById(R.id.songname);
            //여기 click 넣어야 함 아이템 마다
        }
    }
}
