package com.im_plus.service;

import com.im_plus.db.DataBase;
import com.im_plus.pojo.User;

import java.sql.*;

public class UserService {
    private DataBase db = new DataBase();

    /**
     * 登录验证
     * @param loginID,password
     * @return 若存在返回 user, 否则返回 null
     */
    public User CheckUser(String loginID, String password) {
        if (!db.openConnection()) {
            return null;
        }
        String sql = "select * from user where U_LoginID=? and U_PassWord=?";
        Connection conn = db.getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, loginID);
            ps.setString(2, password);
            ResultSet result = ps.executeQuery();
            while (result.next()) {
                User user = new User();
                user.setULoginId(loginID);
                user.setUPassWord(password);
                user.setUHeadPortrait(result.getString("U_HeadPortrait"));
                user.setUSex(result.getByte("U_Sex"));
                user.setUBirthday(result.getString("U_Birthday"));
                user.setUNickName(result.getString("U_NickName"));
                user.setUEmail(result.getString("U_Email"));
                user.setUSignaTure(result.getString("U_SignaTure"));
                return user;
            }
            return null;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            db.closeConnection();
        }
    }

    /**
     * 通过用户登录 id 获取用户信息
     * @param loginID
     * @return 返回 user 对象
     */
    public User queryUserByLoginID(String loginID) {
        if (!db.openConnection()) {
            return null;
        }
        String sql = "select * from user where U_LoginID=?";
        Connection conn = db.getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, loginID);
            ResultSet result = ps.executeQuery();
            while (result.next()) {
                User user = new User();
                user.setULoginId(loginID);
                user.setUNickName(result.getString("U_NickName"));
                user.setUHeadPortrait(result.getString("U_HeadPortrait"));
                user.setUSex(result.getByte("U_Sex"));
                return user;
            }
            return null;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            db.closeConnection();
        }
    }


    /**
     *  注册用户
     * @param user
     * @return  成功返回 true
     */
    public boolean registerUser(User user){
        if(!db.openConnection()){
            return false;
        }
        String sql = "insert into user (U_LoginID,U_NickName,U_PassWord,U_HeadPortrait) values (?,?,?,?)";
        Connection conn = db.getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1,user.getULoginId());
            ps.setString(2,user.getUNickName());
            ps.setString(3,user.getUPassWord());
            ps.setString(4,user.getUHeadPortrait());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            db.closeConnection();
        }
    }

    /**
     * 更新用户资料
     * @param user
     * @return
     */
    public boolean updateUserInfo(User user){
        if(!db.openConnection()){
            return false;
        }
        String sql = "update user set U_NickName=?, U_PassWord=?, U_Sex=?, U_Email=?, U_Birthday=?, U_SignaTure=?, U_HeadPortrait=? where U_LoginID=?";
        Connection conn = db.getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, user.getUNickName());
            ps.setString(2, user.getUPassWord());
            ps.setByte(3, user.getUSex());
            ps.setString(4, user.getUEmail());
            ps.setDate(5, Date.valueOf(user.getUBirthday()));
            ps.setString(6, user.getUSignaTure());
            ps.setString(7, user.getUHeadPortrait());
            ps.setString( 8, user.getULoginId());
            ps.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            db.closeConnection();
        }

    }


}
