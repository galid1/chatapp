package com.galid.ongtalk.model;

import java.util.HashMap;

public class ChatModel {
    public HashMap<String,Boolean> users = new HashMap<>(); // 채팅방 유저들
    public HashMap<String,Message> messages = new HashMap<>(); // 채팅방 내용

    public static class Message {
        public String uid;
        public String message;
    }
}
