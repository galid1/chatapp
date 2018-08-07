package com.galid.ongtalk.view.profile;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.galid.ongtalk.R;
import com.galid.ongtalk.view.chat.ChatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProfileActivity extends AppCompatActivity{

    private String destinationUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        destinationUid = getIntent().getStringExtra(ChatActivity.DESTINATION_UID);
    }

    // 채팅 버튼 클릭시
    @OnClick(R.id.imageview_profileactivity_chat)
    public void showChatActivity(){
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(ChatActivity.DESTINATION_UID, destinationUid);
        startActivity(intent);
        finish();
    }

}
