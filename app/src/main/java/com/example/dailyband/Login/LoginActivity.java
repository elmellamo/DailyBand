package com.example.dailyband.Login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.dailyband.Home.HomeMain;
import com.example.dailyband.R;
import com.example.dailyband.Utils.DataFetchCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularfillableloaders.CircularFillableLoaders;

import java.io.File;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mFirebaseAuth; // 파이어 베이스 인증
    private DatabaseReference mDatabaseRef; // 실시간 데이터 베이스
    private EditText emailEdittext, pwEdittext;
    private Button loginBtn;
    private TextView gotoregisterBtn;
    private CircularFillableLoaders circularFillableLoaders;
    private ConstraintLayout circularlayout;
    private FirebaseAuth mAuth;

    private String loginEmail, loginPW, loginUid;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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

        emailEdittext = findViewById(R.id.email_edittext);
        pwEdittext = findViewById(R.id.password_edittext);
        loginBtn = findViewById(R.id.login_btn);
        gotoregisterBtn = findViewById(R.id.gotoregister_btn);
        circularlayout = findViewById(R.id.circularlayout);
        circularFillableLoaders = (CircularFillableLoaders)findViewById(R.id.circularFillableLoaders);
        circularlayout.bringToFront();


        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        currentUser = null;

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailstr = emailEdittext.getText().toString();
                String pwstr = pwEdittext.getText().toString();


                if (emailstr.isEmpty() || pwstr.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "빈 칸 없이 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                mFirebaseAuth.signInWithEmailAndPassword(emailstr, pwstr).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            // 로그인 성공
                            //Download_image();
                            //myStartActivity(HomeMain.class);
                            //finish();

                            fetchData();

                        }
                        else{
                            Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        gotoregisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myStartActivity(RegisterActivity.class);
            }
        });
    }
    public void Download_image(DataFetchCallback callback){
        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("profile_images/" + userUid + ".jpg");
        String localFilePath = getApplicationContext().getFilesDir() + "/local_image.jpg"; // 로컬에 저장할 파일 경로
        File localFile = new File(localFilePath);

        if (localFile.exists()) {
            localFile.delete();
        }
        storageRef.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
            callback.onDataFetchedSuccessfully();
            myStartActivity(HomeMain.class);
            finish();
        }).addOnFailureListener(exception -> {
            callback.onDataFetchFailed();
            myStartActivity(HomeMain.class);
            finish();
        }).addOnProgressListener(taskSnapshot -> {
        });
    }

    private void showProgressBar() {
        // 프로그레스바를 보여주는 코드
        circularlayout.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        // 프로그레스바를 숨기는 코드
        circularlayout.animate()
                .alpha(0f) // 투명도를 0으로 설정하여 페이드 아웃 애니메이션 적용
                .setDuration(500) // 애니메이션 지속 시간 (밀리초)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        // 애니메이션 종료 후에 실행할 작업 (예: 뷰를 숨기거나 제거하는 등)
                        circularlayout.setVisibility(View.GONE);
                    }
                })
                .start();
    }
    private void myStartActivity(Class c){
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    private void fetchData() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean autoLoginEnabled = preferences.getBoolean("auto_login_enabled", false);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("auto_login_enabled", true);
        editor.apply();

        showProgressBar();
        Download_image(new DataFetchCallback() {
            @Override
            public void onDataFetchedSuccessfully() {
                // getInfo 성공 후의 처리
                hideProgressBar();
            }

            @Override
            public void onDataFetchFailed() {
                // getInfo 실패 후의 처리
                // 여기에서 프로그레스바를 숨김
                hideProgressBar();
            }
        });
    }



    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}