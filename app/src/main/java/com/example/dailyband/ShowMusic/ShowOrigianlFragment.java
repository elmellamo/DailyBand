package com.example.dailyband.ShowMusic;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dailyband.Models.ComplexName;
import com.example.dailyband.R;
import com.example.dailyband.adapter.CollabAdapter;
import com.example.dailyband.adapter.ParentAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.scwang.wave.MultiWaveHeader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShowOrigianlFragment extends Fragment {
    private MultiWaveHeader waveHeader;
    private View view;
    private ImageView clearimg;
    private RecyclerView collaborationlist;
    private LinearLayout emptytxt;
    private String postId;
    public ShowOrigianlFragment() {
    }

    public void setOriginalInfo(String postId){
        this.postId = postId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.show_collaboration_parent, container, false);

        clearimg = view.findViewById(R.id.clearimg);
        collaborationlist = view.findViewById(R.id.collaborationlist);
        emptytxt = view.findViewById(R.id.emptytxt);

        waveHeader = view.findViewById(R.id.wave_header);
        waveHeader.setVelocity(1);
        waveHeader.setProgress(1);
        waveHeader.isRunning();
        waveHeader.setGradientAngle(45);
        waveHeader.setWaveHeight(40);

        collaborationlist.setLayoutManager(new LinearLayoutManager(getContext()));
        getCollab(postId);
        clearimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //해당 프레임 다 안 보이게 해야 함.
                if(getActivity() instanceof NewPickMusic){
                    NewPickMusic newPickMusic = (NewPickMusic) getActivity();
                    newPickMusic.blindFrame();
                }
            }
        });


        return view;
    }

    private void getCollab(String postId){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        List<ComplexName> collabsongs = new ArrayList<>();
        DatabaseReference collabRef = databaseReference.child("my_parents").child(postId);
        collabRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                collabsongs.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Map<String, Object> objectMap = (HashMap<String, Object>) dataSnapshot.getValue();
                    ComplexName song = new ComplexName();
                    song.setWriteruid(objectMap.get("writeruid").toString());
                    song.setTitle(objectMap.get("title").toString());
                    song.setSongid(objectMap.get("songid").toString());
                    collabsongs.add(song);
                }

                ParentAdapter adapter = new ParentAdapter(getContext(), collabsongs);

                adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                    @Override
                    public void onChanged() {
                        super.onChanged();
                        checkEmpty();
                    }

                    void checkEmpty(){
                        emptytxt.setVisibility(adapter.getItemCount()==0?View.VISIBLE:View.GONE);
                    }
                });

                collaborationlist.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}