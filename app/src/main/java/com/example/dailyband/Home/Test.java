package com.example.dailyband.Home;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dailyband.R;
import com.mikhaellopez.circularfillableloaders.CircularFillableLoaders;

public class Test extends AppCompatActivity {
    private CircularFillableLoaders circularFillableLoaders;
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);

        circularFillableLoaders = findViewById(R.id.circularFillableLoaders);

    }
}
