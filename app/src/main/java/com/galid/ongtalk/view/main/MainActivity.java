package com.galid.ongtalk.view.main;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.galid.ongtalk.R;
import com.galid.ongtalk.view.main.fragment.chatroomlist.ChatRoomListFragment;
import com.galid.ongtalk.view.main.fragment.friendlist.FriendListFragment;
import com.galid.ongtalk.view.main.fragment.settings.SettingsFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements  FriendListFragment.Callbacks{

    @BindView(R.id.bottomnavigationview_mainactivity)
    public BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        //첫 화면은 친구목록 프래그먼트
        attachFriendListFragment();
        //하단 네비게이션에 리스너 달기
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
    }

    // 네비게이션 버튼리스너
    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch(item.getItemId()){
                case R.id.item_mainnavigation_friendlist :
                    attachFriendListFragment();
                    return true;

                case R.id.item_mainnavigation_chat :
                    attachChatRoomListFragment();
                    return true;

                case R.id.item_mainnavigation_settings :
                    attachSettingsFragment();
                    return true;

                default:
                    return false;
            }
        }
    };

    public void attachFriendListFragment(){
        Fragment fragment = getFragmentManager().findFragmentById(R.id.framelayout_mainactivity_fragment);
        FriendListFragment friendListFragment = FriendListFragment.newInstance();

        if(fragment == null) {
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.framelayout_mainactivity_fragment, friendListFragment)
                    .commit();
        }
        else{
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.framelayout_mainactivity_fragment, friendListFragment , FriendListFragment.TAG_FRAGMENT)
                    .commit();
        }
    }

    public void attachChatRoomListFragment(){
        Fragment fragment = getFragmentManager().findFragmentById(R.id.framelayout_mainactivity_fragment);
        ChatRoomListFragment chatRoomListFragment = ChatRoomListFragment.newInstance();

        if(fragment == null) {
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.framelayout_mainactivity_fragment, chatRoomListFragment)
                    .commit();
        }
        else{
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.framelayout_mainactivity_fragment, chatRoomListFragment , ChatRoomListFragment.TAG_FRAGMENT)
                    .commit();
        }
    }

    public void attachSettingsFragment(){
        Fragment fragment = getFragmentManager().findFragmentById(R.id.framelayout_mainactivity_fragment);
        SettingsFragment settingsFragment = SettingsFragment.newInstance();

        if(fragment == null) {
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.framelayout_mainactivity_fragment, settingsFragment)
                    .commit();
        }
        else{
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.framelayout_mainactivity_fragment, settingsFragment , SettingsFragment.TAG_FRAGMENT)
                    .commit();
        }
    }

    // FriendListFragment에서 유저이미지를 로드할때 Glide에서 비동기처리로 인해 액티비티가 종료되었을 경우 오류 해결을 위함
    @Override
    public void loadUserProfileImage(String imageUrl, ImageView imageViewProfile) {
        Glide.with(this)
                .load(imageUrl)
                .into(imageViewProfile);
    }
}
