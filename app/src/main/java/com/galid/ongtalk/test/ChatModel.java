package com.galid.ongtalk.test;

import java.util.ArrayList;
import java.util.List;

public class ChatModel {
    public List<String> users = new ArrayList<>(); // 채팅방 유저들
    public List<Message> messages = new ArrayList<>(); // 채팅방 내용

    public static class Message {
        public String uid;
        public String message;
        public String time;
    }
}
