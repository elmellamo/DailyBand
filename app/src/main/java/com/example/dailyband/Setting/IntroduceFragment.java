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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.dailyband.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class IntroduceFragment extends Fragment {

    private View view;
    private DatabaseReference mDatabase;
    EditText introduce_editText;
    Button change_btn;
    String INTRODUCE_SET_TEXT;
    String userId;

    public IntroduceFragment() {}
    public void setIntroduce(String setIntroduce) {
        this.INTRODUCE_SET_TEXT = setIntroduce;
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
        view = inflater.inflate(R.layout.cardview_introduce, container, false);

        introduce_editText = view.findViewById(R.id.introduce_cardview_edittext);
        change_btn = view.findViewById(R.id.change_introduce_btn);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        introduce_editText.setText(INTRODUCE_SET_TEXT);

        change_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputMethodManager = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);

                String NEW_INTRODUCE = introduce_editText.getText().toString();
                mDatabase.child("user_introduce").child(userId).setValue(NEW_INTRODUCE);
                Toast.makeText(getContext(), "소개글이 수정되었습니다", Toast.LENGTH_SHORT).show();

                // 프래그먼트 내에서 사용 중인 EditText의 포커스를 제거하여 키보드를 숨깁니다
                View focusedView = requireActivity().getCurrentFocus();
                if (focusedView != null) {
                    inputMethodManager.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
                }

                if(getActivity() instanceof NewSettingActivity){
                    NewSettingActivity newSettingActivity = (NewSettingActivity) getActivity();
                    newSettingActivity.blindFrame();
                    newSettingActivity.updateIntroduce(NEW_INTRODUCE);
                    //((YourActivity) requireActivity()).updateName(updatedText);
                }
            }
        });

        return view;
    }
}