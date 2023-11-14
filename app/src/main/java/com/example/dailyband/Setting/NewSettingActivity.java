package com.example.dailyband.Setting;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.example.dailyband.Collection.CollectionActivity;
import com.example.dailyband.Home.HomeMain;
import com.example.dailyband.Login.LoginActivity;
import com.example.dailyband.Love.LoveActivity;
import com.example.dailyband.MusicAdd.AddMusic;
import com.example.dailyband.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

public class NewSettingActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private FirebaseStorage storage;
    private DatabaseReference mDatabase;
    private boolean doubleBackToExitPressedOnce = false;
    private ImageView profileImg;
    private ImageView tmpImg;
    private Uri selectedImageUri = null;
    private Uri userImage=null;
    private ConstraintLayout detail_info_layout, setting_user_layout, setting_email_layout
            ,setting_introduce_layout,setting_password_layout;

    private ConstraintLayout setting_withdrawal_layout;
    private ConstraintLayout setting_logout_layout;
    private ConstraintLayout setting_contact_layout;
    private boolean isCardViewVisible = false;

    private ConstraintLayout gray_screen;

    private String NAME_SET_TEXT;
    public String EMAIL_SET_TEXT, INTRODUCE_SET_TEXT;
    private boolean is_Fragment_Open = false;
    private NameFragment nameFragment;
    private IntroduceFragment introduceFragment;
    private PasswordFragment passwordFragment;
    private String name_intent;
    private ImageButton addbtn, setbtn, librarybtn, myInfobtn, homeBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_new);
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        profileImg = findViewById(R.id.bird_img);
        Animation slideUpAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        setting_user_layout = findViewById(R.id.setting_user_layout);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        setting_withdrawal_layout = findViewById(R.id.setting_withdrawal_layout);
        setting_logout_layout = findViewById(R.id.setting_logout_layout);
        setting_contact_layout = findViewById(R.id.setting_contact_layout);
        nameFragment = new NameFragment();
        introduceFragment = new IntroduceFragment();
        passwordFragment  = new PasswordFragment();
        gray_screen = findViewById(R.id.gray_screen);
        detail_info_layout = findViewById(R.id.detail_info_layout);
        setting_introduce_layout = findViewById(R.id.setting_introduce_layout);
        setting_password_layout = findViewById(R.id.setting_password_layout);
        homeBtn = findViewById(R.id.homeBtn);
        myInfobtn = findViewById(R.id.myInfobtn);
        librarybtn = findViewById(R.id.librarybtn);
        setbtn = findViewById(R.id.setbtn);
        addbtn = findViewById(R.id.addbtn);


        profileImg.bringToFront();
        String userUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        StorageReference storageRef = storage.getReference().child("profile_images");
        StorageReference imageRef = storageRef.child(userUID+".jpg");
        mDatabase.child("UserAccount").child(userUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                NAME_SET_TEXT = snapshot.child("name").getValue().toString();
                EMAIL_SET_TEXT = snapshot.child("emailId").getValue().toString();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mDatabase.child("user_introduce").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(userUID)){
                    INTRODUCE_SET_TEXT = snapshot.child(userUID).getValue().toString();
                }
                else{
                    INTRODUCE_SET_TEXT = null;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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

            if (profileImg != null) {
                profileImg.setImageBitmap(rotatedBitmap);
                //imageView7.setImageBitmap(rotatedBitmap);
                //imageView7.bringToFront();
                //Toast.makeText(NewSettingActivity.this, "로컬 파일 존재", Toast.LENGTH_SHORT).show();
            }
        } else {
            // 파일이 존재하지 않는 경우
            // 사용자에게 알림을 표시하거나 다른 조치를 취할 수 있습니다.
        }

        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            String imageUrl = uri.toString();
            Glide.with(this)
                    .load(imageUrl)
                    //.placeholder(R.drawable.brid_second_img)
                    //.error(R.drawable.brid_second_img)
                    .into(profileImg); // profileImage는 앱의 이미지뷰 객체
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
                Intent intent = new Intent(NewSettingActivity.this, AddMusic.class);
                intent.putExtra("parent_Id", "ori");
                startActivity(intent);
            }
        });
        setbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { myStartActivity(NewSettingActivity.class);    }
        });

        //사용자 이름 클릭하면 사용자 이름 수정하는 카드 뷰 띄우기
        setting_user_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // detail_info_layout을 보이도록 변경합니다.
                is_Fragment_Open = true;
                nameFragment = new NameFragment();
                nameFragment.setSetName(NAME_SET_TEXT);
                nameFragment.setuserId(userUID);
                detail_info_layout.setVisibility(View.VISIBLE);
                getSupportFragmentManager().beginTransaction().replace(R.id.detail_info_frame, nameFragment).commit();
            }
        });


        setting_introduce_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // detail_info_layout을 보이도록 변경합니다.\

                is_Fragment_Open = true;
                introduceFragment = new IntroduceFragment();
                detail_info_layout.setVisibility(View.VISIBLE);
                introduceFragment.setuserId(userUID);
                introduceFragment.setIntroduce(INTRODUCE_SET_TEXT);
                getSupportFragmentManager().beginTransaction().replace(R.id.detail_info_frame, introduceFragment).commit();
            }
        });

        setting_password_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // detail_info_layout을 보이도록 변경합니다.

                is_Fragment_Open = true;
                passwordFragment = new PasswordFragment();
                detail_info_layout.setVisibility(View.VISIBLE);
                getSupportFragmentManager().beginTransaction().replace(R.id.detail_info_frame, passwordFragment).commit();
            }
        });

        gray_screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
                is_Fragment_Open = false;
                if (detail_info_layout.getVisibility() == View.VISIBLE) {
                    detail_info_layout.setVisibility(View.GONE);
                }
            }
        });

        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start photo picker to select an image
                openGallery();
            }
        });

        setting_logout_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LOGOUT();
            }
        });

        //탈퇴하는거 구현해야함
        setting_withdrawal_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WITHDRAW();
            }
        });

        setting_contact_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent email = new Intent(Intent.ACTION_SEND);
                email.setType("plain/text");
                String[] address = {"dobbydavid1@naver.com"};
                email.putExtra(Intent.EXTRA_EMAIL, address);
                email.putExtra(Intent.EXTRA_SUBJECT, "데일리밴드 1대1 문의");
                email.putExtra(Intent.EXTRA_TEXT, "아이디 : \n\n문의사항 : ");
                startActivity(email);
            }
        });
    }


    private void WITHDRAW(){
        Dialog customDialog = new Dialog(this);
        customDialog.setContentView(R.layout.withdraw_dialog);
        customDialog.setCancelable(true);

        TextView dialogMessage = customDialog.findViewById(R.id.confirmTextView);
        Button dialogWithdraw = customDialog.findViewById(R.id.yesButton);
        Button dialogCancel = customDialog.findViewById(R.id.noButton);

        dialogWithdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog.dismiss();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference rootRef = database.getReference();
                DatabaseReference dataRef = rootRef.child("UserAccount");
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                String uidToDelete = user.getUid();
                dataRef.child(uidToDelete).removeValue()
                        .addOnSuccessListener(aVoid -> {
                            // 삭제 성공 시 실행할 코드
                        })
                        .addOnFailureListener(e -> {
                            // 삭제 실패 시 실행할 코드
                        });
                //user = FirebaseAuth.getInstance().getCurrentUser();

                user.delete()//계정 삭제 시키기
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(NewSettingActivity.this, "탈퇴 처리되었습니다.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                FirebaseAuth.getInstance().signOut();
                myStartActivity(LoginActivity.class);

            }
        });

        dialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog.dismiss();
            }
        });

        customDialog.show();

    }
    private void LOGOUT(){
        Dialog customDialog = new Dialog(this);
        customDialog.setContentView(R.layout.custom_dialog);
        customDialog.setCancelable(true);

        TextView dialogMessage = customDialog.findViewById(R.id.confirmTextView);
        Button dialogLogout = customDialog.findViewById(R.id.yesButton);
        Button dialogCancel = customDialog.findViewById(R.id.noButton);

        dialogLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 로그아웃 처리
                // 여기에서 로그아웃 코드를 추가하세요
                customDialog.dismiss();
                FirebaseAuth.getInstance().signOut();
                myStartActivity(LoginActivity.class);
            }
        });

        dialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog.dismiss();
            }
        });

        customDialog.show();
    }


    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
    }
    private boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return email.matches(emailPattern);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();

            // 이미지 뷰에 선택한 이미지 표시
            profileImg.setImageURI(selectedImageUri);

            // Firebase Storage에 이미지 업로드
            uploadImageToFirebase(selectedImageUri);
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        Toast.makeText(NewSettingActivity.this, "프로필 이미지 변경 중...", Toast.LENGTH_SHORT).show();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (imageUri != null) {
            StorageReference storageRef = storage.getReference().child("profile_images");
            StorageReference imageRef = storageRef.child(userId+".jpg"); // 저장될 파일 이름
            UploadTask uploadTask = imageRef.putFile(imageUri);

            uploadTask.addOnSuccessListener(taskSnapshot -> {
                // 업로드 성공 시 이미지 URL을 얻어올 수 있음
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();

                    DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
                    databaseRef.child("user_photo").child(userId).child("profileImageUrl").setValue(imageUrl)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    //Toast.makeText(SettingActivity.this, "프로필 이미지 변경 완료", Toast.LENGTH_SHORT).show();
                                    String localFilePath = getApplicationContext().getFilesDir() + "/local_image.jpg"; // 로컬에 저장할 파일 경로
                                    File localFile = new File(localFilePath);
                                    if (localFile.exists()) {
                                        localFile.delete();
                                    }
                                    imageRef.getFile(localFile).addOnSuccessListener(taskSnapshot2 -> {
                                        Toast.makeText(NewSettingActivity.this, "프로필 이미지 변경 완료", Toast.LENGTH_SHORT).show();
                                    }).addOnFailureListener(exception -> {
                                    }).addOnProgressListener(taskSnapshot2 -> {
                                    });
                                } else {
                                    Toast.makeText(NewSettingActivity.this, "프로필 이미지 변경 실패", Toast.LENGTH_SHORT).show();
                                }
                            });
                });
            }).addOnFailureListener(e -> {
                // 업로드 실패 시 에러 처리
                Toast.makeText(NewSettingActivity.this, "이미지 수정 실패", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void blindFrame(){
        if (detail_info_layout.getVisibility() == View.VISIBLE) {
            detail_info_layout.setVisibility(View.GONE);
        }
    }

    public void updateName(String updatedName) {
        NAME_SET_TEXT = updatedName;
    }

    public void updateIntroduce(String updatedIntroduce) {
        INTRODUCE_SET_TEXT = updatedIntroduce;
    }
    public void updateIsFragmentOpen(boolean isFragmentOpen) {
        is_Fragment_Open = isFragmentOpen;
    }


    private Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    private void myStartActivity(Class c){
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if(is_Fragment_Open){
            //getSupportFragmentManager().popBackStack();
            blindFrame();
            is_Fragment_Open=false;
        }
        else {
           myStartActivity(HomeMain.class);
        }
    }
}