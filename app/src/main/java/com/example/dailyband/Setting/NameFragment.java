package com.example.dailyband.Setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.dailyband.R;
import com.example.dailyband.ShowMusic.NewPickMusic;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.jar.Attributes;

public class NameFragment extends Fragment {

    private View view;
    private DatabaseReference mDatabase;
    EditText editText;
    Button change_btn;
    String NAME_SET_TEXT;
    String userId;
    public NameFragment() {
    }
    public void setSetName(String setName) {
        this.NAME_SET_TEXT = setName;
    }

    public void setuserId(String userId) {
        this.userId = userId;
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
        editText.setText(NAME_SET_TEXT);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        change_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputMethodManager = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);

                String NEW_NAME = editText.getText().toString();
                mDatabase.child("UserAccount").child(userId).child("name").setValue(NEW_NAME);
                Toast.makeText(getContext(), "닉네임이 변경되었습니다", Toast.LENGTH_SHORT).show();

                // 프래그먼트 내에서 사용 중인 EditText의 포커스를 제거하여 키보드를 숨깁니다
                View focusedView = requireActivity().getCurrentFocus();
                if (focusedView != null) {
                    inputMethodManager.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
                }

                if(getActivity() instanceof NewSettingActivity){
                    NewSettingActivity newSettingActivity = (NewSettingActivity) getActivity();
                    newSettingActivity.blindFrame();
                    newSettingActivity.updateName(editText.getText().toString());
                    //((YourActivity) requireActivity()).updateName(updatedText);
                }
            }
        });


        return view;
    }
}