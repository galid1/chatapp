package com.galid.ongtalk.view.profile;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.galid.ongtalk.R;
import com.galid.ongtalk.model.UserModel;
import com.galid.ongtalk.view.chat.ChatActivity;
import com.galid.ongtalk.view.profile.fragment.MyProfileFragment;
import com.galid.ongtalk.view.profile.fragment.OtherProfileFragment;
import com.galid.ongtalk.view.profile.fragment.ProfileImageFragment;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileActivity extends AppCompatActivity implements ShowImage{

    public static Intent newIntent(Context context, UserModel opponent){
        Intent intent = new Intent(context, ProfileActivity.class);
        intent.putExtra(ChatActivity.OPPONENT, opponent);
        return intent;
    }

    private String myUid;
    private UserModel opponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        opponent = (UserModel) getIntent().getSerializableExtra(ChatActivity.OPPONENT);

        // 내프로필 클릭됨
        if(myUid.equals(opponent.uid)){
            attachMyProfileFragment();
        }// 다른사람의 프로필 클릭됨
        else{
            attachOtherProfileFragment();
        }
    }

    public void attachMyProfileFragment(){
        MyProfileFragment myProfileFragment = MyProfileFragment.newInstance(opponent);
        Fragment fragment = getFragmentManager().findFragmentById(R.id.framelayout_profileactivity_fragment);

        if(fragment == null){
            getFragmentManager().beginTransaction().add(R.id.framelayout_profileactivity_fragment, myProfileFragment).commit();
        }
        else{
            getFragmentManager().beginTransaction().replace(R.id.framelayout_profileactivity_fragment, myProfileFragment, MyProfileFragment.TAG_FRAGMENT).commit();
        }
    }

    public void attachOtherProfileFragment(){
        OtherProfileFragment otherProfileFragment = OtherProfileFragment.newInstance(opponent);
        Fragment fragment = getFragmentManager().findFragmentById(R.id.framelayout_profileactivity_fragment);

        if(fragment == null){
            getFragmentManager().beginTransaction().add(R.id.framelayout_profileactivity_fragment, otherProfileFragment).commit();
        }
        else{
            getFragmentManager().beginTransaction().replace(R.id.framelayout_profileactivity_fragment, otherProfileFragment, OtherProfileFragment.TAG_FRAGMENT).commit();
        }
    }

    @Override
    public void showFullScreenProfileImage(UserModel user) {
        ProfileImageFragment profileImageFragment = ProfileImageFragment.newInstance(user);
        Fragment fragment = getFragmentManager().findFragmentById(R.id.framelayout_profileactivity_fragment);

        if(fragment == null){
            getFragmentManager().beginTransaction().add(R.id.framelayout_profileactivity_fragment, profileImageFragment).commit();
        }
        else{
            getFragmentManager().beginTransaction().replace(R.id.framelayout_profileactivity_fragment, profileImageFragment, ProfileImageFragment.TAG_FRAGMENT).commit();
        }
    }

}
