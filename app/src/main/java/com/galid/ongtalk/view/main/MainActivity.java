package com.galid.ongtalk.view.main;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.galid.ongtalk.R;
import com.galid.ongtalk.view.main.fragment.friendlist.FriendListFragment;
import com.galid.ongtalk.view.main.fragment.settings.SettingsFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.bottomnavigationview_mainactivity)
    public BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        attachFriendListFragment();

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

}
