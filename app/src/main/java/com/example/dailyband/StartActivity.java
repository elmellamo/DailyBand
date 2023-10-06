package com.example.dailyband;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.view.View;

import com.example.dailyband.R;
import com.google.android.material.imageview.ShapeableImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class StartActivity extends AppCompatActivity {

    private Button registerbtn, loginbtn;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        getWindow().setStatusBarColor(ContextCompat.getColor(StartActivity.this, R.color.primarydark_color));

        Intent musicServiceIntent = new Intent(this, MusicService.class);
        startService(musicServiceIntent);

        View rootView = findViewById(R.id.background_layout);
        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 포커스를 잃으면 키보드를 숨깁니다.
                hideKeyboard();
                return false;
            }
        });


        registerbtn = findViewById(R.id.register_btn);
        loginbtn = findViewById(R.id.login_btn);

        findViewById(R.id.register_btn).setOnClickListener(onClickListener);
        findViewById(R.id.login_btn).setOnClickListener(onClickListener);

    }

    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("어플 종료")
                .setMessage("어플을 종료하시겠습니까?")
                .setNegativeButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish(); // 어플 종료
                    }
                })
                .setPositiveButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); // 다이얼로그 닫기
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    View.OnClickListener onClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            int id = v.getId();
            if (id == R.id.register_btn) {
                myStartActivity(RegisterActivity.class);
            }
            else if (id == R.id.login_btn) {
                myStartActivity(LoginActivity.class);
            }
        }
    };

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