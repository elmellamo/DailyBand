package com.example.dailyband.MusicAdd;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.dailyband.Collection.CollectionActivity;
import com.example.dailyband.Home.HomeMain;
import com.example.dailyband.Love.LoveActivity;
import com.example.dailyband.Models.ComplexName;
import com.example.dailyband.Models.TestSong;
import com.example.dailyband.R;
import com.example.dailyband.Setting.NewSettingActivity;
import com.example.dailyband.Utils.FirebaseMethods;
import com.example.dailyband.Utils.KeyboardUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AddCaption extends AppCompatActivity {
    private String title, postId;
    private Uri uri;
    private boolean doubleBackToExitPressedOnce = false;
    private TextView writtentitle, savemenu;
    private List<ComplexName> parents;
    private EditText writer_content, play_content, play_singer, play_explain;
    private FirebaseMethods mFirebaseMethods;
    private ConstraintLayout menu_bar, captionid;
    private ImageButton homeBtn, setbtn, myInfobtn, librarybtn, addbtn;
    private ArrayList<String> originalpostid;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_caption);

        originalpostid = new ArrayList<>();

        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        uri = Uri.parse(intent.getStringExtra("uri"));
        originalpostid = new ArrayList<>();
        originalpostid =  (ArrayList<String>) intent.getSerializableExtra("original");
        mFirebaseMethods = new FirebaseMethods(AddCaption.this);
        parents = new ArrayList<>(); //나중에 이거 없애고 intent로 받아와서 원곡 누구 있는지 알아야 함.

        play_explain = findViewById(R.id.play_explain);
        writer_content = findViewById(R.id.writer_content);
        play_content = findViewById(R.id.play_content);
        menu_bar =findViewById(R.id.menu_bar);
        captionid =findViewById(R.id.captionid);
        play_singer = findViewById(R.id.play_singer);
        writtentitle = findViewById(R.id.writtentitle);
        savemenu = findViewById(R.id.savemenu);
        homeBtn = findViewById(R.id.homeBtn);
        myInfobtn = findViewById(R.id.myInfobtn);
        librarybtn = findViewById(R.id.librarybtn);
        setbtn = findViewById(R.id.setbtn);
        addbtn = findViewById(R.id.addbtn);

        writtentitle.setText(title);
        getmoreDetail();
        savemenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goUpload();
            }
        });

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myStartActivity(HomeMain.class);
            }
        });
        myInfobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myStartActivity(LoveActivity.class);
            }
        });
        librarybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myStartActivity(CollectionActivity.class);
            }
        });
        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddCaption.this, AddMusic.class);
                intent.putExtra("parent_Id", "ori");
                startActivity(intent);
            }
        });
        setbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { myStartActivity(NewSettingActivity.class);    }
        });

    }

    private void getmoreDetail(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("songs");

        parents = new ArrayList<>();

        for (String postId : originalpostid) {
            // songs 카테고리에서 해당 postId에 대한 데이터를 가져오는 리스너 설정
            databaseReference.child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // postId에 해당하는 데이터를 가져오기
                        TestSong selectedSong = dataSnapshot.getValue(TestSong.class);
                        ComplexName complexName = new ComplexName();
                        complexName.setWriteruid(selectedSong.getUser_id());
                        complexName.setSongid(postId);
                        complexName.setTitle(selectedSong.getTitle());
                        parents.add(complexName);
                    } else {
                        // 해당 postId에 대한 데이터가 없는 경우 처리할 내용을 추가할 수 있습니다.
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // 데이터베이스 읽기가 취소된 경우 처리할 내용을 추가할 수 있습니다.
                }
            });
        }
    }

    private  void goUpload(){
        String writer, play, singer, explain;
        explain = play_explain.getText().toString();
        singer = play_singer.getText().toString();
        play = play_content.getText().toString();
        writer = writer_content.getText().toString();
        if (explain.isEmpty()) {
            explain = "음악 소개가 없습니다";
        }
        if (singer.isEmpty()) {
            singer = "노래를 부른 사람에 대한 설명이 없습니다";
        }
        if (play.isEmpty()) {
            play = "사용한 연주에 대한 설명이 없습니다";
        }
        if (writer.isEmpty()) {
            writer = "작곡한 사람에 대한 설명이 없습니다";
        }

        postId = mFirebaseMethods.addSongToDatabase(title, explain, singer, play, writer, parents);
        mFirebaseMethods.uploadNewStorage(title, uri, postId);
        myStartActivity(HomeMain.class);
    }

    private void myStartActivity(Class c){
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void onPause(){
        super.onPause();
        KeyboardUtils.removeAllKeyboardToggleListeners();
    }
    @Override
    public void onResume() {
        super.onResume();
        KeyboardUtils.removeAllKeyboardToggleListeners();
        KeyboardUtils.addKeyboardToggleListener(this, new KeyboardUtils.SoftKeyboardToggleListener()
        {
            @Override
            public void onToggleSoftKeyboard(boolean isVisible)
            {
                ViewGroup.LayoutParams layoutParams = captionid.getLayoutParams();

                if(isVisible) {
                    // 하단버튼 숨기고 댓글뷰 작게
                    menu_bar.setVisibility(View.GONE);
                    int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,200,getResources().getDisplayMetrics());
                    //layoutParams.height = height;
                    captionid.setLayoutParams(layoutParams);
                }
                else {
                    menu_bar.setVisibility(View.VISIBLE);
                    int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,200,getResources().getDisplayMetrics());
                    //layoutParams.height = height;
                    captionid.setLayoutParams(layoutParams);
                }
                Log.d("keyboard", "keyboard visible: "+isVisible);
            }
        });
    }

}
