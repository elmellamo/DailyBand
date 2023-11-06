package com.example.dailyband.Setting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.dailyband.R;
public class NameFragment extends Fragment {

    private View view;
    EditText editText;
    Button change_btn;
    public NameFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.cardview_name, container, false);


        editText = view.findViewById(R.id.name_cardview_edittext);
        change_btn = view.findViewById(R.id.change_name_btn);

        return view;
    }
}