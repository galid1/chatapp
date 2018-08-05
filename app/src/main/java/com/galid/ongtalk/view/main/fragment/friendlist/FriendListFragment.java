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
import com.galid.ongtalk.constant.FirebaseDatabaseConstant;
import com.galid.ongtalk.model.UserModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FriendListFragment extends Fragment{
    public static final String TAG_FRAGMENT = "FRIENDLIST";

    public static FriendListFragment newInstance(){
        FriendListFragment friendListFragment = new FriendListFragment();
        return friendListFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View friendListFragment = inflater.inflate(R.layout.fragment_main_friendlist, container , false);

        RecyclerView recyclerViewFriendList = friendListFragment.findViewById(R.id.recyclerview_mainfragment_friendlist);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(container.getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewFriendList.setLayoutManager(linearLayoutManager);
        FriendListFragmentAdapter friendListFragmentAdapter = new FriendListFragmentAdapter();
        recyclerViewFriendList.setAdapter(friendListFragmentAdapter);

        return friendListFragment;
    }

    public class FriendListFragmentAdapter extends RecyclerView.Adapter<FriendListFragmentAdapter.FriendListItemViewHolder> {

        private List<UserModel> friendList;

        public FriendListFragmentAdapter(){
            friendList = new ArrayList<>();
            FirebaseDatabase.getInstance().getReference().child(FirebaseDatabaseConstant.FIREBASE_DATABASE_USERLIST).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    friendList.clear();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        friendList.add(snapshot.getValue(UserModel.class));
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
            View friendListItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friendlistfragment, parent , false);

            return new FriendListItemViewHolder(friendListItem);
        }

        @Override
        public void onBindViewHolder(FriendListItemViewHolder holder, int position) {
            holder.bind(friendList.get(position));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }

        @Override
        public int getItemCount() {
            return friendList.size();
        }

        public class FriendListItemViewHolder extends RecyclerView.ViewHolder{
            @BindView(R.id.imageview_item_profileimage)
            ImageView imageViewProfileImage;
            @BindView(R.id.textview_item_name)
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
