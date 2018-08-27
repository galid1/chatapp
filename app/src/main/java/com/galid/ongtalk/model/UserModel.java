package com.galid.ongtalk.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserModel implements Serializable{
    public String uid;
    public String userName;
    public String profileImageUrl;
    public String pushToken;

    //TODO 친구목록을 가지고있는게 좋은지 생각해보기
    //public List<UserModel> friendList = new ArrayList<>();
}
