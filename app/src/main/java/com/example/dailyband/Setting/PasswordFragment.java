package com.example.dailyband.Setting;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
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

import com.example.dailyband.Login.RegisterActivity;
import com.example.dailyband.R;
import com.example.dailyband.Utils.KeyboardUtils;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PasswordFragment extends Fragment {

    private View view;
    private NewSettingActivity newSettingActivity;
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
        change_btn = view.findViewById(R.id.change_pw_btn);
        newSettingActivity =(NewSettingActivity) getActivity();

        change_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputMethodManager = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                View focusedView = requireActivity().getCurrentFocus();
                if (focusedView != null) {
                    inputMethodManager.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
                }

                String currentPassword = prevpw.getText().toString();
                String newPassword = currpw.getText().toString();
                String newPasswordchk = currpwchk.getText().toString();
                if (!newPasswordchk.equals(newPassword)) {
                    Toast.makeText(getContext(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!isPasswordValid(newPassword)) {
                    Toast.makeText(getContext(), "비밀번호는 영문자, 숫자, 기호를 조합하여 8자리 이상이어야 합니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser user = mAuth.getCurrentUser();
                AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);
                user.reauthenticate(credential)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // 기존 비밀번호가 올바르게 확인되었을 때, 비밀번호 변경 진행
                                user.updatePassword(newPassword)
                                        .addOnCompleteListener(updateTask -> {
                                            if (updateTask.isSuccessful()) {
                                                // 비밀번호 변경이 성공적으로 완료됨
                                                Toast.makeText(getContext(), "비밀번호 변경 완료", Toast.LENGTH_SHORT).show();

                                                if(getActivity() instanceof NewSettingActivity){
                                                    NewSettingActivity newSettingActivity = (NewSettingActivity) getActivity();
                                                    newSettingActivity.blindFrame();
                                                    newSettingActivity.updateIsFragmentOpen(false);
                                                    //((YourActivity) requireActivity()).updateName(updatedText);
                                                }
                                            } else {
                                                // 비밀번호 변경 중 오류 발생
                                                String errorMessage = updateTask.getException().getMessage();
                                                Toast.makeText(getContext(), "비밀번호 변경 실패: " + errorMessage, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                // 기존 비밀번호가 틀렸을 때 에러 처리
                                String errorMessage = task.getException().getMessage();
                                Toast.makeText(getContext(), "기존 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        return view;
    }

    private boolean isPasswordValid(String password) {
        // 비밀번호가 영문자, 숫자, 기호를 조합하여 8자리 이상인지 확인
        String passwordPattern = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=!]).{8,}$";
        return password.matches(passwordPattern);
    }

    public void onPause(){
        super.onPause();
        KeyboardUtils.removeAllKeyboardToggleListeners();
    }
    @Override
    public void onResume() {
        super.onResume();
        KeyboardUtils.removeAllKeyboardToggleListeners();
        KeyboardUtils.addKeyboardToggleListener(newSettingActivity, new KeyboardUtils.SoftKeyboardToggleListener()
        {
            @Override
            public void onToggleSoftKeyboard(boolean isVisible)
            {
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();

                if(isVisible) {
                    // 하단버튼 숨기고 댓글뷰 작게
                    newSettingActivity.menubar.setVisibility(View.GONE);
                    int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,200,getResources().getDisplayMetrics());
                    //layoutParams.height = height;
                    view.setLayoutParams(layoutParams);
                }
                else {
                    newSettingActivity.menubar.setVisibility(View.VISIBLE);
                    int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,400,getResources().getDisplayMetrics());
                    //layoutParams.height = height;
                    view.setLayoutParams(layoutParams);
                }
                Log.d("keyboard", "keyboard visible: "+isVisible);
            }
        });
    }

}