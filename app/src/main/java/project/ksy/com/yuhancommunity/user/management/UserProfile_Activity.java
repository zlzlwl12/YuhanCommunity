package project.ksy.com.yuhancommunity.user.management;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import project.ksy.com.yuhancommunity.ProgressDialog_yh;
import project.ksy.com.yuhancommunity.R;
import project.ksy.com.yuhancommunity.user.current.CurrentUser_Activity;

public class UserProfile_Activity extends AppCompatActivity implements View.OnClickListener {
    private ArrayAdapter arrayAdapter;
    private Spinner spinner;
    private EditText ed_usetPhoneNum, ed_userName;
    private RadioGroup userGender;
    private ProgressDialog_yh progressDialog_yh;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private ImageView userProfileImg;
    private ImageButton cameraBtn, albumBtn;
    private Uri filePath;
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_IMAGE_REQUEST = 2;
    private String mCurrentPhotoPath;
    private StorageReference storagereference;
    private Uri imgStorageUri;
    private final static String TAG = "UserProfile_Activity";

    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseAuth.getCurrentUser() == null) {
            Toast.makeText(getApplicationContext(), "이메일 인증이 필요합니다.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_);

//        firebase
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storagereference = FirebaseStorage.getInstance().getReference();

//        progressDialog
        progressDialog_yh = new ProgressDialog_yh(this);

        ed_userName = (EditText) findViewById(R.id.ed_userName);
        ed_usetPhoneNum = (EditText) findViewById(R.id.ed_userphone);
        userGender = (RadioGroup) findViewById(R.id.usermale);
        userProfileImg = (ImageView) findViewById(R.id.userImage_Profile);
        spinner = (Spinner) findViewById(R.id.majorSpinner);
        cameraBtn = (ImageButton) findViewById(R.id.BtnCamera);
        albumBtn = (ImageButton) findViewById(R.id.BtnAlbum);

        arrayAdapter = ArrayAdapter.createFromResource(this, R.array.major, android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        cameraBtn.setOnClickListener(this);
        albumBtn.setOnClickListener(this);
    }

    public void onBtnNewUser(View v) {
        saveUserData();
    }

    //    user data save
    private void saveUserData() {
        int checkedGender = userGender.getCheckedRadioButtonId();
        String userName = ed_userName.getText().toString().trim();
        String usetPhoneNum = ed_usetPhoneNum.getText().toString().trim();
        String userMajor = spinner.getSelectedItem().toString().trim();
        String userGender = ((RadioButton) findViewById(checkedGender)).getText().toString().trim();
        String userProfile = imgStorageUri.toString().trim();
        if (TextUtils.isEmpty(userName)) {
            Toast.makeText(getApplicationContext(), "이름을 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(usetPhoneNum)) {
            Toast.makeText(getApplicationContext(), "비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(userMajor)) {
            Toast.makeText(getApplicationContext(), "전공을 선택하세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(userGender)) {
            Toast.makeText(getApplicationContext(), "성별을 선택하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        UserData userData = new UserData(userMajor, userName, usetPhoneNum, userGender, userProfile);
        progressDialog_yh.setMessage("정보를 저장하는중입니다..");
        progressDialog_yh.show();
        databaseReference.child("users").child(firebaseAuth.getCurrentUser().getUid()).setValue(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog_yh.dismiss();
                if (task.isSuccessful()) {
                    Toast.makeText(UserProfile_Activity.this, "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(new Intent(UserProfile_Activity.this, CurrentUser_Activity.class));
                } else {
                    Toast.makeText(UserProfile_Activity.this, "회원가입이 실패하였습니다.. 다시시도해 주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void UploadFile() {
        progressDialog_yh.setMessage("잠시만 기다려주세요..");
        progressDialog_yh.show();
        if (filePath != null) {
            StorageReference riversRef = storagereference.child("images/" + firebaseAuth.getCurrentUser().getEmail() + "_profile.jpg");
            riversRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Log.d(TAG, "업로드 성공");
                            progressDialog_yh.dismiss();
                            //noinspection VisibleForTests
                            imgStorageUri = taskSnapshot.getDownloadUrl();
                            //noinspection VisibleForTests
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            //display error Toast

        }

    }

    private void ShowFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "select An Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onClick(View v) {
        if (v == cameraBtn) {
            takePhoth();
        } else if (v == albumBtn) {
            ShowFileChooser();
        }
    }

    private void takePhoth() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); // 사진을 찍기 위하여 설정.
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException e) {
            Toast.makeText(UserProfile_Activity.this, "이미지 처리오류 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
            finish();
            e.printStackTrace();
        }
        if (photoFile != null) {
            filePath = FileProvider.getUriForFile(UserProfile_Activity.this, "project.ksy.com.yuhancommunity.provider", photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, filePath); // 사진을 찍어 해당 Content uri 를 photoUri 에 적용시키기 위함
            startActivityForResult(intent, PICK_FROM_CAMERA);
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
        String imageFileName = "test_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/Yuhan/"); // Yuhan 이라는 경로에 이미지를 저장하기 위함
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        mCurrentPhotoPath = "file : " + image.getAbsolutePath();   //external 절대경로로 사진 저장경로지정
        return image;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                userProfileImg.setImageBitmap(bitmap);
                UploadFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == PICK_FROM_CAMERA && resultCode == RESULT_OK) {
            MediaScannerConnection.scanFile(UserProfile_Activity.this,
                    new String[]{filePath.getPath()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        @Override
                        public void onScanCompleted(String path, Uri uri) {
                        }
                    });
            galleryAddPic();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), filePath);
                Bitmap thumbImage = ThumbnailUtils.extractThumbnail(bitmap, 1028, 1028);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                thumbImage.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                userProfileImg.setImageBitmap(thumbImage);
                UploadFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }


}
