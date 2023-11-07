package com.example.dailyband.Home;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dailyband.Library.LibraryMain;
import com.example.dailyband.Love.LoveActivity;
import com.example.dailyband.Models.TestSong;
import com.example.dailyband.MusicAdd.AddMusic;
import com.example.dailyband.R;
import com.example.dailyband.Setting.NewSettingActivity;
import com.example.dailyband.Utils.FirebaseMethods;
import com.example.dailyband.adapter.RankingSongAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HomeMain extends AppCompatActivity{
    private ImageButton addbtn, setbtn, librarybtn, myInfobtn;
    private TextView username;
    private RecyclerView recyclerView;
    private RankingSongAdapter adapter;
    private List<TestSong> songs;

    private static final int REQUEST_PERMISSION_CODE = 1000;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;
    private FirebaseAuth mAuth;
    private String nickname;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homemain);

        mFirebaseMethods = new FirebaseMethods(HomeMain.this);
        myPermissions();

        nickname = "사용자";
        addbtn = findViewById(R.id.addbtn);
        librarybtn = findViewById(R.id.librarybtn);
        recyclerView = findViewById(R.id.popularlist);
        myInfobtn = findViewById(R.id.myInfobtn);
        username = findViewById(R.id.username);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        songs = new ArrayList<>();
        getInfo();
        getSongs();
        username.setText(nickname);

        myInfobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myStartActivity(LoveActivity.class);
            }
        });
        librarybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myStartActivity(LibraryMain.class);
            }
        });
        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //myStartActivity(TestAdd.class);
                myStartActivity(AddMusic.class);
            }
        });
        setbtn = findViewById(R.id.setbtn);
        setbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { myStartActivity(NewSettingActivity.class);    }
        });
    }

    private void getSongs(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("songs");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                songs.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    TestSong song = snapshot.getValue(TestSong.class);
                    if (song != null) {
                        songs.add(song);
                    }
                }

                // songs 리스트를 love를 기준으로 내림차순으로 정렬하고, love가 같을 경우에는 시간을 기준으로 내림차순으로 정렬
                Collections.sort(songs, new Comparator<TestSong>() {
                    @Override
                    public int compare(TestSong song1, TestSong song2) {
                        // love가 큰 순서대로 정렬
                        int loveComparison = Integer.compare(song2.getLove(), song1.getLove());
                        if (loveComparison != 0) {
                            return loveComparison;
                        }
                        // love가 같은 경우 시간을 큰 순서대로 정렬
                        return song2.getDate_created().compareTo(song1.getDate_created());
                    }
                });

                // 정렬된 데이터를 리사이클러뷰에 표시하기 위해 어댑터에 설정
                RankingSongAdapter adapter = new RankingSongAdapter(HomeMain.this, songs);
                recyclerView.setAdapter(adapter);

                // songs 리스트에 파이어베이스에서 읽어온 데이터가 들어있음
                // 이제 songs 리스트를 정렬하고 리사이클러뷰에 표시할 수 있음
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 데이터를 가져오는데 실패한 경우 처리하는 코드
                Toast.makeText(HomeMain.this, "노래가 성공적으로 등록되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getInfo(){
        String userUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userAccountRef = FirebaseDatabase.getInstance().getReference().child("UserAccount").child(userUID);
        userAccountRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot userAccountDataSnapshot) {
                if (userAccountDataSnapshot.exists()) {
                    // UserAccount 카테고리에서 name 값을 가져와서 사용하거나 처리할 수 있습니다.
                    nickname = userAccountDataSnapshot.child("name").getValue(String.class);
                    username.setText(nickname);
                } else {
                    // "UserAccount" 카테고리에서 해당 데이터가 없는 경우에 대한 처리
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 데이터 가져오기가 실패한 경우에 대한 처리
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            boolean allPermissionsGranted = true;
            int j=0;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;

                    //권한을 하나라도 허용하지 않는다면 앱 종료
                    Toast.makeText(getApplicationContext(), permissions[j] + " 권한을 설정하세요", Toast.LENGTH_LONG).show();
                    myPermissions();
                    break;
                }
                j++;
            }

            if (allPermissionsGranted) {
                // 모든 권한이 허용된 경우 원하는 작업 수행
                // 예: 녹음 시작 또는 다른 작업 수행
            } else {
                // 사용자에게 알림을 표시하거나 적절한 조치를 취하세요.
                //Toast.makeText(getApplicationContext(), "모든 권한을 허용해야 사용할 수 있습니다.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void myPermissions(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            List<String> permissionsTORequest = new ArrayList<>();
            String[] permissions = new String[]{
                    Manifest.permission.READ_MEDIA_AUDIO,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.MODIFY_AUDIO_SETTINGS
            };
            for (String permission : permissions){
                if (ContextCompat.checkSelfPermission(this,permission) != PackageManager.PERMISSION_GRANTED){
                    permissionsTORequest.add(permission);
                    //startToast("여기 이상해요."+permission);
                }
            }

            if (permissionsTORequest.isEmpty()){
                // All permissions are already granted
                //Toast.makeText(this, "All permissions are already granted", Toast.LENGTH_SHORT).show();


            } else {
                String[] permissionsArray = permissionsTORequest.toArray(new String[0]);
                boolean shouldShowRationale = false;

                for (String permission : permissionsArray){
                    if (shouldShowRequestPermissionRationale(permission)){
                        shouldShowRationale = true;
                        break;
                    }
                }

                if (shouldShowRationale){
                    new AlertDialog.Builder(this)
                            .setMessage("Please allow all permissions")
                            .setCancelable(false)
                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //requestPermissionLauncher.launch(permissionsArray);
                                    ActivityCompat.requestPermissions(HomeMain.this, permissionsArray, REQUEST_PERMISSION_CODE);
                                }
                            })

                            .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            })
                            .show();

                } else {
                    //requestPermissionLauncher.launch(permissionsArray);

                    ActivityCompat.requestPermissions(HomeMain.this, permissionsArray, REQUEST_PERMISSION_CODE);
                }


            }


        } else{
            String[] allPermissions = {
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.MODIFY_AUDIO_SETTINGS
            };

            List<String> permissionsTORequest = new ArrayList<>();
            for (String permission : allPermissions){
                if (ContextCompat.checkSelfPermission(this,permission) != PackageManager.PERMISSION_GRANTED){
                    permissionsTORequest.add(permission);
                }
            }

            if (permissionsTORequest.isEmpty()){
                // All permissions are already granted
                //Toast.makeText(this, "All permissions are already granted", Toast.LENGTH_SHORT).show();


            } else {
                String[] permissionsArray = permissionsTORequest.toArray(new String[0]);
                boolean shouldShowRationale = false;

                for (String permission : permissionsArray){
                    if (shouldShowRequestPermissionRationale(permission)){
                        shouldShowRationale = true;
                        break;
                    }
                }

                if (shouldShowRationale){
                    new AlertDialog.Builder(this)
                            .setMessage("Please allow all permissions")
                            .setCancelable(false)
                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //requestPermissionLauncher.launch(permissionsArray);

                                    ActivityCompat.requestPermissions(HomeMain.this, permissionsArray, REQUEST_PERMISSION_CODE);
                                }
                            })

                            .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            })
                            .show();

                } else {
                    //requestPermissionLauncher.launch(permissionsArray);
                    ActivityCompat.requestPermissions(HomeMain.this, permissionsArray, REQUEST_PERMISSION_CODE);
                }
            }
        }
    }


    private void myStartActivity(Class c){
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
