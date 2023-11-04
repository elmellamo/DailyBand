package com.example.dailyband.Setting;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.example.dailyband.Login.LoginActivity;
import com.example.dailyband.Login.RegisterActivity;
import com.example.dailyband.R;
import com.example.dailyband.Start.SplashActivity;
import com.example.dailyband.Utils.SharedData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

public class SettingActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_IMAGE_PICK = 1;
    private FirebaseStorage storage;
    private DatabaseReference mDatabase;

    private StorageReference storageRef;
    private ImageView profileImg;
    private ImageView tmpImg;
    private Uri selectedImageUri = null;
    private Uri userImage=null;
    private CardView cardView;
    private CardView cardView_email;
    private ConstraintLayout setting_user_layout;
    private ConstraintLayout setting_email_layout;
    private ConstraintLayout cardview_email_layout;
    private ConstraintLayout cardview_layout;
    private ConstraintLayout setting_withdrawal_layout;
    private ConstraintLayout setting_logout_layout;
    private ConstraintLayout setting_contact_layout;
    private boolean isCardViewVisible = false;
    private EditText name_cardview_edittext;
    private EditText email_cardview_edittext;
    private EditText email_pw_cardview_edittext;
    private String NAME_SET_TEXT;
    private ConstraintLayout touchable_cardview_layout;
    private Button change_name_btn;
    private Button change_email_btn;
    public String EMAIL_SET_TEXT;


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
        change_email_btn = findViewById(R.id.change_email_btn);
        email_pw_cardview_edittext = findViewById(R.id.email_pw_cardview_edittext);
        setting_withdrawal_layout = findViewById(R.id.setting_withdrawal_layout);
        setting_logout_layout = findViewById(R.id.setting_logout_layout);
        setting_contact_layout = findViewById(R.id.setting_contact_layout);

        String userUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        StorageReference storageRef = storage.getReference().child("profile_images");
        StorageReference imageRef = storageRef.child(userUID+".jpg");
        change_name_btn = findViewById(R.id.change_name_btn);
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




        String localFilePath = getApplicationContext().getFilesDir() + "/local_image.jpg"; // 로컬에 저장할 파일 경로
        File localFile = new File(localFilePath); // 이미지 파일의 로컬 경로
        if (localFile.exists()) {
            //ImageView imageView7 = findViewById(R.id.imageView7);

            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());

            int rotation = 0;
            try {
                ExifInterface exif = new ExifInterface(localFilePath);
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

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
                Toast.makeText(SettingActivity.this, "로컬 파일 존재", Toast.LENGTH_SHORT).show();
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
                    //cardView.bringToFront();
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

        cardView_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 카드뷰를 터치해도 아무 동작 없음
            }
        });
        cardView_email.setOnTouchListener(new View.OnTouchListener() {
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
                mDatabase.child("UserAccount").child(userId).child("name").setValue(tmpstr);
                NAME_SET_TEXT = tmpstr;
                cardView.setVisibility(View.INVISIBLE);
                cardview_layout.setVisibility(View.INVISIBLE);

                isCardViewVisible = false;
            }
        });

        change_email_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeEmail();
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
                                    Toast.makeText(SettingActivity.this, "탈퇴되었습니다.", Toast.LENGTH_SHORT).show();
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

    private void changeEmail(){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        String newEmail = email_cardview_edittext.getText().toString(); // 새로운 이메일
        String password = email_pw_cardview_edittext.getText().toString(); // 현재 비밀번호

        AuthCredential credential = EmailAuthProvider.getCredential(mAuth.getCurrentUser().getEmail(), password);
        FirebaseUser user = mAuth.getCurrentUser();

        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            if (!isValidEmail(newEmail)) {
                                Toast.makeText(SettingActivity.this, "이메일 주소가 유효하지 않습니다.", Toast.LENGTH_SHORT).show();
                                hideKeyboard();
                                return;
                            }
                            Toast.makeText(SettingActivity.this, "유효한 이메일 주소입니다.",Toast.LENGTH_SHORT).show();
                            //여기까지 작동 정상적

                            // 이메일 업데이트
                            user.updateEmail(newEmail.toString())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(SettingActivity.this, "이메일 수정에 성공하였습니다.", Toast.LENGTH_SHORT).show();
                                                realtime_change_email(newEmail.toString());

                                            } else {
                                                Exception e = task.getException();
                                                Log.e("EmailUpdateError", e.getMessage());
                                                Toast.makeText(SettingActivity.this, EMAIL_SET_TEXT, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(SettingActivity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                            hideKeyboard();
                        }
                    }
                });

    }

    private void realtime_change_email(String newEmail){
        String userUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabase.child("UserAccount").child(userUID).child("emailId").setValue(newEmail);
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
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (imageUri != null) {
            StorageReference storageRef = storage.getReference().child("profile_images");
            StorageReference imageRef = storageRef.child(userId+".jpg"); // 저장될 파일 이름
            UploadTask uploadTask = imageRef.putFile(imageUri);

            uploadTask.addOnSuccessListener(taskSnapshot -> {
                // 업로드 성공 시 이미지 URL을 얻어올 수 있음
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    Toast.makeText(SettingActivity.this, "이미지 변경 중...", Toast.LENGTH_SHORT).show();

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
                                        // 다운로드 성공
                                        // localFilePath에 이미지가 저장됨
                                        // 여기서 UI 업데이트 등을 수행할 수 있습니다.
                                        Toast.makeText(SettingActivity.this, "프로필 이미지 변경 완료", Toast.LENGTH_SHORT).show();
                                    }).addOnFailureListener(exception -> {
                                        // 다운로드 실패
                                    }).addOnProgressListener(taskSnapshot2 -> {
                                        // 다운로드 진행 중
                                    });
                                } else {
                                    Toast.makeText(SettingActivity.this, "프로필 이미지 변경 실패", Toast.LENGTH_SHORT).show();
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

    private Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    private void myStartActivity(Class c){
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
