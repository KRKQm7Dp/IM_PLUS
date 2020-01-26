package com.im_plus.service;

import com.im_plus.db.DataBase;
import com.im_plus.pojo.User;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FriendService {
    private DataBase db = new DataBase();

    public JSONArray queryMyFriends(String loginUserID){
        if(!db.openConnection()){
            return null;
        }
        JSONArray jsonArray = new JSONArray();
        String sql = "select sys_user.*,friends.F_FriendGroupsID from friends join sys_user on F_FirendID=U_LoginID where F_UserID=?";
        Connection conn = db.getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1,loginUserID);
            ResultSet result = ps.executeQuery();
            while(result.next()){
                JSONObject user_json = new JSONObject();
                user_json.put("uLoginId", result.getString("U_LoginID"));
                user_json.put("uHeadPortrait", result.getString("U_HeadPortrait"));
                user_json.put("uNickName", result.getString("U_NickName"));
                user_json.put("fFriendGroupsID", result.getInt("F_FriendGroupsID"));
                jsonArray.put(user_json);
            }
            return jsonArray;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            db.closeConnection();
        }
    }

    public boolean addFriendRelationship(String userID_1, String userID_2){
        if(!db.openConnection()){
            return false;
        }
        UserService userService = new UserService();
        User user_1 = userService.queryUserByLoginID(userID_1);
        User user_2 = userService.queryUserByLoginID(userID_2);
        Connection conn = db.getConnection();
        String sql_1 = "insert into friends(F_FirendID,F_UserID,F_Name,F_FriendTypeID,F_FriendGroupsID) values(?,?,?,?,?)";
        String sql_2 = "insert into friends(F_FirendID,F_UserID,F_Name,F_FriendTypeID,F_FriendGroupsID) values(?,?,?,?,?)";
        try {
            PreparedStatement ps1 = conn.prepareStatement(sql_1);
            ps1.setString(1,userID_1);
            ps1.setString(2,userID_2);
            ps1.setString(3,user_1.getUNickName());
            ps1.setInt(4, 0);
            ps1.setInt(5, 0); // 设置为默认好友分组
            ps1.executeUpdate();

            PreparedStatement ps2 = conn.prepareStatement(sql_2);
            ps2.setString(1,userID_2);
            ps2.setString(2,userID_1);
            ps2.setString(3,user_2.getUNickName());
            ps2.setInt(4, 0);
            ps2.setInt(5, 0); // 设置为默认好友分组
            ps2.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            db.closeConnection();
        }

    }
}
