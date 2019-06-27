package com.im_plus.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.im_plus.log.MyLog;
import com.im_plus.pojo.User;
import com.im_plus.service.FriendService;
import org.json.JSONArray;
import org.json.JSONObject;


/**
 * 用户获取好友列表
 */
@WebServlet("/userlist")
public class UserListServlet extends JsonServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=utf-8");
        response.setCharacterEncoding("UTF-8");

        String requestString = ReadFromStream(request);
        JSONObject loginUserJson = new JSONObject(requestString);

        FriendService friendService = new FriendService();

        JSONArray friends = friendService.queryMyFriends(loginUserJson.getString("loginUserID"));

        MyLog.log(this.getClass(), friends.toString());

        PrintWriter writer = response.getWriter();
        writer.println(friends.toString());
        writer.close(); // ajax 返回的时候必须关闭流
    }
}

