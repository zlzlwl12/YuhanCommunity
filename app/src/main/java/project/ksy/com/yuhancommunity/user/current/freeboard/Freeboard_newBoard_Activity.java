package project.ksy.com.yuhancommunity.user.current.freeboard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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
import java.util.Calendar;
import java.util.Locale;

import project.ksy.com.yuhancommunity.MainActivity;
import project.ksy.com.yuhancommunity.ProgressDialog_yh;
import project.ksy.com.yuhancommunity.R;

public class Freeboard_newBoard_Activity extends AppCompatActivity implements View.OnClickListener {
    private ImageView img_post;
    private EditText ed_posting;
    private int count;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebasedatabase;
    private DatabaseReference databasereference;
    private Uri filePath;
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_IMAGE_REQUEST = 2;
    private Uri imgStorageUri_freeboard;
    private String mCurrentPhotoPath;
    private Button cameraTv, albumTv;
    private TextView postingTv;
    private ProgressDialog_yh progressDialog_yh;
    private StorageReference storagereference;
    private final String TAG = "Freeboard_newBoard_Activity";
    private SharedPreferences mUserShared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_freeboard_new_board_);
        ed_posting = (EditText) findViewById(R.id.post_edt);
        img_post = (ImageView) findViewById(R.id.post_img);
        cameraTv = (Button) findViewById(R.id.camera_tv);
        albumTv = (Button) findViewById(R.id.album_tv);
        postingTv = (TextView) findViewById(R.id.tv_posting);
        firebaseAuth = FirebaseAuth.getInstance();
        firebasedatabase = FirebaseDatabase.getInstance();
        databasereference = firebasedatabase.getReference();
        storagereference = FirebaseStorage.getInstance().getReference();
        mUserShared = getSharedPreferences("UserShared", Context.MODE_PRIVATE);
        progressDialog_yh = new ProgressDialog_yh(this);
        cameraTv.setOnClickListener(this);
        albumTv.setOnClickListener(this);
        Intent intent = getIntent();
        intent.getIntExtra("size", 0);
        if (firebaseAuth.getCurrentUser() == null) {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            finish();
            firebaseAuth.signOut();
            startActivity(new Intent(Freeboard_newBoard_Activity.this, MainActivity.class));
        }

    }

    public void onTvPosting(View v) {
        posting();
    }

    private void posting() {
        String name = mUserShared.getString("name", "");
        String major = mUserShared.getString("major", "");
        String photo = mUserShared.getString("photo", "");
        String postComment = ed_posting.getText().toString();
        String postImg = imgStorageUri_freeboard.toString().trim();
        if (postComment.trim().contains("시발") || postComment.trim().contains("개새끼") || postComment.trim().contains("fuck") || postComment.trim().contains("병신")) {
            Toast.makeText(Freeboard_newBoard_Activity.this, "욕설은 안되요 ㅠㅠ", Toast.LENGTH_SHORT).show();
            return;
        }
        String date = currentTime();
        Freeboard_item Post_items = new Freeboard_item(name, major, photo, postImg, postComment, count);
        databasereference.child("yuhanboard").child(date).setValue(Post_items).
                addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog_yh.dismiss();
                        if (task.isSuccessful()) {
                            Toast.makeText(Freeboard_newBoard_Activity.this, "게시글 작성이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                            finish();
                            startActivity(new Intent(Freeboard_newBoard_Activity.this, Freeboard_Activity_Listview.class));
                        } else {
                            Toast.makeText(Freeboard_newBoard_Activity.this, "게시글 작성이 실패하였습니다.. 다시시도해 주세요", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void UploadFile() {
        progressDialog_yh.setMessage("잠시만 기다려주세요..");
        progressDialog_yh.show();
        if (filePath != null) {
            StorageReference riversRef = storagereference.child("images/" + "yuhanboard/" + currentTime() + "_yuhanboard.jpg");
            riversRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @SuppressWarnings("VisibleForTests")
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Log.d(TAG, "업로드 성공");
                            progressDialog_yh.dismiss();
                            imgStorageUri_freeboard = taskSnapshot.getDownloadUrl();
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

    //    album select photo
    private void ShowFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "select An Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onClick(View v) {
        if (v == cameraTv) {
            takePhoto();
        } else if (v == albumTv) {
            ShowFileChooser();
        }
    }

    //    take picture
    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); // 사진을 찍기 위하여 설정.
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException e) {
            Toast.makeText(Freeboard_newBoard_Activity.this, "이미지 처리오류 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
            finish();
            e.printStackTrace();
        }
        if (photoFile != null) {
            filePath = FileProvider.getUriForFile(Freeboard_newBoard_Activity.this, "project.ksy.com.yuhancommunity.provider", photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, filePath); // 사진을 찍어 해당 Content uri 를 photoUri 에 적용시키기 위함
            startActivityForResult(intent, PICK_FROM_CAMERA);
        }
    }

    // create image file
    private File createImageFile() throws IOException {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat timeStamp = new SimpleDateFormat("HHmmss", Locale.KOREAN);
//        String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
        String date = timeStamp.format(cal.getTime());
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
                img_post.setImageBitmap(bitmap);
                UploadFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == PICK_FROM_CAMERA && resultCode == RESULT_OK) {
            MediaScannerConnection.scanFile(Freeboard_newBoard_Activity.this,
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
                img_post.setImageBitmap(thumbImage);
                UploadFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    private String currentTime() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("HHmmss", Locale.KOREAN);
        String date = format.format(cal.getTime());
        return date;
    }
}
