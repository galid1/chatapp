package com.galid.ongtalk.view.main.fragment.chatroomlist;

import android.app.Fragment;
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
import com.galid.ongtalk.model.ChatModel;
import com.galid.ongtalk.model.UserModel;
import com.galid.ongtalk.util.constant.FirebaseConstant;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

//TODO 채팅방 이름 변경 가능하게 DEFAULT는 상대방 이름(닉네임)
public class ChatRoomListFragment extends Fragment{
    public static final String TAG_FRAGMENT = "CHATROOMLIST";

    public static ChatRoomListFragment newInstance(){
        ChatRoomListFragment chatRoomListFragment = new ChatRoomListFragment();
        return chatRoomListFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View chatRoomListFragment = inflater.inflate(R.layout.fragment_main_chatroomlist, container , false);

        RecyclerView recyclerViewChatRoomList = chatRoomListFragment.findViewById(R.id.recyclerview_chatroomlistfragment_chatroomlist);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(chatRoomListFragment.getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewChatRoomList.setLayoutManager(linearLayoutManager);
        ChatRoomListAdapter chatRoomListAdapter = new ChatRoomListAdapter();
        recyclerViewChatRoomList.setAdapter(chatRoomListAdapter);
        return chatRoomListFragment;
    }

    class ChatRoomListAdapter extends RecyclerView.Adapter<ChatRoomListViewHolder>{
        // 리사이클러뷰 데이터셋
        private List<ChatModel> chatRoomList = new ArrayList<>();
        private String myUid;

        public ChatRoomListAdapter() {
            myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            getChatRoomListFromFirebaseDatabase();
        }

        public void getChatRoomListFromFirebaseDatabase(){
            FirebaseDatabase.getInstance().getReference().child(FirebaseConstant.FIREBASE_DATABASE_CHATROOM).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    chatRoomList.clear();
                    for(DataSnapshot item : dataSnapshot.getChildren()){
                        ChatModel chatModel = item.getValue(ChatModel.class);
                        chatRoomList.add(chatModel);
                    }
                    notifyDataSetChanged();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }

        @Override
        public ChatRoomListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View chatRoomListItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_chatrommlist_item_chatroom, parent , false);
            return new ChatRoomListViewHolder(chatRoomListItem);
        }

        @Override
        public void onBindViewHolder(final ChatRoomListViewHolder holder, final int position) {
            String opponentUid = "";
            // TODO (CODE:2)모든 사용자에 대해 아래와 같은 처리를 하도록 바꾸기
            for(int i = 0; i < chatRoomList.get(position).users.size(); i++){
                String userUid = chatRoomList.get(position).users.get(i);
                if(!userUid.equals(myUid)) {
                    opponentUid = userUid;
                    FirebaseDatabase.getInstance().getReference().child(FirebaseConstant.FIREBASE_DATABASE_USERLIST).child(opponentUid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            UserModel userModel = dataSnapshot.getValue(UserModel.class);
                            holder.bindUserInfo(userModel);
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
            }
        }

        @Override
        public int getItemCount() {
            return chatRoomList.size();
        }

    }

    private class ChatRoomListViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewProfile;
        TextView textViewChatRoomName;
        TextView textViewLastMessage;
        TextView textViewLastMessageTime;
        TextView textViewMessageNum;
        // TODO (CODE:2)모든 사용자에 대해 아래와 같은 처리를 하도록 바꾸기
        //private List<UserModel> users;

        public ChatRoomListViewHolder(View itemView) {
            super(itemView);
            // TODO (CODE:2)모든 사용자에 대해 아래와 같은 처리를 하도록 바꾸기
        //  users = new ArrayList<>();
            imageViewProfile = itemView.findViewById(R.id.imageview_chatroomlistfragment_item_profile);
            textViewChatRoomName = itemView.findViewById(R.id.textview_chatroomlistfragment_item_chatroomname);
            textViewLastMessage = itemView.findViewById(R.id.textview_chatroomlistfragment_item_lastmessage);
            textViewLastMessageTime = itemView.findViewById(R.id.textview_chatroomlistfragment_item_lastmessagetime);
            textViewMessageNum = itemView.findViewById(R.id.textview_chatroomlistfragment_item_messagenum);
        }
        // TODO (CODE:2)모든 사용자에 대해 아래와 같은 처리를 하도록 바꾸기
        /*public void addUsers(UserModel userInfo){
            users.add(userInfo);
        }*/

        public void bindUserInfo(UserModel userInfo){
            // TODO (CODE:2)모든 사용자에 대해 아래와 같은 처리를 하도록 바꾸기
            /*String chatRoomName = "";
            for(int i = 0; i < users.size(); i ++){
                if(i == users.size()) {
                    chatRoomName += userInfo;
                }
                else
                    chatRoomName += userInfo + ",";
            }*/
            textViewChatRoomName.setText(userInfo.userName);
            Glide.with(imageViewProfile)
                    .load(userInfo.profileImageUrl)
                    .into(imageViewProfile);
        }

        public void bindMessage(ChatModel.Message message){
            textViewLastMessage.setText(message.message);
            textViewLastMessageTime.setText(message.time);
        }

    }
}
