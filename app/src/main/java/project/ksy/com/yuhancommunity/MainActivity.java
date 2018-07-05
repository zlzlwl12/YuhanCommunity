package project.ksy.com.yuhancommunity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import project.ksy.com.yuhancommunity.user.current.CurrentUser_Activity;
import project.ksy.com.yuhancommunity.user.current.ShowUserData;
import project.ksy.com.yuhancommunity.user.management.RegisterUser_Activity;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = "MainActivity";
    private FirebaseAuth firebaseAuth;
    private ProgressDialog_yh progressDialog_yh;
    private String[] permissions = {android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA};// 권한설정 변수
    private static final int MUTIPLE_PERMISSIONS = 101; // 권한동의 여부 등을 묻고 CALL BACK 함수에 쓰일 변수

    @BindView(R.id.ed_useremail)
    EditText ed_userEmail;

    @BindView(R.id.ed_userpassword)
    EditText ed_userPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        checkPermissions();
        progressDialog_yh = new ProgressDialog_yh(this);
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
//            로그인 중일때.
            startActivity(new Intent(MainActivity.this, ShowUserData.class));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MUTIPLE_PERMISSIONS: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++) {
                        if (permissions[i].equals(this.permissions[0])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showNoPermissionToastAndFinish();
                            }
                        } else if (permissions[i].equals(this.permissions[1])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showNoPermissionToastAndFinish();
                            }
                        } else if (permissions[i].equals(this.permissions[2])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showNoPermissionToastAndFinish();
                            }
                        }
                    }
                } else {
                    showNoPermissionToastAndFinish();
                }
                return;

            }
        }
    }

    //    권한 획득에 동의하지않은 경우 아래 Toast 메세지를 띄우고 Activity 종료
    private void showNoPermissionToastAndFinish() {
        Toast.makeText(this, "권한 요청에 동의 해주셔야 이용 가능합니다. 설정에서 권한 허용 하시기 바랍니다.", Toast.LENGTH_SHORT).show();
        finish();
    }

    private boolean checkPermissions() {
        int result;
        List<String> permissionList = new ArrayList<>();
        for (String pm : permissions) {
            result = ContextCompat.checkSelfPermission(MainActivity.this, pm);
            if (result != PackageManager.PERMISSION_GRANTED) { // 사용자가 해당 권한을 가지고 있지 않을 경우 리스트에 해당 권한명 추가
                permissionList.add(pm);
            }
        }
        if (!permissionList.isEmpty()) { // 권한이 추가되었으면 해당 리스트가 empty 가 아니므로 request 즉 권한을 요청
            ActivityCompat.requestPermissions(MainActivity.this, permissionList.toArray(new String[permissionList.size()]), MUTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    private void checkLogin() {
        String email = ed_userEmail.getText().toString().trim();
        String password = ed_userPassword.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "이메일을 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog_yh.setMessage("로그인중 입니다..");
        progressDialog_yh.show();
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog_yh.dismiss();
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "로그인 되었습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(new Intent(MainActivity.this, ShowUserData.class));
                }
            }
        });
    }

    @OnClick(R.id.loginBtn)
    public void OnBtnlogin(Button btn) {
        checkLogin();
    }

    @OnClick(R.id.jointv)
    public void OnJoinTv(TextView tv) {
        finish();
        startActivity(new Intent(MainActivity.this, RegisterUser_Activity.class));
    }
}



