package com.galid.ongtalk.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.galid.ongtalk.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class TestActivity extends AppCompatActivity {
    public static final String CHATROOMS = "testchatroom";
    public static final String USERS = "testusers";
    private String myUid;
    private String chatRoomUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);

        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @OnClick(R.id.test_send)
    public void send(){
        ChatModel chatModel = new ChatModel();
        chatModel.users.add(myUid);
        ChatModel.Message message = new ChatModel.Message();
        message.message = "Hi~~~~~~~~";
        message.uid = myUid;
        message.time = "11";
        chatModel.messages.add(message);

        FirebaseDatabase.getInstance().getReference().child(CHATROOMS).push().setValue(chatModel);
    }

    @OnClick(R.id.test_receive)
    public void receive(){
        FirebaseDatabase.getInstance().getReference().child(CHATROOMS).child(chatRoomUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ChatModel chatModel = dataSnapshot.getValue(ChatModel.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @OnClick(R.id.test_get)
    public void get(){
        FirebaseDatabase.getInstance().getReference().child(CHATROOMS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                chatRoomUid = dataSnapshot.getChildren().iterator().next().getKey();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
