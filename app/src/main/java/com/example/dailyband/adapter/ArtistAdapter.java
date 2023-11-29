package com.example.dailyband.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dailyband.Models.ComplexName;
import com.example.dailyband.Models.TestSong;
import com.example.dailyband.R;
import com.example.dailyband.ShowMusic.NewPickMusic;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

    public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder> {
        private Context context;
        private List<TestSong> songs;

        public ArtistAdapter(Context context, List<TestSong> songs) {
            this.context = context;
            this.songs = songs;
        }

        @NonNull
        @Override
        public ArtistAdapter.ArtistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.artist_song_item, parent, false);
            ArtistAdapter.ArtistViewHolder holder = new ArtistAdapter.ArtistViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull com.example.dailyband.adapter.ArtistAdapter.ArtistViewHolder holder, final int position) {
            TestSong song = songs.get(position);
            holder.songName.setText(song.getTitle());
            holder.lovenum.setText(String.valueOf(song.getLove()-1));

            // 받은 날짜 문자열을 Date 객체로 파싱
            SimpleDateFormat firebaseDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.KOREA);
            Date castDate = null;
            try {
                castDate = firebaseDateFormat.parse(song.getDate_created());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            // 새로운 날짜 포맷을 지정
            SimpleDateFormat desiredDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일", Locale.KOREA);
            // Date 객체를 새로운 포맷으로 변환
            String desiredDateString = desiredDateFormat.format(castDate);
            holder.whensong.setText(desiredDateString);


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

        public class ArtistViewHolder extends RecyclerView.ViewHolder {
            public TextView songName;
            public TextView whensong;
            public TextView lovenum;
            public ConstraintLayout containertxt;

            public ArtistViewHolder(@NonNull View itemView) {
                super(itemView);
                songName = itemView.findViewById(R.id.songname);
                whensong = itemView.findViewById(R.id.whensong);
                lovenum = itemView.findViewById(R.id.lovenum);
                containertxt = itemView.findViewById(R.id.containertxt);
                //여기 click 넣어야 함 아이템 마다
            }
        }
    }
