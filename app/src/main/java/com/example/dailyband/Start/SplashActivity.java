package com.example.dailyband.Start;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dailyband.Home.HomeMain;
import com.example.dailyband.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class SplashActivity extends AppCompatActivity{

    private static final long SPLASH_DELAY = 500; // 스플래시 화면 표시 시간 (2 초)
    private boolean AUTO_LOGIN = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        // SPLASH_DELAY 시간 후에 다음 화면으로 이동합니다.

        if (currentUser != null) {
            // 사용자가 이미 로그인한 경우
            // 여기서 사용자를 앱 내부로 리디렉션하거나 홈 화면으로 이동할 수 있습니다.
            // 예를 들어, 사용자의 프로필 화면으로 리디렉션:
            // startActivity(new Intent(this, UserProfileActivity.class));
            AUTO_LOGIN = true;
        } else {
            // 사용자가 로그인하지 않은 경우
            // 여기서 로그인 화면으로 이동하거나 다른 작업을 수행할 수 있습니다.
            // 예를 들어, 로그인 화면으로 리디렉션:
            // startActivity(new Intent(this, LoginActivity.class));
            AUTO_LOGIN=false;
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(AUTO_LOGIN){
                    Intent mainIntent = new Intent(SplashActivity.this, HomeMain.class);
                    startActivity(mainIntent);
                    finish();
                }
                else{
                    Intent mainIntent = new Intent(SplashActivity.this, StartActivity.class);
                    startActivity(mainIntent);
                    finish();
                }
            }
        }, SPLASH_DELAY);
    }
}
