package com.royarijit998.vlearnit;

public class Users {
    private String userId, userProfileImg, userFullName, userStatus;

    public Users(String userId, String userProfileImg, String userFullName, String userStatus) {
        this.userId = userId;
        this.userProfileImg = userProfileImg;
        this.userFullName = userFullName;
        this.userStatus = userStatus;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserProfileImg() {
        return userProfileImg;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public String getUserStatus() {
        return userStatus;
    }
}
