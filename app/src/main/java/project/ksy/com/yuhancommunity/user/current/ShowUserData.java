package project.ksy.com.yuhancommunity.user.current;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import project.ksy.com.yuhancommunity.MainActivity;
import project.ksy.com.yuhancommunity.R;
import project.ksy.com.yuhancommunity.user.current.freeboard.Freeboard_Activity_Listview;

public class ShowUserData extends AppCompatActivity {
    final static String TAG = "ShowUserData";
    private TextView tv_uName;
    private TextView tv_uEmail;
    private TextView tv_uPhoneNum;
    private TextView tv_logout;
    private ImageView iuserImg;
    private FirebaseAuth firebaseAuth;
    private SharedPreferences mUserShared;

    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseAuth.getCurrentUser() == null) {
            Toast.makeText(getApplicationContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            finish();
            firebaseAuth.signOut();
            startActivity(new Intent(ShowUserData.this, MainActivity.class));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_user_data);
        initView();
        firebaseAuth = FirebaseAuth.getInstance();
        mUserShared = getSharedPreferences("UserShared", Context.MODE_PRIVATE);
        String name = mUserShared.getString("name", "");
        String major = mUserShared.getString("major", "");
        String phoneNum = mUserShared.getString("phoneNum", "");
        String photo = mUserShared.getString("photo", "");
        Uri profileUri = Uri.parse(photo);
        String email = mUserShared.getString("email", "");
        if (TextUtils.isEmpty(name) && TextUtils.isEmpty(major) && TextUtils.isEmpty(phoneNum) && TextUtils.isEmpty(email) && TextUtils.isEmpty(photo)) {
            Toast.makeText(ShowUserData.this, "데이터 정보가 존재하지 않습니다.. 다시 시도해 주세요", Toast.LENGTH_SHORT).show();
            finish();
            firebaseAuth.signOut();
            startActivity(new Intent(ShowUserData.this, MainActivity.class));
        }
        Log.d(TAG, name + "\n" + major + "\n" + email + "\n" + phoneNum);
        tv_uName.setText(name);
        tv_uEmail.setText(email);
        tv_uPhoneNum.setText(phoneNum);
        Glide.with(ShowUserData.this).load(profileUri).into(iuserImg);

    }

    public void onUserName(View v) {
        Log.d(TAG, "onUserName");
        Intent nameIntent = new Intent(ShowUserData.this, UserName_Activity.class);
        startActivity(nameIntent);
        overridePendingTransition(R.anim.pull_in_left, R.anim.pull_in_right);
    }
    public void onLogout(View v){
        Log.d(TAG, "onLogout");
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(ShowUserData.this, MainActivity.class));
    }

    public void onGoFreeboard(View v){
        Toast.makeText(ShowUserData.this, "게시판으로 이동합니다." ,Toast.LENGTH_SHORT).show();
        Intent freeboardIntent = new Intent(ShowUserData.this, Freeboard_Activity_Listview.class);
        startActivity(freeboardIntent);
    }

    public void initView() {
        tv_uName = (TextView) findViewById(R.id.ontv_username);
        tv_uEmail = (TextView) findViewById(R.id.ontv_useremail);
        tv_uPhoneNum = (TextView) findViewById(R.id.tv_phoneNum);
        iuserImg = (ImageView) findViewById(R.id.userimg_profile);
    }
}
