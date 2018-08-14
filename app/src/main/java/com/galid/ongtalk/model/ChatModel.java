package com.galid.ongtalk.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatModel {
    public List<String> users = new ArrayList<>(); // 채팅방 유저들
    public Map<String, ChatMessageModel> chatmessages = new HashMap<>(); // 채팅방 내용
}
