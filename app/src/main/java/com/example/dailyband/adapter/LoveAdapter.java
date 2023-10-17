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
import com.example.dailyband.Models.TestSong;
        import com.example.dailyband.ShowMusic.PickMusic;
        import com.example.dailyband.R;

        import java.util.List;

public class LoveAdapter extends RecyclerView.Adapter<LoveAdapter.LoveViewHolder> {
    private Context context;
    private List<ComplexName> songs;

    public LoveAdapter(Context context, List<ComplexName> songs) {
        this.context = context;
        this.songs = songs;
    }

    @NonNull
    @Override
    public LoveAdapter.LoveViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.love_item, parent, false);
        LoveViewHolder holder = new LoveViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull LoveAdapter.LoveViewHolder holder, final int position) {
        ComplexName song = songs.get(position);
        holder.songName.setText(song.getTitle());

        holder.songName.setOnClickListener(new View.OnClickListener() {
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

    public class LoveViewHolder extends RecyclerView.ViewHolder {
        public TextView songName;

        public LoveViewHolder(@NonNull View itemView) {
            super(itemView);
            songName = itemView.findViewById(R.id.songname);
            //여기 click 넣어야 함 아이템 마다
        }
    }
}
