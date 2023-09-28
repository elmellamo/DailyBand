package com.example.dailyband;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;

import com.example.dailyband.R;
import com.google.android.material.imageview.ShapeableImageView;

import androidx.appcompat.app.AppCompatActivity;
public class StartActivity extends AppCompatActivity {

    private Button registerbtn, loginbtn;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        registerbtn = findViewById(R.id.register_btn);
        loginbtn = findViewById(R.id.login_btn);

        findViewById(R.id.register_btn).setOnClickListener(onClickListener);
        findViewById(R.id.login_btn).setOnClickListener(onClickListener);

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
}