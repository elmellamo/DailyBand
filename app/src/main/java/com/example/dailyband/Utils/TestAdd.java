package com.example.dailyband.Utils;

import static android.Manifest.*;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.dailyband.HomeMain;
import com.example.dailyband.R;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;

public class TestAdd extends AppCompatActivity {
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private MediaRecorder mediaRecorder;
    private String outputFile;
    private ImageView playbtn;
    private ImageView pausebtn;
    private ImageView stopbtn;
    private FirebaseMethods mFirebaseMethods;
    private String postId;
    private TextView savemenu;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testaddmusic);

        mFirebaseMethods = new FirebaseMethods(TestAdd.this);

        //녹음 파일 경로 저장
        String storagePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/record.3gp";
        outputFile = storagePath;

        //녹음 시작 버튼 클릭
        playbtn = findViewById(R.id.playbtn);
        pausebtn = findViewById(R.id.pausebtn);
        stopbtn = findViewById(R.id.stopbtn);
        savemenu = findViewById(R.id.savemenu);

        playbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chkPermission();
            }
        });

        stopbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopRecording();
                uploadToFirebase();
            }
        });
        savemenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testFirebase();
            }
        });

    }

    private void testFirebase(){
        TextInputLayout textInputLayout = findViewById(R.id.songname_edit_layout);
        final String title = textInputLayout.getEditText().getText().toString();
        if(title.length()>0){
            //여기 아무것도 녹음하지 않았을 때 안 함. 테스트니까..!
            postId = mFirebaseMethods.addSongToDatabase(title);
            Intent intent = new Intent(this, HomeMain.class);
            startActivity(intent);
        }else{
            startToast("곡명을 정해주세요.");
        }
    }

    private void startRecording(){
        if (mediaRecorder == null) {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setOutputFile(outputFile);

            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
            } catch (IOException e) {
                e.printStackTrace();
                // 녹음 시작에 실패한 경우 처리할 코드 추가
            }
        }
    }

    private void chkPermission(){
        if (ContextCompat.checkSelfPermission(this, permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            // 녹음 권한이 허용되지 않은 경우 런타임 권한 요청
            ActivityCompat.requestPermissions(this, new String[]{permission.RECORD_AUDIO},
                    REQUEST_RECORD_AUDIO_PERMISSION);
        } else {
            startRecording();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_RECORD_AUDIO_PERMISSION){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //녹음 권한이 허용된 경우 녹음 시작 코드 실행
                chkPermission();
            }else{
                //녹음 권한이 거부된 경우 사용자에게 설명하기 혹은 다시 요청하기
            }
        }
    }

    private void stopRecording(){
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }

    private void uploadToFirebase(){
        TextInputLayout textInputLayout = findViewById(R.id.songname_edit_layout);
        final String title = textInputLayout.getEditText().getText().toString();
        if(title.length()>0){
            //여기 아무것도 녹음하지 않았을 때 안 함. 테스트니까..!
            postId = mFirebaseMethods.addSongToDatabase(title);
            mFirebaseMethods.uploadNewStorage(title, outputFile, postId);
        }else{
            startToast("곡명을 정해주세요.");
        }
    }

    private void startToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

}
