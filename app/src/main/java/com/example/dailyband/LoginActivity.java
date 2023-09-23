package com.example.dailyband;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mFirebaseAuth; // 파이어 베이스 인증
    private DatabaseReference mDatabaseRef; // 실시간 데이터 베이스
    private EditText emailEdittext, pwEdittext;
    private Button loginBtn, gotoregisterBtn;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();

        emailEdittext = findViewById(R.id.email_edittext);
        pwEdittext = findViewById(R.id.password_edittext);
        loginBtn = findViewById(R.id.login_btn);
        gotoregisterBtn = findViewById(R.id.gotoregister_btn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailstr = emailEdittext.getText().toString();
                String pwstr = pwEdittext.getText().toString();
                mFirebaseAuth.signInWithEmailAndPassword(emailstr, pwstr).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            // 로그인 성공
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
    }

    private void myStartActivity(Class c){
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}