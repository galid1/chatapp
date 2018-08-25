package com.galid.ongtalk.view.profile.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.galid.ongtalk.R;
import com.galid.ongtalk.model.UserModel;
import com.galid.ongtalk.view.chat.ChatActivity;
import com.galid.ongtalk.view.profile.ShowImage;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyProfileFragment extends Fragment {

    public static final String TAG_FRAGMENT = "MYPROFILE";
    public static final String ARGS_USERMODEL_ME = "ME";

    public static MyProfileFragment newInstance(UserModel me){
        MyProfileFragment myProfileFragment = new MyProfileFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARGS_USERMODEL_ME, me);
        myProfileFragment.setArguments(args);
        return myProfileFragment;
    }

    public CircleImageView imageViewChat;
    public CircleImageView imageViewEditProfile;
    public CircleImageView imageViewMyProfile;
    public TextView textViewMyName;
    public TextView textViewMyPhone;

    private ShowImage mCallbacks;
    private UserModel me;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(context instanceof ShowImage)
            mCallbacks = (ShowImage)context;
        else
            throw new IllegalArgumentException("Must Implements ShowImage");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View myProfileFragment = inflater.inflate(R.layout.fragment_profile_myprofile, container, false);

        imageViewChat = myProfileFragment.findViewById(R.id.imageview_myprofilefragment_chat);
        imageViewEditProfile = myProfileFragment.findViewById(R.id.imageview_myprofilefragment_edit);
        imageViewMyProfile = myProfileFragment.findViewById(R.id.imageview_myprofilefragment_profileimage);
        textViewMyName = myProfileFragment.findViewById(R.id.textview_myprofilefragment_name);
        textViewMyPhone = myProfileFragment.findViewById(R.id.textview_myprofilefragment_phone);

        imageViewChat.setOnClickListener((view) -> {
            showChatActivity();
        });
        imageViewEditProfile.setOnClickListener((view) -> {
            showEditProfileFragment();
        });
        imageViewMyProfile.setOnClickListener((view) -> {
            mCallbacks.showFullScreenProfileImage(me);// 이미지 사진 클릭시 전체화면으로
        });

        Bundle args = getArguments();
        if(args != null) {
            me = (UserModel) args.getSerializable(ARGS_USERMODEL_ME);
            bindView(me);
        }

        return myProfileFragment;
    }

    // 채팅 버튼 클릭시
    public void showChatActivity(){
        Intent intent = ChatActivity.newIntent(getActivity(), me);
        startActivity(intent);
        getActivity().finish();
    }

    // 프로필 수정 버튼 클릭시
    public void showEditProfileFragment() {

    }

    private void bindView(UserModel myUserModel){
        textViewMyName.setText(myUserModel.userName);
        //TODO PHONE 넘버
        Glide.with(imageViewMyProfile)
                .load(myUserModel.profileImageUrl)
                .into(imageViewMyProfile);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }
}
