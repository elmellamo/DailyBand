package com.example.dailyband.Setting;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.dailyband.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EmailFragment extends Fragment {

    private View view;
    EditText email_editText, pw_editText;
    Button change_btn;
    private DatabaseReference mDatabase;

    String EMAIL_SET_TEXT, userId;
    public EmailFragment() {
    }
    public void setEmail(String setEmail) {
        this.EMAIL_SET_TEXT = setEmail;
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
        view = inflater.inflate(R.layout.cardview_email, container, false);


        email_editText = view.findViewById(R.id.email_cardview_edittext);
        pw_editText = view.findViewById(R.id.email_pw_cardview_edittext);
        change_btn = view.findViewById(R.id.change_email_btn);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        change_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { //아래 많이 고쳐야함
                InputMethodManager inputMethodManager = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);

                String NEW_NAME = email_editText.getText().toString();
                mDatabase.child("UserAccount").child(userId).child("name").setValue(NEW_NAME);

                // 프래그먼트 내에서 사용 중인 EditText의 포커스를 제거하여 키보드를 숨깁니다
                View focusedView = requireActivity().getCurrentFocus();
                if (focusedView != null) {
                    inputMethodManager.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
                }

                if(getActivity() instanceof NewSettingActivity){
                    NewSettingActivity newSettingActivity = (NewSettingActivity) getActivity();
                    newSettingActivity.blindFrame();
                    newSettingActivity.updateName(email_editText.getText().toString());
                    //((YourActivity) requireActivity()).updateName(updatedText);
                }
            }
        });

        return view;
    }
}