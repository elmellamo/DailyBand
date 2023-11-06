package com.example.dailyband.Login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dailyband.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mFirebaseAuth; // 파이어 베이스 인증
    private DatabaseReference mDatabaseRef; // 실시간 데이터 베이스
    private EditText nameEdittext,emailEdittext, pwEdittext,pwchkEdittext;
    private Button  registerBtn;
    private TextView gotologinBtn;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        View rootView = findViewById(R.id.background_layout);
        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 포커스를 잃으면 키보드를 숨깁니다.
                hideKeyboard();
                return false;
            }
        });

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();

        nameEdittext = findViewById(R.id.name_edittext);
        emailEdittext = findViewById(R.id.email_edittext);
        pwEdittext = findViewById(R.id.password_edittext);
        pwchkEdittext = findViewById(R.id.password_chk_edittext);
        gotologinBtn = findViewById(R.id.gotologin_btn);
        registerBtn = findViewById(R.id.register_btn);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String namestr = nameEdittext.getText().toString();
                String emailstr = emailEdittext.getText().toString();
                String pwstr = pwEdittext.getText().toString();
                String pwchkstr = pwchkEdittext.getText().toString();

                if (namestr.isEmpty() || emailstr.isEmpty() || pwstr.isEmpty() || pwchkstr.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "정보를 정확히 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!pwstr.equals(pwchkstr)) {
                    Toast.makeText(RegisterActivity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!isPasswordValid(pwstr)) {
                    Toast.makeText(RegisterActivity.this, "비밀번호는 영문자, 숫자, 기호를 조합하여 8자리 이상이어야 합니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!isValidEmail(emailstr)) {
                    Toast.makeText(RegisterActivity.this, "이메일 주소가 유효하지 않습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                mFirebaseAuth.fetchSignInMethodsForEmail(emailstr).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        boolean isNewUser = task.getResult().getSignInMethods().isEmpty();
                        if (isNewUser) {
                            // 새로운 사용자인 경우 회원가입 진행
                            mFirebaseAuth.createUserWithEmailAndPassword(emailstr, pwstr)
                                    .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
                                                UserAccount account = new UserAccount();
                                                account.setIdToken(firebaseUser.getUid());
                                                account.setEmailId(firebaseUser.getEmail());
                                                account.setPassword(pwstr);
                                                account.setUsername(namestr);

                                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                                if (user != null) {
                                                    // 사용자가 로그인되어 있음, getUid() 메소드 호출 가능
                                                    String uid = user.getUid();
                                                    mDatabaseRef.child("UserAccount").child(firebaseUser.getUid()).setValue(account);
                                                    mDatabaseRef.child("user_introduce").child(firebaseUser.getUid()).setValue(null);

                                                } else {
                                                    // 사용자가 로그인되어 있지 않음
                                                    // 오류 처리 또는 로그인 화면으로 이동 등의 작업 수행
                                                }

                                                Toast.makeText(RegisterActivity.this, "회원가입에 성공하였습니다.", Toast.LENGTH_SHORT).show();
                                                myStartActivity(LoginActivity.class);
                                            } else {
                                                Toast.makeText(RegisterActivity.this, "회원가입에 실패하였습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            // 이미 등록된 이메일 주소인 경우 메시지 표시
                            Toast.makeText(RegisterActivity.this, "이미 등록된 이메일 주소입니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        gotologinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myStartActivity(LoginActivity.class);
            }
        });
    }
    private void myStartActivity(Class c){
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
    private boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return email.matches(emailPattern);
    }

    private boolean isPasswordValid(String password) {
        // 비밀번호가 영문자, 숫자, 기호를 조합하여 8자리 이상인지 확인
        String passwordPattern = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=!]).{8,}$";
        return password.matches(passwordPattern);
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}