package com.jemmy.viewmodel;

import androidx.lifecycle.ViewModel;

import com.jemmy.vo.User;
import com.jemmy.vo.UserVO;

public class UserViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    UserVO userVO;
    User user;

    public UserVO getUserVO() {
        return userVO;
    }

    public void setUserVO(UserVO userVO) {
        this.userVO = userVO;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
