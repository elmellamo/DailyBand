package com.example.dailyband.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dailyband.Models.TestSong;
import com.example.dailyband.R;
import com.example.dailyband.ShowMusic.NewPickMusic;
import com.example.dailyband.Utils.PopUpClickListener;

import java.util.List;
public class PopUp_New_PopularAdapter extends RecyclerView.Adapter<PopUp_New_PopularAdapter.PopUp_New_PopularViewHolder> {
    private Context context;
    private List<TestSong> songs;
    private int previousselectednum = -1;
    private PopUpClickListener clickListener;
    private RecyclerView recyclerView;
    private int selectedPosition = RecyclerView.NO_POSITION;

    public PopUp_New_PopularAdapter(Context context, List<TestSong> songs, PopUpClickListener clickListener, RecyclerView recyclerView) {
        this.context = context;
        this.songs = songs;
        this.clickListener = clickListener;
        this.recyclerView = recyclerView;
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

        if (selectedPosition == position) {
            // 선택된 아이템인 경우 UI 업데이트
            holder.btn_expand_toggle.setImageResource(R.drawable.arrow_up);
            holder.recyclercontainer.setBackgroundColor(context.getResources().getColor(R.color.selectedname));
        } else {
            // 선택되지 않은 아이템인 경우 UI 업데이트
            holder.btn_expand_toggle.setImageResource(R.drawable.arrow_below);
            holder.recyclercontainer.setBackgroundColor(context.getResources().getColor(R.color.white));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int oldSelectedPosition = selectedPosition;
                selectedPosition = holder.getAbsoluteAdapterPosition();

                // 이전 선택 위치와 현재 선택 위치에 대한 UI 업데이트
                notifyItemChanged(oldSelectedPosition);
                notifyItemChanged(selectedPosition);

                if (position != RecyclerView.NO_POSITION) {
                    TestSong selectedSong = songs.get(position);
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
        public ImageView btn_expand_toggle;
        public ConstraintLayout recyclercontainer;

        public PopUp_New_PopularViewHolder(@NonNull View itemView) {
            super(itemView);
            numRanking = itemView.findViewById(R.id.numRanking);
            songName = itemView.findViewById(R.id.songname);
            btn_expand_toggle = itemView.findViewById(R.id.btn_expand_toggle);
            recyclercontainer = itemView.findViewById(R.id.recyclercontainer);
            //여기 click 넣어야 함 아이템 마다
        }
    }
}
