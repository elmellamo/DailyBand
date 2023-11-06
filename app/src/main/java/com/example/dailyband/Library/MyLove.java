package com.example.dailyband.Library;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dailyband.Models.ComplexName;
import com.example.dailyband.R;
import com.example.dailyband.Utils.FirebaseMethods;
import com.example.dailyband.adapter.LoveAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyLove extends Fragment {
    private View view;
    private RecyclerView recyclerView;
    private LoveAdapter adapter;
    private List<ComplexName> songs;
    private FirebaseMethods mFirebaseMethods;
    private LinearLayout emptytxt;
    public MyLove() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.mylovemusic, container, false);


        mFirebaseMethods = new FirebaseMethods(getActivity());
        recyclerView = view.findViewById(R.id.lovelist);
        emptytxt = view.findViewById(R.id.emptytxt);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        songs = new ArrayList<>();
        getLove();

        return view;
    }

    private void getLove(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        String myUID = mFirebaseMethods.getMyUid();
        DatabaseReference loveRef = databaseReference.child("user_like").child(myUID);
        loveRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    songs.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Map<String, Object> objectMap = (HashMap<String, Object>) snapshot.getValue();
                        ComplexName song = new ComplexName();
                        song.setWriteruid(objectMap.get("writeruid").toString());
                        song.setTitle(objectMap.get("title").toString());
                        song.setSongid(objectMap.get("songid").toString());
                        songs.add(song);
                    }

                    adapter = new LoveAdapter(getActivity(), songs);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    checkEmpty();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 데이터를 가져오는데 실패한 경우 처리하는 코드
            }
        });
    }
    private void checkEmpty(){
        emptytxt.setVisibility(adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }
}
