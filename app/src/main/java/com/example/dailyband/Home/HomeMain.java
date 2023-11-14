package com.example.dailyband.Home;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dailyband.Collection.CollectionActivity;
import com.example.dailyband.Love.LoveActivity;
import com.example.dailyband.Models.TestSong;
import com.example.dailyband.MusicAdd.AddMusic;
import com.example.dailyband.R;
import com.example.dailyband.Setting.NewSettingActivity;
import com.example.dailyband.Utils.DataFetchCallback;
import com.example.dailyband.Utils.FirebaseMethods;
import com.example.dailyband.adapter.RankingSongAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularfillableloaders.CircularFillableLoaders;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HomeMain extends AppCompatActivity{
    private ImageButton addbtn, setbtn, librarybtn, myInfobtn, homeBtn;
    private TextView username, gomakeyourmusic;
    private RecyclerView recyclerView;
    private RankingSongAdapter adapter;
    private List<TestSong> songs;
    private ImageView circle_iv;
    private LinearLayout emptytxt;
    private boolean doubleBackToExitPressedOnce = false;
    private static final int REQUEST_PERMISSION_CODE = 1000;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;
    private FirebaseAuth mAuth;
    private String nickname;
    int progress = 0;
    boolean started = false;
    private CircularFillableLoaders circularFillableLoaders;
    private ConstraintLayout circularlayout;

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
        setbtn = findViewById(R.id.setbtn);
        homeBtn = findViewById(R.id.homeBtn);
        circle_iv = findViewById(R.id.circle_iv);
        emptytxt = findViewById(R.id.emptytxt);
        gomakeyourmusic = findViewById(R.id.gomakeyourmusic);
        circularlayout = findViewById(R.id.circularlayout);
        circularFillableLoaders = (CircularFillableLoaders)findViewById(R.id.circularFillableLoaders);
        circularlayout.bringToFront();
        circle_iv.bringToFront();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        songs = new ArrayList<>();
        getImage();
        fetchData();
        username.setText(nickname);


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
                Intent intent = new Intent(HomeMain.this, AddMusic.class);
                intent.putExtra("parent_Id", "ori");
                startActivity(intent);
            }
        });
        setbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { myStartActivity(NewSettingActivity.class);    }
        });
    }

    private void getSongs(DataFetchCallback callback){
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
                adapter = new RankingSongAdapter(HomeMain.this, songs);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                emptytxt.setVisibility(songs.size() == 0 ? View.VISIBLE : View.GONE);
                callback.onDataFetchedSuccessfully();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 데이터를 가져오는데 실패한 경우 처리하는 코드
                Toast.makeText(HomeMain.this, "노래가 성공적으로 등록되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getInfo(DataFetchCallback callback){
        //started = true;
        String userUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userAccountRef = FirebaseDatabase.getInstance().getReference().child("UserAccount").child(userUID);
        userAccountRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot userAccountDataSnapshot) {
                if (userAccountDataSnapshot.exists()) {
                    // UserAccount 카테고리에서 name 값을 가져와서 사용하거나 처리할 수 있습니다.
                    nickname = userAccountDataSnapshot.child("name").getValue(String.class);
                    username.setText(nickname);
                    callback.onDataFetchedSuccessfully();
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
    private void getImage(){
        /*
        String userUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        StorageReference profileImageRef = FirebaseStorage.getInstance().getReference().child("profile_images").child(userUID+".jpg");
        profileImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            String imageUrl = uri.toString();
            if(!isDestroyed() && !isFinishing()){
                Glide.with(this)
                        .load(imageUrl)
                        .into(circle_iv); // profileImage는 앱의 이미지뷰 객체
            }
        });
        */
        String localFilePath = getApplicationContext().getFilesDir() + "/local_image.jpg";
        File localFile = new File(localFilePath); // 이미지 파일의 로컬 경로
        if (localFile.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());

            int rotation = 0;
            try {
                ExifInterface exif = new ExifInterface(localFilePath);
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION
                        , ExifInterface.ORIENTATION_NORMAL);

                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotation = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotation = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotation = 270;
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            Bitmap rotatedBitmap = rotateImage(bitmap, rotation);

            //if (circle_iv != null) {
            circle_iv.setImageBitmap(rotatedBitmap);
            //}
        } else {
            // 파일이 존재하지 않는 경우
            // 사용자에게 알림을 표시하거나 다른 조치를 취할 수 있습니다.
        }

    }
    private Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
    private void fetchData() {
        showProgressBar();

        getInfo(new DataFetchCallback() {
            @Override
            public void onDataFetchedSuccessfully() {
                // getInfo 성공 후의 처리

                getSongs(new DataFetchCallback() {
                    @Override
                    public void onDataFetchedSuccessfully() {
                        // getSongs 성공 후의 처리
                        hideProgressBar();
                        // 여기에서 프로그레스바를 숨김
                    }

                    @Override
                    public void onDataFetchFailed() {
                        // getSongs 실패 후의 처리
                        hideProgressBar();
                        songs.clear(); // 데이터를 가져오는데 실패했으므로 리스트 비우기
                        adapter.notifyDataSetChanged(); // 어댑터에 변경된 내용 알림
                        emptytxt.setVisibility(songs.size() == 0 ? View.VISIBLE : View.GONE);
                    }
                });
            }

            @Override
            public void onDataFetchFailed() {
                // getInfo 실패 후의 처리
                hideProgressBar();
                // 여기에서 프로그레스바를 숨김
            }
        });
    }

    private void showProgressBar() {
        // 프로그레스바를 보여주는 코드
        circularlayout.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        // 프로그레스바를 숨기는 코드
        circularlayout.animate()
                .alpha(0f) // 투명도를 0으로 설정하여 페이드 아웃 애니메이션 적용
                .setDuration(500) // 애니메이션 지속 시간 (밀리초)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        // 애니메이션 종료 후에 실행할 작업 (예: 뷰를 숨기거나 제거하는 등)
                        circularlayout.setVisibility(View.GONE);
                    }
                })
                .start();
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
        finish();
    }
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            finishAffinity(); // 현재 액티비티와 관련된 모든 액티비티 종료
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "한 번 더 누르면 앱이 종료됩니다.", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000); // 2초 안에 다시 뒤로가기 버튼을 눌러야 종료
    }

}
