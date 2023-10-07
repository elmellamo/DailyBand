package com.example.dailyband.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dailyband.Models.ComplexName;
import com.example.dailyband.PickMusic;
import com.example.dailyband.R;

import java.util.List;

public class CollabAdapter extends RecyclerView.Adapter<CollabAdapter.CollabViewHolder> {
    private Context context;
    private List<ComplexName> songs;

    public CollabAdapter(Context context, List<ComplexName> songs) {
        this.context = context;
        this.songs = songs;
    }

    @NonNull
    @Override
    public CollabAdapter.CollabViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.popular_item, parent, false);
        CollabViewHolder holder = new CollabViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CollabAdapter.CollabViewHolder holder, final int position) {
        ComplexName song = songs.get(position);
        holder.numRanking.setText(String.valueOf(position + 1));
        holder.songName.setText(song.getTitle());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int adapterPosition = holder.getAbsoluteAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    ComplexName selectedSong = songs.get(adapterPosition);
                    Intent intent = new Intent(context, PickMusic.class);
                    intent.putExtra("postId", selectedSong.getSongid());
                    intent.putExtra("title", selectedSong.getTitle());
                    intent.putExtra("user_id", selectedSong.getWriteruid()); //해당 노래를 작곡한 사람
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return (null !=songs ? songs.size() : 0);
    }

    public class CollabViewHolder extends RecyclerView.ViewHolder {
        public TextView numRanking;
        public TextView songName;

        public CollabViewHolder(@NonNull View itemView) {
            super(itemView);
            numRanking = itemView.findViewById(R.id.numRanking);
            songName = itemView.findViewById(R.id.songname);
            //여기 click 넣어야 함 아이템 마다
        }
    }
}
