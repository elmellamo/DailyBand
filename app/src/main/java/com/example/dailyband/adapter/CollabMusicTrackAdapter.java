package com.example.dailyband.adapter;

        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.CompoundButton;
        import android.widget.EditText;
        import android.widget.ToggleButton;

        import androidx.recyclerview.widget.RecyclerView;

        import com.example.dailyband.MusicAdd.AddMusic;
        import com.example.dailyband.MusicAdd.CollabAddMusic;
        import com.example.dailyband.R;

        import java.util.List;

public class CollabMusicTrackAdapter extends RecyclerView.Adapter<CollabMusicTrackAdapter.TrackViewHolder>{

    //    private List<String> items;
//    public List<Boolean> isSpeaking;
    private List<CollabAddMusic.CollabMusicTrack> tracks;

    public static class TrackViewHolder extends RecyclerView.ViewHolder {
        public final EditText tv_path;
        public final ToggleButton mute_btn;

        public TrackViewHolder(View itemView) {
            super(itemView);

            tv_path = itemView.findViewById(R.id.path_text);
            mute_btn = itemView.findViewById(R.id.mutebtn);
        }
    }

    public CollabMusicTrackAdapter(List<CollabAddMusic.CollabMusicTrack> tracks) {
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
    }

    @Override
    public int getItemCount() {
        return this.tracks.size();
    }

    // 아이템 클릭시 이벤트리스너
//    private OnItemClickEventListener mItemClickListener;
//
//    public interface OnItemClickEventListener {
//        void onItemClick(View a_view, int a_position);
//    }
//    public void setOnItemClickListener(OnItemClickEventListener a_listener) {
//        mItemClickListener = a_listener;
//    }

}
