package com.example.dailyband.Library;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dailyband.Home.HomeMain;
import com.example.dailyband.MusicAdd.AddMusic;
import com.example.dailyband.MusicFragment.CategoryAddMusic;
import com.example.dailyband.R;
import com.example.dailyband.Setting.SettingActivity;

public class LibraryMain extends AppCompatActivity {
    private ImageButton addbtn, setbtn, homeBtn, librarybtn;
    private SelectLibraryFragment selectLibraryFragment;
    private MyCollect myCollect;
    private MyLove myLove;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.library_main);
        addbtn = findViewById(R.id.addbtn);
        setbtn = findViewById(R.id.setbtn);
        homeBtn = findViewById(R.id.homeBtn);
        librarybtn = findViewById(R.id.librarybtn);
        selectLibraryFragment = new SelectLibraryFragment();
        myCollect = new MyCollect();
        myLove = new MyLove();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.library_frame, selectLibraryFragment)
                    .commit();
        }
        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myStartActivity(AddMusic.class);
            }
        });
        setbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { myStartActivity(SettingActivity.class);    }
        });
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myStartActivity(HomeMain.class);
            }
        });
        librarybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager().beginTransaction().replace(R.id.library_frame, selectLibraryFragment).commit();
            }
        });
    }

    private void myStartActivity(Class c){
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
