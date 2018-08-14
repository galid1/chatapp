package com.galid.ongtalk.view.profile;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.galid.ongtalk.R;
import com.galid.ongtalk.view.chat.ChatActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProfileActivity extends AppCompatActivity{

    private String opponentUid;

    public static Intent newIntent(Context context, String opponentUid){
        Intent intent = new Intent(context, ProfileActivity.class);
        intent.putExtra(ChatActivity.DESTINATION_UID, opponentUid);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        opponentUid = getIntent().getStringExtra(ChatActivity.DESTINATION_UID);
    }

    // 채팅 버튼 클릭시
    @OnClick(R.id.imageview_profileactivity_chat)
    public void showChatActivity(){
        Intent intent = ChatActivity.newIntent(this, opponentUid);
        startActivity(intent);
        finish();
    }

}
