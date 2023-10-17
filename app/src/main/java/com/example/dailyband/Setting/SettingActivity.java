package com.example.dailyband.Setting;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.example.dailyband.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class SettingActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_IMAGE_PICK = 1;
    private FirebaseStorage storage;
    private DatabaseReference mDatabase;

    private StorageReference storageRef;
    private ImageView profileImg;
    private Uri selectedImageUri = null;
    private Uri userImage=null;
    private CardView cardView;
    private CardView cardView_email;
    private ConstraintLayout setting_user_layout;
    private ConstraintLayout setting_email_layout;
    private ConstraintLayout cardview_email_layout;

    private ConstraintLayout cardview_layout;
    private boolean isCardViewVisible = false;
    private EditText name_cardview_edittext;
    private EditText email_cardview_edittext;
    private String NAME_SET_TEXT;
    private ConstraintLayout touchable_cardview_layout;
    private Button change_name_btn;
    private String EMAIL_SET_TEXT;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        profileImg = findViewById(R.id.bird_img);
        Animation slideUpAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        cardView = findViewById(R.id.cardView);
        setting_user_layout = findViewById(R.id.setting_user_layout);
        cardview_layout = findViewById(R.id.cardview_layout);
        name_cardview_edittext = findViewById(R.id.name_cardview_edittext);
        cardView.setVisibility(View.INVISIBLE);
        cardview_layout.setVisibility((View.INVISIBLE));
        cardview_email_layout = findViewById(R.id.cardview_email_layout);
        cardView_email = findViewById(R.id.cardView_email);
        cardView_email.setVisibility(View.INVISIBLE);
        cardview_email_layout.setVisibility(View.INVISIBLE);
        setting_email_layout = findViewById(R.id.setting_email_layout);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        touchable_cardview_layout = findViewById(R.id.touchable_cardview_layout);
        email_cardview_edittext = findViewById(R.id.email_cardview_edittext);

        String userUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        StorageReference storageRef = storage.getReference().child("profile_images");
        StorageReference imageRef = storageRef.child(userUID+".jpg");
        change_name_btn = findViewById(R.id.change_name_btn);
        mDatabase.child(userUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                NAME_SET_TEXT = snapshot.child("name").getValue().toString();
                EMAIL_SET_TEXT = snapshot.child("emailId").getValue().toString();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            String imageUrl = uri.toString();
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.brid_second_img)
                    .error(R.drawable.brid_second_img)
                    .into(profileImg); // profileImage는 앱의 이미지뷰 객체
        });

        //사용자 이름 클릭하면 사용자 이름 수정하는 카드 뷰 띄우기
        setting_user_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isCardViewVisible) {
                    // 카드뷰가 보이지 않으면 스르륵 올라와서 보이게 함
                    cardView.setVisibility(View.VISIBLE);
                    cardview_layout.setVisibility(View.VISIBLE);
                    cardView.startAnimation(slideUpAnimation);
                    name_cardview_edittext.setText(NAME_SET_TEXT);
                    isCardViewVisible = true;
                    //touchable_cardview_layout.bringToFront();
                } else {
                    // 카드뷰가 이미 보이면 다시 숨김
                    cardView.setVisibility(View.INVISIBLE);
                    cardview_layout.setVisibility(View.INVISIBLE);
                    isCardViewVisible = false;
                }
            }
        });
        //외부 클릭시 카드뷰 없어지게끔
        setting_email_layout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (!isCardViewVisible) {
                    // 카드뷰가 보이지 않으면 스르륵 올라와서 보이게 함
                    cardView_email.setVisibility(View.VISIBLE);
                    cardview_email_layout.setVisibility(View.VISIBLE);
                    cardView_email.startAnimation(slideUpAnimation);
                    email_cardview_edittext.setText(EMAIL_SET_TEXT);
                    isCardViewVisible = true;
                    setting_user_layout.setClickable(false);
                    cardView.bringToFront();
                } else {
                    // 카드뷰가 이미 보이면 다시 숨김
                    cardView_email.setVisibility(View.INVISIBLE);
                    cardview_email_layout.setVisibility(View.INVISIBLE);
                    isCardViewVisible = false;
                }
            }
        });


        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 카드뷰를 터치해도 아무 동작 없음
            }
        });

        cardView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 포커스를 잃으면 키보드를 숨깁니다.
                hideKeyboard();
                return false;
            }
        });

        View rootView = findViewById(R.id.topscreen_setting);
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                if (isCardViewVisible) {
                    cardView.setVisibility(View.INVISIBLE);
                    cardview_layout.setVisibility(View.INVISIBLE);
                    cardview_email_layout.setVisibility(View.INVISIBLE);
                    cardView_email.setVisibility(View.INVISIBLE);

                    setting_user_layout.setClickable(true);
                    isCardViewVisible = false;
                }
            }
        });
        change_name_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tmpstr = name_cardview_edittext.getText().toString();
                mDatabase.child(userId).child("name").setValue(tmpstr);
                NAME_SET_TEXT = tmpstr;
                cardView.setVisibility(View.INVISIBLE);
                cardview_layout.setVisibility(View.INVISIBLE);

                isCardViewVisible = false;
            }
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

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
