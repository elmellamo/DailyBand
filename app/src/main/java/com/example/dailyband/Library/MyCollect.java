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

import com.example.dailyband.Models.TestSong;
import com.example.dailyband.R;
import com.example.dailyband.Utils.FirebaseMethods;
import com.example.dailyband.adapter.MySongAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyCollect extends Fragment {
    private View view;
    private RecyclerView recyclerView;
    private MySongAdapter adapter;
    private List<TestSong> songs;
    private FirebaseMethods mFirebaseMethods;
    private LinearLayout emptytxt;

    public MyCollect() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.mycollection_layout, container, false);

        mFirebaseMethods = new FirebaseMethods(getActivity());
        recyclerView = view.findViewById(R.id.mycollectionlist);
        emptytxt = view.findViewById(R.id.emptytxt);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        songs = new ArrayList<>();
        getMySong();

        return view;
    }

    private void getMySong(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        String myUID = mFirebaseMethods.getMyUid();
        DatabaseReference mysongRef = databaseReference.child("user_songs").child(myUID);
        mysongRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    songs.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Map<String, Object> objectMap = (HashMap<String, Object>) snapshot.getValue();
                        TestSong song = new TestSong();
                        song.setLove(Integer.parseInt(objectMap.get("love").toString()));
                        song.setDate_created(objectMap.get("date_created").toString());
                        song.setTitle(objectMap.get("title").toString());
                        song.setPost_id(objectMap.get("post_id").toString());
                        song.setUser_id(objectMap.get("user_id").toString());
                        songs.add(song);
                    }

                    adapter = new MySongAdapter(getActivity(), songs);
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
