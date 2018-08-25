package com.galid.ongtalk.view.main.fragment.friendlist;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.galid.ongtalk.R;
import com.galid.ongtalk.util.constant.FirebaseConstant;
import com.galid.ongtalk.model.UserModel;
import com.galid.ongtalk.view.addfriend.AddFriendActivity;
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
import de.hdodenhof.circleimageview.CircleImageView;

//TODO  최근 추가된 친구, 친구 정렬
public class FriendListFragment extends Fragment{
    public static final String TAG_FRAGMENT = "FRIENDLIST";

    private LinearLayout linearLayoutMyProfileBar;
    private TextView textViewMyName;
    private TextView textViewFriendListNum;
    private CircleImageView imageViewMyProfileImage;
    private ImageView buttonAddFriend;
    private EditText editTextSearch;

    private FriendListFragmentAdapter adapter;
    private UserModel me;

    //TODO 내프로필 누르면 다른사람 누르는것과 같게 구현

    public static FriendListFragment newInstance(){
        FriendListFragment friendListFragment = new FriendListFragment();
        return friendListFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View friendListFragment = inflater.inflate(R.layout.fragment_main_friendlist, container , false);

        linearLayoutMyProfileBar = friendListFragment.findViewById(R.id.linearlayout_friendlistfragment_myprofilebar);
        textViewMyName = friendListFragment.findViewById(R.id.textview_friendlistfragment_myname);
        textViewFriendListNum = friendListFragment.findViewById(R.id.textview_friendlistfragment_friendlistnum);
        imageViewMyProfileImage = friendListFragment.findViewById(R.id.imageview_friendlistfragment_myprofileimage);
        editTextSearch = friendListFragment.findViewById(R.id.edittext_friendlistfragment_search);

        buttonAddFriend = friendListFragment.findViewById(R.id.imageview_friendlistfragment_addfriendbutton);
        buttonAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(friendListFragment.getContext(), AddFriendActivity.class);
                startActivity(intent);
            }
        });

        // 내 프로필 처리 코드
        String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference().child(FirebaseConstant.FIREBASE_DATABASE_USERLIST).child(myUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                me = dataSnapshot.getValue(UserModel.class);
                textViewMyName.setText(me.userName);

                Glide.with(imageViewMyProfileImage)
                        .load(me.profileImageUrl)
                        .into(imageViewMyProfileImage);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        // 내프로필 창 열기 리스너 달기
        linearLayoutMyProfileBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProfile(getActivity(), me);
            }
        });

        // recyclerview 생성 코드
        RecyclerView recyclerViewFriendList = friendListFragment.findViewById(R.id.recyclerview_friendlistfragment_friendlist);
        recyclerViewFriendList.setLayoutManager(new LinearLayoutManager(container.getContext()));
        adapter = new FriendListFragmentAdapter();
        recyclerViewFriendList.setAdapter(adapter);

        // 검색을 위한 리스너
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                // input창에 문자를 입력할때마다 호출된다.
                // search 메소드를 호출한다.
                String searchName = editTextSearch.getText().toString();
                adapter.search(searchName);
            }
        });

        return friendListFragment;
    }

    private void showProfile(Context context, UserModel opponent){
        Intent intent = ProfileActivity.newIntent(context, opponent);
        startActivity(intent);
    }

    public class FriendListFragmentAdapter extends RecyclerView.Adapter<FriendListFragmentAdapter.FriendListItemViewHolder> {

        private List<UserModel> friendList;
        private List<UserModel> searchList;

        public FriendListFragmentAdapter(){
            friendList = new ArrayList<>();
            searchList = new ArrayList<>();

            final String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            FirebaseDatabase.getInstance().getReference().child(FirebaseConstant.FIREBASE_DATABASE_USERLIST).child(myUid).child(FirebaseConstant.FIREBASE_DATABASE_FRIENDLIST)
                    .addValueEventListener(new ValueEventListener() {
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
                                // 친구숫자 셋팅
                                textViewFriendListNum.setText(Integer.toString(friendList.size()));
                            }
                            // 친구검색을 위한 리스트에 전체 친구목록을 복사해 놓는다
                            searchList.addAll(friendList);

                            notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }

        // 검색을 수행하는 메소드
        public void search(String userName) {
            // 문자 입력시마다 리스트를 지우고 새로 뿌려준다.
            friendList.clear();

            // 문자 입력이 없을때는 모든 데이터를 보여준다.
            if (userName.length() == 0) {
                friendList.addAll(searchList);
            }
            // 문자 입력을 할때..
            else
            {
                // 리스트의 모든 데이터를 검색한다.
                for(int i = 0;i < searchList.size(); i++)
                {
                    // arraylist의 모든 데이터에 입력받은 단어(charText)가 포함되어 있으면 true를 반환한다.
                    if (searchList.get(i).userName.toLowerCase().contains(userName))
                    {
                        // 검색된 데이터를 리스트에 추가한다.
                        friendList.add(searchList.get(i));
                    }
                }
            }
            // 리스트 데이터가 변경되었으므로 아답터를 갱신하여 검색된 데이터를 화면에 보여준다.
            adapter.notifyDataSetChanged();
        }

        @Override
        public FriendListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View friendListItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_friendlistfragment_item_user, parent , false);
            return new FriendListItemViewHolder(friendListItem);
        }

        @Override
        public void onBindViewHolder(FriendListItemViewHolder holder, final int position) {  //TODO FIX IT
            holder.bind(friendList.get(position));

            //프로필 보여주기
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UserModel opponent = friendList.get(position);
                    showProfile(view.getContext(), opponent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return friendList.size();
        }

        public class FriendListItemViewHolder extends RecyclerView.ViewHolder{
            @BindView(R.id.imageview_friendlistfragment_item_profileimage)
            CircleImageView imageViewProfileImage;
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
