package com.galid.ongtalk.view.chat;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.galid.ongtalk.R;
import com.galid.ongtalk.model.ChatModel;
import com.galid.ongtalk.model.ChatMessageModel;
import com.galid.ongtalk.model.UserModel;
import com.galid.ongtalk.util.constant.FirebaseConstant;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

//TODO 대화상대 추가 만들기 (사이드 슬립 네비게이션 만들기) , 대화 메시지 검색 만들기
public class ChatActivity extends AppCompatActivity {
    public static final String OPPONENT = "OPPONENT";

    private String myUid;
    private String chatRoomUid;

    private UserModel opponent;

    // TODO (CODE:1)코드수정 요구 결합도가 높음
    private RecyclerView recyclerViewChatLog;
    private ChatAdapter chatAdapter;

    @BindView(R.id.edittext_chatactivity_message)
    public EditText editTextMessage;

    public static Intent newIntent(Context context, UserModel opponent){
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(OPPONENT, opponent);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid(); //채팅을 요구하는 id (나)

        if(getIntent() != null) {
            opponent = (UserModel) getIntent().getSerializableExtra(OPPONENT); // 채팅을 당하는 id (상대)
        }

        recyclerViewChatLog = findViewById(R.id.recyclerview_chatctivity_chatlog);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewChatLog.setLayoutManager(linearLayoutManager);
        chatAdapter = new ChatAdapter();
        recyclerViewChatLog.setAdapter(chatAdapter);

        // 액티비티 생성시 이미 생성된 채팅방인지 확인
        checkDuplicatedChatRoomAndGetChatRoomKey();
    }

