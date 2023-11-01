package com.example.dailyband.Start;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dailyband.Home.HomeMain;
import com.example.dailyband.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.example.dailyband.Start.StartActivity;
import com.example.dailyband.Login.RegisterActivity;


public class SplashActivity extends AppCompatActivity{

    private static final long SPLASH_DELAY = 650; // 스플래시 화면 표시 시간 (2 초)
    private boolean AUTO_LOGIN = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        // SPLASH_DELAY 시간 후에 다음 화면으로 이동합니다.

        if (currentUser != null)
            AUTO_LOGIN = true;
        else
            AUTO_LOGIN=false;


        final TextView splashText = findViewById(R.id.splashTextView);
        final ImageView splashImage = findViewById(R.id.splashImage);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim_splash_textview);
        Animation animation1 = AnimationUtils.loadAnimation(this,R.anim.anim_splash_imageview);
        splashImage.setVisibility(View.VISIBLE);
        splashText.setVisibility(View.VISIBLE);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                // 애니메이션 종료 후, 메인 액티비티를 시작하고 현재 액티비티를 종료합니다.
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(AUTO_LOGIN){
                            Intent intent = new Intent(SplashActivity.this, HomeMain.class);
                            startActivity(intent);
                            finish();
                        }
                        else{
                            Intent intent = new Intent(SplashActivity.this, StartActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                }, SPLASH_DELAY);


            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        animation1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                // 애니메이션 종료 후, 메인 액티비티를 시작하고 현재 액티비티를 종료합니다.
                //Intent intent = new Intent(SplashActivity.this, StartActivity.class);
                //startActivity(intent);
                //finish();
            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        splashText.startAnimation(animation);
        splashImage.startAnimation(animation1);

    }

    @UiThread
    private void splashAnimation() {
        TextView splashTextView = findViewById(R.id.splashTextView);
        Animation textAnim = AnimationUtils.loadAnimation(this, R.anim.anim_splash_textview);
        splashTextView.startAnimation(textAnim);
        Animation imageAnim = AnimationUtils.loadAnimation(this, R.anim.anim_splash_imageview);
        splashTextView.startAnimation(imageAnim);

        imageAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                // Replace the following line with the appropriate code to start MainActivity in Java
                // startActivity(new Intent(getApplicationContext(), MainActivity.class));

                // You can use an Intent to start the MainActivity
                Intent intent = new Intent(getApplicationContext(), StartActivity.class);
                startActivity(intent);

                // You can set the animation transition here as well
                overridePendingTransition(R.anim.anim_splash_out_top, R.anim.anim_splash_in_down);

                finish();
            }

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }
}
