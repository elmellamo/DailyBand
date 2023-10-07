package com.example.dailyband;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dailyband.Models.ComplexName;
import com.example.dailyband.Utils.FirebaseMethods;
import com.example.dailyband.Utils.TestAdd;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class TestParent extends AppCompatActivity {
    private String parent_Id, parent_title, writer_uid, postId;
    private TextView parenttitle;
    private FirebaseMethods mFirebaseMethods;
    private TextView savemenu;
    private List<ComplexName> parents;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testparent);

        mFirebaseMethods = new FirebaseMethods(TestParent.this);
        parents = new ArrayList<>();
        Intent intent = getIntent();
        parent_Id = intent.getStringExtra("parent_Id");
        parent_title = intent.getStringExtra("parent_title");
        writer_uid = intent.getStringExtra("writer_uid");
        ComplexName cmp = new ComplexName();
        cmp.setTitle(parent_title);
        cmp.setSongid(parent_Id);
        cmp.setWriteruid(writer_uid);

        parents.add(cmp);

        parenttitle = findViewById(R.id.parenttitle);
        savemenu = findViewById(R.id.savemenu);
        parenttitle.setText(parent_title);

        savemenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testParentFirebase();
            }
        });
    }

    private void testParentFirebase(){
        TextInputLayout textInputLayout = findViewById(R.id.songname_edit_layout);
        final String title = textInputLayout.getEditText().getText().toString();
        if(title.length()>0){
            //여기 아무것도 녹음하지 않았을 때 안 함. 테스트니까..!
            postId = mFirebaseMethods.addSongToDatabase(title, parents);
            Intent intent = new Intent(this, HomeMain.class);
            startActivity(intent);
        }else{
            startToast("곡명을 정해주세요.");
        }
    }

    private void startToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

}