    //메시지 보내기 메소드
    @OnClick(R.id.button_chatactivity_send)
    public void sendMessage(){
        Date date = new Date();                   // 보낼 때의 시간
        SimpleDateFormat currentTime = new SimpleDateFormat("a hh:mm", Locale.KOREA);

        String chatMessage = editTextMessage.getText().toString();      //보낼 메시지
        final ChatMessageModel chatMessageModel = new ChatMessageModel();   // 서버에 저장될 메시지 형태
        chatMessageModel.uid = myUid;
        chatMessageModel.message = chatMessage;
        chatMessageModel.time = currentTime.format(date);

        checkDuplicatedChatRoomAndGetChatRoomKey();

        if(chatRoomUid == null){ //중복 안된 경우
            ChatModel chatModel = new ChatModel();

            chatModel.users.add(myUid);          // TODO 처음에만 , 대화방이 생성된 이후에는 요청자는 저장 안되게하기
            chatModel.users.add(opponent.uid);

            FirebaseDatabase.getInstance().getReference().child(FirebaseConstant.FIREBASE_DATABASE_CHATROOM).push().setValue(chatModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(ChatActivity.this, "최초로 보내는 메시지는 상대에게 전달되지 않습니다.", Toast.LENGTH_LONG).show();
                    //TODO (CODE:1)코드수정 요구 결합도 높음
                    makeChatEmpty();
                }
            });
        }
        else {
            // 중복된 경우 (방은 생성하지 않고 메시지만 보낸다)
            FirebaseDatabase.getInstance().getReference().child(FirebaseConstant.FIREBASE_DATABASE_CHATROOM).child(chatRoomUid)
                    .child(FirebaseConstant.FIREBASE_DATABASE_CHATMESSAGE).push().setValue(chatMessageModel);
            makeChatEmpty();
        }
    }

    // 채팅방 중복체크 (중복되었으면 그채팅방의 UID를 firebaseDatabase로 부터 얻어온다)
    private void checkDuplicatedChatRoomAndGetChatRoomKey() {
        FirebaseDatabase.getInstance().getReference().child(FirebaseConstant.FIREBASE_DATABASE_CHATROOM).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot item : dataSnapshot.getChildren()){
                    ChatModel chatModel = item.getValue(ChatModel.class);

                    //TODO 여러사람들과의 채팅방 중복도 체크할 필요성이 있음.. 인가 ?
                    if(chatModel.users.contains(opponent.uid) && chatModel.users.contains(myUid)){
                        chatRoomUid = item.getKey();
                        //TODO (CODE:1)코드수정 요구 결합도 높음
                        chatAdapter.getMessageListFromFirebaseDatabase();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // 채팅 메시지창을 empty로만든다
    private void makeChatEmpty(){
        editTextMessage.setText("");
    }

    // Recyclerview Adapter
    class ChatAdapter extends RecyclerView.Adapter{
        public static final int VIEWTYPE_SENDMESSAGE = 0;
        public static final int VIEWTYPE_RECEIVEMESSAGE = 1;

        private List<ChatMessageModel> messages;

        public ChatAdapter() {
            messages = new ArrayList<>();
        }

        //TODO (CODE:1)이 코드를 어디선가 꼭 호출해야한다 그 호출하는 클래스와의 결합도를 낮추기위해 이코드를 나중에 수정하자
        public void getMessageListFromFirebaseDatabase(){
            if(chatRoomUid != null) {
                FirebaseDatabase.getInstance().getReference().child(FirebaseConstant.FIREBASE_DATABASE_CHATROOM).child(chatRoomUid)
                        .child(FirebaseConstant.FIREBASE_DATABASE_CHATMESSAGE).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        messages.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            messages.add(snapshot.getValue(ChatMessageModel.class));
                        }
                        notifyDataSetChanged();

                        //TODO 사용자 편의를 위해 내가 보낸 메시지만 아래로 스크롤 되게 바꾸기
                        if(!messages.isEmpty())
                            recyclerViewChatLog.scrollToPosition(messages.size() - 1);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }

        @Override
        public int getItemViewType(int position) {
            if(messages.get(position).uid.equals(myUid))
                return VIEWTYPE_SENDMESSAGE;
            else
                return VIEWTYPE_RECEIVEMESSAGE;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());

            // 내가 보내는 메시지의 경우
            if(viewType == VIEWTYPE_SENDMESSAGE){
                View sendMessageView = inflater.inflate(R.layout.recyclerview_chatactivity_item_sendmessages, parent , false);
                return new SendMessageViewHolder(sendMessageView);
            }
            // 상대가 보내는 메시지의 경우 VIEWTYPE_RECEIVEMESSAGE
            else{
                View receiveMessageView = inflater.inflate(R.layout.recyclerview_chatactivity_item_receivemessages, parent, false);
                return new ReceiveMessageViewHolder(receiveMessageView);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if(holder instanceof SendMessageViewHolder){
                ((SendMessageViewHolder) holder).bindSendMessage(messages.get(position));
            }
            else if(holder instanceof ReceiveMessageViewHolder){
                ((ReceiveMessageViewHolder) holder).bindReceiveMessage(messages.get(position), opponent);
            }
        }

        @Override
        public int getItemCount() {
            return messages.size();
        }
    }

    // 내가 보내는 메시지 뷰홀더
    private class SendMessageViewHolder extends RecyclerView.ViewHolder {
        TextView textViewSendMessages;
        TextView textViewSendTime;

        public SendMessageViewHolder(View itemView) {
            super(itemView);
            textViewSendMessages = itemView.findViewById(R.id.textview_chatactivity_item_sendmessages);
            textViewSendTime = itemView.findViewById(R.id.textview_chatactivity_item_sendtime);
        }

        public void bindSendMessage(ChatMessageModel message){
            textViewSendMessages.setText(message.message);
            textViewSendTime.setText(message.time);
        }

    }

    // 상대가 보내는 메시지 뷰홀더
    private class ReceiveMessageViewHolder extends RecyclerView.ViewHolder{
        CircleImageView imageViewReceiveProfile;
        TextView textViewReceiveName;
        TextView textViewReceiveMessages;
        TextView textViewReceiveTime;

        public ReceiveMessageViewHolder(View itemView) {
            super(itemView);
            imageViewReceiveProfile = itemView.findViewById(R.id.imageview_chatactivity_item_receiveprofileimage);
            textViewReceiveName = itemView.findViewById(R.id.textview_chatactivity_item_receivename);
            textViewReceiveMessages = itemView.findViewById(R.id.textview_chatactivity_item_receivemessages);
            textViewReceiveTime = itemView.findViewById(R.id.textview_chatactivity_item_receivetime);
        }

        public void bindReceiveMessage(ChatMessageModel message , UserModel opponent){
            textViewReceiveMessages.setText(message.message);
            textViewReceiveName.setText(opponent.userName);
            textViewReceiveTime.setText(message.time);
            Glide.with(imageViewReceiveProfile)
                    .load(opponent.profileImageUrl)
                    .into(imageViewReceiveProfile);
        }

    }

}