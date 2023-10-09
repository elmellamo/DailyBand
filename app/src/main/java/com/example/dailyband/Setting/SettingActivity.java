package com.example.dailyband.Setting;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.dailyband.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class SettingActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_IMAGE_PICK = 1;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private ImageView profileImg;
    private Uri selectedImageUri = null;
    private Uri userImage=null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        profileImg = findViewById(R.id.bird_img);


        String userUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        StorageReference storageRef = storage.getReference().child("profile_images");
        StorageReference imageRef = storageRef.child(userUID+".jpg");

        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            String imageUrl = uri.toString();
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.brid_second_img)
                    .error(R.drawable.brid_second_img)
                    .into(profileImg); // profileImage는 앱의 이미지뷰 객체
        });

        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start photo picker to select an image
                openGallery();
            }
        });
    }
    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
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
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (imageUri != null) {
            StorageReference storageRef = storage.getReference().child("profile_images");
            StorageReference imageRef = storageRef.child(userId+".jpg"); // 저장될 파일 이름

            UploadTask uploadTask = imageRef.putFile(imageUri);

            uploadTask.addOnSuccessListener(taskSnapshot -> {
                // 업로드 성공 시 이미지 URL을 얻어올 수 있음
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    Toast.makeText(SettingActivity.this, "이미지 수정 성공", Toast.LENGTH_SHORT).show();

                    // TODO: imageUrl을 사용하여 데이터베이스에 저장하거나 다른 작업 수행

                    DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
                    databaseRef.child("user_photo").child(userId).child("profileImageUrl").setValue(imageUrl)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(SettingActivity.this, "이미지 수정 완료", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(SettingActivity.this, "이미지 수정 실패", Toast.LENGTH_SHORT).show();
                                }
                            });
                });
            }).addOnFailureListener(e -> {
                // 업로드 실패 시 에러 처리
                Toast.makeText(SettingActivity.this, "이미지 수정 실패", Toast.LENGTH_SHORT).show();
            });
        }
    }

}
