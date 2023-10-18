package com.example.dailyband.Library;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.dailyband.MusicAdd.AddMusic;
import com.example.dailyband.R;

public class SelectLibraryFragment extends Fragment{

    private View view;
    private ConstraintLayout lovelayout, writelayout;
    public SelectLibraryFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.select_library, container, false);

        lovelayout = view.findViewById(R.id.lovelayout);
        writelayout = view.findViewById(R.id.writelayout);

        lovelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.library_frame, new MyLove());
                transaction.addToBackStack(null); // 뒤로 가기 버튼으로 이전 프래그먼트로 이동할 수 있도록 스택에 추가
                transaction.commit();
            }
        });
        writelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.library_frame, new MyCollect());
                transaction.addToBackStack(null); // 뒤로 가기 버튼으로 이전 프래그먼트로 이동할 수 있도록 스택에 추가
                transaction.commit();
            }
        });
        return view;
    }
}
