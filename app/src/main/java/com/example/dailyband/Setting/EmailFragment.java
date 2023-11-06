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
public class EmailFragment extends Fragment {

    private View view;
    EditText email_editText, pw_editText;
    Button change_btn;
    public EmailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.cardview_email, container, false);


        email_editText = view.findViewById(R.id.email_cardview_edittext);
        pw_editText = view.findViewById(R.id.email_pw_cardview_edittext);
        change_btn = view.findViewById(R.id.change_email_btn);

        return view;
    }
}