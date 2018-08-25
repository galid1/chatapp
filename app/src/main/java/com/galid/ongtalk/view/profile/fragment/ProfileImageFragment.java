package com.galid.ongtalk.view.profile.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.galid.ongtalk.R;
import com.galid.ongtalk.model.UserModel;

public class ProfileImageFragment extends Fragment {

    public static final String TAG_FRAGMENT = "PROFILEIMAGE";
    public static final String ARGS_USERMODEL_USER = "USER";

    public static ProfileImageFragment newInstance(UserModel user){
        ProfileImageFragment profileImageFragment = new ProfileImageFragment();

        Bundle args = new Bundle();
        args.putSerializable(ARGS_USERMODEL_USER, user);
        profileImageFragment.setArguments(args);

        return profileImageFragment;
    }

    public ImageView imageViewProfileImage;

    private UserModel me;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View profileImageFramgnet = inflater.inflate(R.layout.fragment_profile_profileimage, container, false);

        Bundle args = getArguments();

        if(args != null)
            me = (UserModel) args.getSerializable(ARGS_USERMODEL_USER);

        imageViewProfileImage = profileImageFramgnet.findViewById(R.id.imageview_profileimagefragment_profileimage);
        Glide.with(imageViewProfileImage)
                .load(me.profileImageUrl)
                .into(imageViewProfileImage);

        return profileImageFramgnet;
    }

}
