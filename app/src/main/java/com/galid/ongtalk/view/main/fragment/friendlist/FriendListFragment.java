package com.galid.ongtalk.view.main.fragment.friendlist;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.galid.ongtalk.R;
import com.galid.ongtalk.util.constant.FirebaseConstant;
import com.galid.ongtalk.model.UserModel;
import com.galid.ongtalk.view.chat.ChatActivity;
import com.galid.ongtalk.view.profile.ProfileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

//TODO 친구 검색기능
public class FriendListFragment extends Fragment{
    public static final String TAG_FRAGMENT = "FRIENDLIST";
    private Unbinder unBinder;
    public TextView textViewMyName;
    public ImageView imageViewMyProfileImage;
    //TODO 내프로필 누르면 다른사람 누르는것과 같게 구현

    public static FriendListFragment newInstance(){
        FriendListFragment friendListFragment = new FriendListFragment();
        return friendListFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View friendListFragment = inflater.inflate(R.layout.fragment_main_friendlist, container , false);
        textViewMyName = friendListFragment.findViewById(R.id.textview_friendlistfragment_myname);
        imageViewMyProfileImage = friendListFragment.findViewById(R.id.imageview_friendlistfragment_myprofileimage);

        // 내 프로필 처리 코드
        String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference().child(FirebaseConstant.FIREBASE_DATABASE_USERLIST).child(myUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserModel me = dataSnapshot.getValue(UserModel.class);
                textViewMyName.setText(me.userName);
                Glide.with(imageViewMyProfileImage)
                        .load(me.profileImageUrl)
                        .into(imageViewMyProfileImage);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        RecyclerView recyclerViewFriendList = friendListFragment.findViewById(R.id.recyclerview_friendlistfragment_friendlist);
        recyclerViewFriendList.setLayoutManager(new LinearLayoutManager(container.getContext()));
        recyclerViewFriendList.setAdapter(new FriendListFragmentAdapter());

        return friendListFragment;
    }

    public class FriendListFragmentAdapter extends RecyclerView.Adapter<FriendListFragmentAdapter.FriendListItemViewHolder> {

        private List<UserModel> friendList;

        public FriendListFragmentAdapter(){
            friendList = new ArrayList<>();
            final String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            FirebaseDatabase.getInstance().getReference().child(FirebaseConstant.FIREBASE_DATABASE_USERLIST).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    friendList.clear();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        UserModel user = snapshot.getValue(UserModel.class);
                        // 내 아이디와 같다면 패스
                        if(myUid.equals(user.uid))
                            continue;

                        // 아니라면 보여질 데이타셋에 추가
                        friendList.add(user);
                    }
                    notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        @Override
        public FriendListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View friendListItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_friendlistfragment_item_user, parent , false);
            return new FriendListItemViewHolder(friendListItem);
        }

        @Override
        public void onBindViewHolder(FriendListItemViewHolder holder, final int position) {  //TODO FIX IT
            holder.bind(friendList.get(position));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String destinationUid = friendList.get(position).uid;
                    showProfileActivity(view, destinationUid);
                }
            });
        }

        @Override
        public int getItemCount() {
            return friendList.size();
        }

        // Profile Activity 실행
        private void showProfileActivity(View view , String destinationUid) {
            Intent intent = new Intent(view.getContext(), ProfileActivity.class);
            intent.putExtra(ChatActivity.DESTINATION_UID , destinationUid);
            startActivity(intent);
        }

        public class FriendListItemViewHolder extends RecyclerView.ViewHolder{
            @BindView(R.id.imageview_friendlistfragment_item_profileimage)
            ImageView imageViewProfileImage;
            @BindView(R.id.textview_friendlistfragment_item_name)
            TextView textViewName;

            public FriendListItemViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            public void bind(UserModel userModel){
                textViewName.setText(userModel.userName);

                Glide.with(imageViewProfileImage)
                        .load(userModel.profileImageUrl)
                        .into(imageViewProfileImage);
            }
        }

    }
}
