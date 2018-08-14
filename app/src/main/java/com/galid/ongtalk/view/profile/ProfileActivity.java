package com.galid.ongtalk.view.profile;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.galid.ongtalk.R;
import com.galid.ongtalk.model.UserModel;
import com.galid.ongtalk.view.chat.ChatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity{

    @BindView(R.id.imageview_profileactivity_profileimage)
    public CircleImageView imageViewOpponentProfile;
    @BindView(R.id.textview_profileactivity_name)
    public TextView textViewOpponentName;
    @BindView(R.id.textview_profileactivity_phone)
    public TextView textViewopponentPhone;

    private UserModel opponent;

    public static Intent newIntent(Context context, UserModel opponent){
        Intent intent = new Intent(context, ProfileActivity.class);
        intent.putExtra(ChatActivity.OPPONENT, opponent);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        opponent = (UserModel) getIntent().getSerializableExtra(ChatActivity.OPPONENT);
        bindView(opponent);
    }

    public void bindView(UserModel opponentUserModel){
        textViewOpponentName.setText(opponentUserModel.userName);
        //TODO PHONE 넘버
        Glide.with(imageViewOpponentProfile)
                .load(opponentUserModel.profileImageUrl)
                .into(imageViewOpponentProfile);
    }

    // 채팅 버튼 클릭시
    @OnClick(R.id.imageview_profileactivity_chat)
    public void showChatActivity(){
        Intent intent = ChatActivity.newIntent(this, opponent);
        startActivity(intent);
        finish();
    }

}
