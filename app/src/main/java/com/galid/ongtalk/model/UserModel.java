package com.galid.ongtalk.model;

import java.util.ArrayList;
import java.util.List;

public class UserModel {
    public String uid;
    public String userName;
    public String profileImageUrl;

    public List<UserModel> friendList = new ArrayList<>();
}
