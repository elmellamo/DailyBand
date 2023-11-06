package com.example.dailyband.Setting;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EmailFragment extends Fragment {

    private View view;
    EditText email_editText, pw_editText, curr_email_editText;
    Button change_btn;
    private DatabaseReference mDatabase;

    String EMAIL_SET_TEXT, userId;
    public EmailFragment() {}

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

        curr_email_editText = view.findViewById(R.id.curr_email_cardview_edittext);
        //변경할 이메일
        email_editText = view.findViewById(R.id.email_cardview_edittext);
        pw_editText = view.findViewById(R.id.email_pw_cardview_edittext);
        change_btn = view.findViewById(R.id.change_email_btn);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        change_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { //아래 많이 고쳐야함
                InputMethodManager inputMethodManager = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                View focusedView = requireActivity().getCurrentFocus();
                if (focusedView != null) {
                    inputMethodManager.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
                }


                FirebaseAuth mAuth = FirebaseAuth.getInstance();

                String prevemail = email_editText.getText().toString();
                String passwordchk = pw_editText.getText().toString();
                String NEW_EMAILID = curr_email_editText.getText().toString();
                AuthCredential credential = EmailAuthProvider.getCredential(prevemail, passwordchk);
                FirebaseUser user = mAuth.getCurrentUser();

                user.reauthenticate(credential)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // 현재 비밀번호가 올바르게 확인되었을 때, 이메일 변경 진행
                                Toast.makeText(getContext(), "이메일, 비밀번호 일치", Toast.LENGTH_SHORT).show();
                                updateEmail(NEW_EMAILID);
                            } else {
                                // 현재 비밀번호가 틀렸을 때 에러 처리
                                // task.getException()로 에러 정보에 접근 가능
                                Toast.makeText(getContext(), "이메일, 비밀번호 불일치", Toast.LENGTH_SHORT).show();

                            }
                        });

                String NEW_EMAIL = email_editText.getText().toString();
                mDatabase.child("UserAccount").child(userId).child("emailId").setValue(NEW_EMAIL);
            }
        });

        return view;
    }

    private void updateEmail(String newEmail) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();

        user.updateEmail(newEmail)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // 이메일 변경이 성공적으로 완료됨
                        Toast.makeText(getContext(), "이메일 변경 완료", Toast.LENGTH_SHORT).show();

                    } else {
                        String errorMessage = task.getException().getMessage();
                        //int errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                        String errorText = " Message: " + errorMessage;
                        Log.e("EmailChangeError", errorText);

                        // 이메일 변경 중 오류 발생
                        // task.getException()로 에러 정보에 접근 가능
                        Toast.makeText(getContext(), "이메일 변경 실패", Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private boolean isPasswordValid(String password) {
        // 비밀번호가 영문자, 숫자, 기호를 조합하여 8자리 이상인지 확인
        String passwordPattern = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=!]).{8,}$";
        return password.matches(passwordPattern);
    }
}