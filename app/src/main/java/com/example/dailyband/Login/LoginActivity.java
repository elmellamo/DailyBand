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

import com.example.dailyband.Home.HomeMain;
import com.example.dailyband.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mFirebaseAuth; // 파이어 베이스 인증
    private DatabaseReference mDatabaseRef; // 실시간 데이터 베이스
    private EditText emailEdittext, pwEdittext;
    private Button loginBtn;
    private TextView gotoregisterBtn;
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
                            Download_image();
                            myStartActivity(HomeMain.class);
                            finish();
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
    public void Download_image(){
        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("profile_images/" + userUid + ".jpg");
        //String imagePath = Environment.getExternalStorageDirectory() + "/" + userUid + ".jpg";
        String localFilePath = getApplicationContext().getFilesDir() + "/local_image.jpg"; // 로컬에 저장할 파일 경로
        File localFile = new File(localFilePath);
        if (localFile.exists()) {
            localFile.delete();
        }
        storageRef.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
            // 다운로드 성공
            // localFilePath에 이미지가 저장됨
            // 여기서 UI 업데이트 등을 수행할 수 있습니다.
            //Toast.makeText(SplashActivity.this, "이미지 다운로드", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(exception -> {
            // 다운로드 실패
        }).addOnProgressListener(taskSnapshot -> {
            // 다운로드 진행 중
        });

    }
    private void myStartActivity(Class c){
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}