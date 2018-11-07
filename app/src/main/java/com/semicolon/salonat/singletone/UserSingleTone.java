package com.semicolon.salonat.singletone;

import com.semicolon.salonat.models.UserModel;

public class UserSingleTone {
    private static UserSingleTone instance=null;
    private UserModel userModel = null;

    private UserSingleTone() {
    }

    public static synchronized UserSingleTone getInstance()
    {
        if (instance==null)
        {
            instance = new UserSingleTone();
        }
        return instance;
    }
    public void setUserModel(UserModel userModel)
    {
        this.userModel=userModel;
    }

    public UserModel getUserModel()
    {
        return this.userModel;
    }

    public void clear()
    {
        this.userModel=null;
    }
}
