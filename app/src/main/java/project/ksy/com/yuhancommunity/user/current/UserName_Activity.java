package project.ksy.com.yuhancommunity.user.current;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import project.ksy.com.yuhancommunity.MainActivity;
import project.ksy.com.yuhancommunity.R;

public class UserName_Activity extends AppCompatActivity {
    Button btnclose;
    TextView userName;
    private SharedPreferences mUserShared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_name_);
        mUserShared = getSharedPreferences("UserShared", Context.MODE_PRIVATE);
        btnclose = (Button) findViewById(R.id.onBtnSave);
        userName = (TextView) findViewById(R.id.username_tv);
        String name = mUserShared.getString("name", "");
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(UserName_Activity.this, "정보가 존재하지 않습니다.. 다시시도해주세요", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(UserName_Activity.this, MainActivity.class));
        }
        userName.setText(name);
        btnclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.pull_out_right, R.anim.pull_out_left);
            }
        });
    }
}
