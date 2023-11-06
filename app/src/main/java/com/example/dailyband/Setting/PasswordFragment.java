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
public class PasswordFragment extends Fragment {

    private View view;
    EditText prevpw, currpw, currpwchk;
    Button change_btn;
    public PasswordFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.cardview_password, container, false);

        prevpw = view.findViewById(R.id.prevpw_cardview_edittext);
        currpw = view.findViewById(R.id.currpw_cardview_edittext);
        currpwchk = view.findViewById(R.id.currpwchk_cardview_edittext);

        return view;
    }
}