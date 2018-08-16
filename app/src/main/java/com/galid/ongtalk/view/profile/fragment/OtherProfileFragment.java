package com.galid.ongtalk.view.profile.fragment;

import android.app.Fragment;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class OtherProfileFragment extends Fragment {

    public static final String TAG_FRAGMENT = "OTHERPROFILE";
    public static final String ARGS_OPPONENT = "OPPONENT";

    public static OtherProfileFragment newInstance(UserModel opponent){
        OtherProfileFragment otherProfileFragment = new OtherProfileFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARGS_OPPONENT, opponent);
        otherProfileFragment.setArguments(args);
        return otherProfileFragment;
    }

    public CircleImageView imageViewChat;
    public CircleImageView imageViewOpponentProfile;
    public TextView textViewOpponentName;
    public TextView textViewOpponentPhone;

    private UserModel opponent;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View otherProfileFragment = inflater.inflate(R.layout.fragment_profile_otherprofile, container, false);
        imageViewChat = otherProfileFragment.findViewById(R.id.imageview_otherprofilefragment_chat);

        imageViewOpponentProfile = otherProfileFragment.findViewById(R.id.imageview_otherprofilefragment_profileimage);
        textViewOpponentName = otherProfileFragment.findViewById(R.id.textview_otherprofilefragment_name);
        textViewOpponentPhone = otherProfileFragment.findViewById(R.id.textview_otherprofilefragment_phone);

        otherProfileFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChatActivity();
            }
        });

        Bundle args = getArguments();
        if(args != null)
            opponent = (UserModel) args.getSerializable(ARGS_OPPONENT);
        bindView(opponent);

        return otherProfileFragment;
    }

    // 채팅 버튼 클릭시
    public void showChatActivity(){
        Intent intent = ChatActivity.newIntent(getActivity(), opponent);
        startActivity(intent);
        getActivity().finish();
    }

    private void bindView(UserModel opponentUserModel){
        textViewOpponentName.setText(opponentUserModel.userName);
        //TODO PHONE 넘버
        Glide.with(imageViewOpponentProfile)
                .load(opponentUserModel.profileImageUrl)
                .into(imageViewOpponentProfile);
    }

}
