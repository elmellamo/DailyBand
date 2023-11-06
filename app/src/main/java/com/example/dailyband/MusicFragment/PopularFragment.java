package com.example.dailyband.MusicFragment;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import static com.example.dailyband.Utils.wavClass.PERMISSION_REQUEST_CODE;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dailyband.Home.HomeMain;
import com.example.dailyband.Models.TestSong;
import com.example.dailyband.MusicAdd.AddMusic;
import com.example.dailyband.MusicAdd.CollabAddMusic;
import com.example.dailyband.R;
import com.example.dailyband.Utils.FirebaseMethods;
import com.example.dailyband.Utils.OnRecordingCompletedListener;
import com.example.dailyband.Utils.wavClass;
import com.example.dailyband.adapter.PPExpandableListAdapter;
import com.example.dailyband.adapter.RankingSongAdapter;
import com.github.squti.androidwaverecorder.WaveConfig;
import com.github.squti.androidwaverecorder.WaveRecorder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
public class PopularFragment extends Fragment {
    private ExpandableListView expandableListView;
    private PPExpandableListAdapter adapter;
    private ArrayList<TestSong> songs;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;
    private FirebaseAuth mAuth;
    public PopularFragment() {
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.popup_popularmusic, container, false);
        mFirebaseMethods = new FirebaseMethods(getContext());
        // 리사이클러뷰 초기화
        expandableListView = view.findViewById(R.id.expandableListView);
        songs = new ArrayList<>();

        getSongs();

        return view;
    }

    private void getSongs(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("songs");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                songs.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    TestSong song = snapshot.getValue(TestSong.class);
                    if (song != null) {
                        songs.add(song);
                    }
                }

                // songs 리스트를 love를 기준으로 내림차순으로 정렬하고, love가 같을 경우에는 시간을 기준으로 내림차순으로 정렬
                Collections.sort(songs, new Comparator<TestSong>() {
                    @Override
                    public int compare(TestSong song1, TestSong song2) {
                        // love가 큰 순서대로 정렬
                        int loveComparison = Integer.compare(song2.getLove(), song1.getLove());
                        if (loveComparison != 0) {
                            return loveComparison;
                        }
                        // love가 같은 경우 시간을 큰 순서대로 정렬
                        return song2.getDate_created().compareTo(song1.getDate_created());
                    }
                });
                adapter = new PPExpandableListAdapter(songs, getContext());
                expandableListView.setAdapter(adapter);
                /*
                expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                    @Override
                    public boolean onGroupClick(ExpandableListView parent, View view, int groupPosition, long l) {
                        setListVieweHight(parent, groupPosition);
                        return false;
                    }
                });
                //pprecyclerview.setAdapter(adapter);
                 */
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 데이터를 가져오는데 실패한 경우 처리하는 코드
                Toast.makeText(getContext(), "인기 순위를 불러오는 것에 실패하였습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setListVieweHight(ExpandableListView listView, int group) {
        PPExpandableListAdapter listAdapter = (PPExpandableListAdapter) listView.getExpandableListAdapter();
        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.EXACTLY);

        for (int i = 0; i < listAdapter.getGroupCount(); i++) {
            View groupItem = listAdapter.getGroupView(i, false, null, listView);
            groupItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += groupItem.getMeasuredHeight();

            if ((listView.isGroupExpanded(i) && i != group) || (!listView.isGroupExpanded(i) && i == group)) {
                for (int j = 0; j < listAdapter.getChildrenCount(i); j++) {
                    View listItem = listAdapter.getChildView(i, j, false, null, listView);
                    listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                    totalHeight += listItem.getMeasuredHeight();
                }
            }
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        int height = totalHeight + listView.getDividerHeight() * (listAdapter.getGroupCount() - 1);
        if (height < 10) {
            height = 200;
        }
        params.height = height;
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
}
