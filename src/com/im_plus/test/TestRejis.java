package com.im_plus.test;

import com.im_plus.pojo.User;
import com.im_plus.service.UserService;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

public class TestRejis {
    public static void main(String[] args){
        UserService userService = new UserService();
        User user = new User();
        user.setULoginId("10004");
        user.setUNickName("aaa");
        user.setUPassWord("123456");
        if(userService.registerUser(user))
            System.out.println("cheng gong");
    }
}
