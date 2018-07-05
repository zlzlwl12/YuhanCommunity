package project.ksy.com.yuhancommunity.user.management;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import project.ksy.com.yuhancommunity.ProgressDialog_yh;
import project.ksy.com.yuhancommunity.R;

public class RegisterUser_Activity extends AppCompatActivity {
    private final static String TAG = "RegisterUser_Activity";
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    ;
    EditText useremail_register;

    EditText userpassword_register;

    ProgressDialog_yh progressDialog_yh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_user_);
        progressDialog_yh = new ProgressDialog_yh(this);
        useremail_register = (EditText) findViewById(R.id.ed_useremail_register);
        userpassword_register = (EditText) findViewById(R.id.ed_userpw_register);
    }


    private void createNewUser() {
        String userEmail = useremail_register.getText().toString().trim();
        String userPassword = userpassword_register.getText().toString().trim();

        if (TextUtils.isEmpty(userEmail) && TextUtils.isEmpty(userPassword)) {
//            email or password is empty
            Toast.makeText(RegisterUser_Activity.this, "이메일과 비밀번호를 입력하세요. ", Toast.LENGTH_SHORT).show();
            return;
        }
//        Log.d("TAG", userEmail+"\n"+userPassword);
        progressDialog_yh.setMessage("잠시만 기다려주세요...");
        progressDialog_yh.show();
        firebaseAuth.createUserWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(RegisterUser_Activity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog_yh.dismiss();
                if (task.isSuccessful()) {
                    Toast.makeText(RegisterUser_Activity.this, "회원정보 입력 페이지로 이동합니다.", Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(new Intent(RegisterUser_Activity.this, UserProfile_Activity.class));
                } else {
                    Toast.makeText(RegisterUser_Activity.this, "이메일 비밀번호 형식이 맞지않습니다..", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void onBtnNewUser(View v) {
        createNewUser();
    }
}
