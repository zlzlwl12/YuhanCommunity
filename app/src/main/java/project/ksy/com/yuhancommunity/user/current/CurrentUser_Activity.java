package project.ksy.com.yuhancommunity.user.current;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import project.ksy.com.yuhancommunity.MainActivity;
import project.ksy.com.yuhancommunity.ProgressDialog_yh;
import project.ksy.com.yuhancommunity.R;
import project.ksy.com.yuhancommunity.user.current.freeboard.Freeboard_Activity_Listview;
import project.ksy.com.yuhancommunity.user.management.UserData;


public class CurrentUser_Activity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ProgressDialog_yh progressDialog_yh;
    final static String TAG = "CurrentUser_Activity";
    private ImageView profileimg;
    private SharedPreferences mUserShared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_user_);
        init();
        if (firebaseUser == null) {
            Toast.makeText(getApplicationContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(CurrentUser_Activity.this, MainActivity.class));
        }
        getUserdata();
    }
    private void init(){
        profileimg = (ImageView) findViewById(R.id.userdata_view);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("users");
        mUserShared = getSharedPreferences("UserShared", Context.MODE_PRIVATE);
        progressDialog_yh = new ProgressDialog_yh(CurrentUser_Activity.this);
    }
    // 데이터를 얻어옴.
    private void getUserdata() {
        progressDialog_yh.show();


        databaseReference.child(firebaseAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog_yh.dismiss();
                if (dataSnapshot.getValue() != null) {
                    UserData userData = dataSnapshot.getValue(UserData.class);
//                app 내에서 사용하는 데이터 set
                    SharedPreferences.Editor editor = mUserShared.edit();
                    editor.putString("name", userData.getUserName());
                    editor.putString("major", userData.getUserMajor());
                    editor.putString("phoneNum", userData.getUserPhoneNum());
                    editor.putString("email", firebaseUser.getEmail());
                    editor.putString("photo", userData.getUserprofileimg());
                    editor.commit();
                    Glide.with(CurrentUser_Activity.this).load(userData.getUserprofileimg()).apply(RequestOptions.bitmapTransform(new CircleCrop())).into(profileimg);
                } else {
                    Toast.makeText(CurrentUser_Activity.this, "정보를 가져오지 못했습니다.. 다시 로그인 해주세요!.", Toast.LENGTH_SHORT).show();
                    firebaseAuth.signOut();
                    finish();
                    startActivity(new Intent(CurrentUser_Activity.this, MainActivity.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, databaseError.toString());
            }
        });

    }

    //    유저 데이터 관리 페이지.
    public void onUserData(View v) {
        Intent udaIntent = new Intent(CurrentUser_Activity.this, ShowUserData.class);
        startActivity(udaIntent);
    }

    //    로그아웃
    public void onSignOut(View v) {
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(CurrentUser_Activity.this, MainActivity.class));
    }

    //    자유게시판
    public void onBtnFreeBoard(View v) {
        Intent freeboardIntent = new Intent(CurrentUser_Activity.this, Freeboard_Activity_Listview.class);
        startActivity(freeboardIntent);
    }
}
