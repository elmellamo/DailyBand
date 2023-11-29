package com.example.dailyband.Start;
import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.dailyband.Home.HomeMain;
import com.example.dailyband.R;
import com.example.dailyband.Utils.MusicService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class SplashActivity extends AppCompatActivity{

    private static final long SPLASH_DELAY = 650;
    private boolean AUTO_LOGIN = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_splash);
        final TextView splashText = findViewById(R.id.splashTextView);
        final ImageView splashImage = findViewById(R.id.splashImage);
        Intent musicServiceIntent = new Intent(this, MusicService.class);
        startService(musicServiceIntent);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        // SPLASH_DELAY 시간 후에 다음 화면으로 이동합니다.

        if (currentUser != null){
            //Download_image();
            AUTO_LOGIN = true;
        }
        else{
            AUTO_LOGIN=false;
        }

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


    public class MyOuterClass {
        // 바깥 클래스에서 정적 멤버 선언
        public class MyInnerClass {
            // 내부 클래스에서 정적 멤버를 사용할 수 있음
            public void downloadImage(String userUid) {
                // Firebase Storage에서 이미지를 가져와 어플리케이션 내부 저장소에 저장하는 코드
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference().child("images/" + userUid + ".jpg");

                storageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(bytes -> {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                    //저장할 경로 설정 (내부 저장소)
                    String imagePath = Environment.getExternalStorageDirectory() + "/" + userUid + ".jpg";

                    try {
                        // 이미지를 내부 저장소에 저장
                        FileOutputStream fos = new FileOutputStream(imagePath);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                        fos.close();
                        Log.d(TAG, "Image downloaded and saved to " + imagePath);
                    } catch (IOException e) {
                        Log.e(TAG, "Error saving image: " + e.getMessage());
                    }
                }).addOnFailureListener(exception -> {
                    Log.e(TAG, "Error downloading image: " + exception.getMessage());
                });
            }
        }
    }
}
