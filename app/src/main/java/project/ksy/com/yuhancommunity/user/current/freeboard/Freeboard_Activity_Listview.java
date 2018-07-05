package project.ksy.com.yuhancommunity.user.current.freeboard;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

import project.ksy.com.yuhancommunity.MainActivity;
import project.ksy.com.yuhancommunity.ProgressDialog_yh;
import project.ksy.com.yuhancommunity.R;

/**
 * Created by kor on 2017-09-19.
 * sung youn kim
 * practice listview freeboard
 */

public class Freeboard_Activity_Listview extends AppCompatActivity {

    private ListView listView;
    private ArrayList<Freeboard_item> items = new ArrayList<>();
    private ProgressDialog_yh progressDialog_yh;
    private Freeboard_Adapter_list freeboard_adapter_list;
    private DatabaseReference databaseReference;
    private Query query;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_freeboard_listview);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        progressDialog_yh = new ProgressDialog_yh(this);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        listView = (ListView) findViewById(R.id.listview);
        query = databaseReference.child("yuhanboard").orderByChild("starCount");

        if (firebaseAuth.getCurrentUser() == null) {
            Toast.makeText(Freeboard_Activity_Listview.this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            finish();
            firebaseAuth.signOut();
            startActivity(new Intent(Freeboard_Activity_Listview.this, MainActivity.class));
        }
//        게시글 가져오기.
        getFreeboardDataindex();
    }

    private void getFreeboardData() {
        progressDialog_yh.setMessage("잠시만 기다려주세요..");
        progressDialog_yh.show();
        databaseReference.child("yuhanboard").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getValue(Freeboard_item.class) == null) {
                    progressDialog_yh.dismiss();
                    Toast.makeText(getApplicationContext(), "게시글이 없습니다.", Toast.LENGTH_SHORT).show();
                }
                progressDialog_yh.dismiss();
                Freeboard_item freeboard_item = dataSnapshot.getValue(Freeboard_item.class);
                items.add(freeboard_item);
                freeboard_adapter_list = new Freeboard_Adapter_list(items, Freeboard_Activity_Listview.this, R.layout.freeboard_listview_item);
                listView.setAdapter(freeboard_adapter_list);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        freeboard_adapter_list = new Freeboard_Adapter_list(items, this, R.layout.freeboard_listview_item);
        listView.setAdapter(freeboard_adapter_list);
    }

    private void getFreeboardDataindex() {
        progressDialog_yh.setMessage("잠시만 기다려주세요..");
        progressDialog_yh.show();
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getValue(Freeboard_item.class) == null) {
                    progressDialog_yh.dismiss();
                    Toast.makeText(getApplicationContext(), "게시글이 없습니다.", Toast.LENGTH_SHORT).show();
                }
                progressDialog_yh.dismiss();
                Freeboard_item freeboard_item = dataSnapshot.getValue(Freeboard_item.class);
                items.add(freeboard_item);
                freeboard_adapter_list = new Freeboard_Adapter_list(items, Freeboard_Activity_Listview.this, R.layout.freeboard_listview_item);
                listView.setAdapter(freeboard_adapter_list);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        freeboard_adapter_list = new Freeboard_Adapter_list(items, this, R.layout.freeboard_listview_item);
        listView.setAdapter(freeboard_adapter_list);
    }

    public void onBtnNewBoard(View v) {
        Intent newPostIntent = new Intent(Freeboard_Activity_Listview.this, Freeboard_newBoard_Activity.class);
        newPostIntent.putExtra("size",items.size());
        startActivity(newPostIntent);
        overridePendingTransition(R.anim.pull_in_left, R.anim.pull_in_right);
    }

}
