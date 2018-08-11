package com.galid.ongtalk.view.addfriend;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.galid.ongtalk.R;
import com.galid.ongtalk.model.UserModel;
import com.galid.ongtalk.util.constant.FirebaseConstant;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddFriendActivity extends AppCompatActivity {

    private String myUid;
    private boolean isDuplicated;
    private List<UserModel> allUsers;
    private AddFriendActivityAdapter adapter;

    private EditText editTextSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfriend);
        editTextSearch = findViewById(R.id.edittext_addfriendactivity_search);

        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        isDuplicated = false;

        RecyclerView recyclerViewAddFriendActivity = findViewById(R.id.recyclerview_addfriendactivity_allusers);
        recyclerViewAddFriendActivity.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AddFriendActivityAdapter();
        allUsers = adapter.allUsers; // 네트워크 문제로 추가한 친구가 제거되지 않은 경우 중복확인후 제거를 위해
        recyclerViewAddFriendActivity.setAdapter(adapter);

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

    }

    public void addFriend(final UserModel friend) {
        //중복이 안된경우만 추가
        FirebaseDatabase.getInstance().getReference().child(FirebaseConstant.FIREBASE_DATABASE_USERLIST).child(myUid).child(FirebaseConstant.FIREBASE_DATABASE_FRIENDLIST)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        UserModel userModel;
                        for (DataSnapshot item : dataSnapshot.getChildren()) {
                            userModel = item.getValue(UserModel.class);
                            if (userModel.uid.equals(friend.uid)) {
                                isDuplicated = true;
                            }
                        }
                        if (!isDuplicated) {
                            FirebaseDatabase.getInstance().getReference().child(FirebaseConstant.FIREBASE_DATABASE_USERLIST).child(myUid).child(FirebaseConstant.FIREBASE_DATABASE_FRIENDLIST)
                                    .push().setValue(friend);

                            addMeToFriend(friend.uid);
                        } else {
                            Toast.makeText(AddFriendActivity.this, "이미 추가된 친구 입니다.", Toast.LENGTH_LONG).show();
                        }
                        isDuplicated = false;
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    // 내가 친구를 추가하면 상대도 내가 추가되게함
    public void addMeToFriend(final String friendUid) {
        FirebaseDatabase.getInstance().getReference().child(FirebaseConstant.FIREBASE_DATABASE_USERLIST).child(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserModel me = dataSnapshot.getValue(UserModel.class);

                if(me != null) {
                    FirebaseDatabase.getInstance().getReference().child(FirebaseConstant.FIREBASE_DATABASE_USERLIST).child(friendUid).child(FirebaseConstant.FIREBASE_DATABASE_FRIENDLIST)
                            .push().setValue(me);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    class AddFriendActivityAdapter extends RecyclerView.Adapter<AddFriendItemViewHolder> {
        private List<UserModel> allUsers;
        private List<UserModel> searchList; // 검색을 위한 임시리스트
        private List<String> myFriendList; // 내 친구는 친구추천 목록에서 제외하기위한 리스트(uid만 저장)

        public AddFriendActivityAdapter() {
            allUsers = new ArrayList<>();
            searchList = new ArrayList<>();
            myFriendList = new ArrayList<>();

            FirebaseDatabase.getInstance().getReference().child(FirebaseConstant.FIREBASE_DATABASE_USERLIST).child(myUid).child(FirebaseConstant.FIREBASE_DATABASE_FRIENDLIST).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    UserModel friend;
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        friend = data.getValue(UserModel.class);
                        myFriendList.add(friend.uid);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

            FirebaseDatabase.getInstance().getReference().child(FirebaseConstant.FIREBASE_DATABASE_USERLIST).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    allUsers.clear();
                    for (DataSnapshot item : dataSnapshot.getChildren()) {
                        UserModel user = item.getValue(UserModel.class);

                        // 내프로필은 건너띄기
                        if (user.uid.equals(myUid))
                            continue;
                        // 내친구는 건너띄기
                        if (myFriendList.contains(user.uid))
                            continue;

                        allUsers.add(user);
                    }
                    // 친구검색을 위한 리스트에 전체 친구목록을 복사해 놓는다
                    searchList.addAll(allUsers);

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
            allUsers.clear();

            // 문자 입력이 없을때는 모든 데이터를 보여준다.
            if (userName.length() == 0) {
                allUsers.addAll(searchList);
            }
            // 문자 입력을 할때..
            else {
                // 리스트의 모든 데이터를 검색한다.
                for (int i = 0; i < searchList.size(); i++) {
                    // arraylist의 모든 데이터에 입력받은 단어(charText)가 포함되어 있으면 true를 반환한다.
                    if (searchList.get(i).userName.toLowerCase().contains(userName)) {
                        // 검색된 데이터를 리스트에 추가한다.
                        allUsers.add(searchList.get(i));
                    }
                }
            }
            // 리스트 데이터가 변경되었으므로 아답터를 갱신하여 검색된 데이터를 화면에 보여준다.
            adapter.notifyDataSetChanged();
        }

        @Override
        public AddFriendItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_addfriendactivity_item_user, parent, false);
            return new AddFriendItemViewHolder(item);
        }

        @Override
        public void onBindViewHolder(AddFriendItemViewHolder holder, final int position) {
            holder.bind(allUsers.get(position));

            // 각 아이템에 팝업메뉴 리스너를 단다
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popupMenu = new PopupMenu(getApplicationContext(), view);
                    getMenuInflater().inflate(R.menu.menu_addfriendactivity_addmenu, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()) {
                                case R.id.menuitem_addfriendactivity_addfriend:
                                    // 친구 추가후에 목록에서 없앤다
                                    addFriend(allUsers.get(position));
                                    allUsers.remove(position);
                                    notifyDataSetChanged();
                                    break;
                            }
                            return false;
                        }
                    });
                    popupMenu.show();
                }
            });

        }

        @Override
        public int getItemCount() {
            return allUsers.size();
        }
    }

    public class AddFriendItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.imageview_addfriendactivity_item_profileimage)
        ImageView imageViewProfileImage;
        @BindView(R.id.textview_addfriendactivity_item_name)
        TextView textViewName;

        public AddFriendItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(UserModel userModel) {
            textViewName.setText(userModel.userName);

            Glide.with(imageViewProfileImage)
                    .load(userModel.profileImageUrl)
                    .into(imageViewProfileImage);
        }
    }
}
