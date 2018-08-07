package com.galid.ongtalk.view.main.fragment.settings;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.galid.ongtalk.R;
import com.galid.ongtalk.view.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;


public class SettingsFragment extends Fragment {
    public static final String TAG_FRAGMENT = "SETTINGS";

    public static SettingsFragment newInstance(){
        SettingsFragment settingsFragment = new SettingsFragment();
        return settingsFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View settingsFragment = inflater.inflate(R.layout.fragment_main_settings , container , false);

        Button buttonLogout = settingsFragment.findViewById(R.id.button_mainactivity_settingsfragment_logout);
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

        return settingsFragment;
    }

    public void logout(){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getActivity() , LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

}
