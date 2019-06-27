package com.im_plus.service;

import com.im_plus.db.DataBase;
import com.im_plus.pojo.Message;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Stack;

public class MsgService {

    DataBase db = new DataBase();

    /**
     * 将消息保存到数据库
     * @param msg
     * @return
     */
    public boolean insertMsgToDB(Message msg){
        if(!db.openConnection()){
            return false;
        }
        String sql = "insert into messages(M_FromUserID, M_ToUserID, M_PostMessages, M_MessagesTypeID, M_Time, M_status) values (?,?,?,?,?,?)";
        Connection conn = db.getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, msg.getMFromUserId());
            ps.setString(2, msg.getMToUserId());
            ps.setString(3, msg.getMPostMessages());
            ps.setInt(4, msg.getMMessagesTypeId());
            ps.setTimestamp(5, msg.getMTime());
            ps.setInt(6, msg.getMStatus());
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
     * 查询指定日期前的 msgNum 条历史记录
     * @param from
     * @param to
     * @param timeStamp
     * @param msgNum
     * @return 返回消息列表 (逆序)
     */
    public LinkedList<Message> queryMsgByPage(String from, String to, Timestamp timeStamp, int msgNum){
        if(!db.openConnection()){
            return null;
        }
        String sql = "select * from messages " +
                "where ((M_FromUserID=? and M_ToUserID=?) " +
                "or (M_FromUserID=? and M_ToUserID=?)) " +
                "and M_Time<? " +
                "order by M_Time desc " +
                "limit 0, ?";
        Connection conn = db.getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, from);
            ps.setString(2, to);
            ps.setString(3, to);
            ps.setString(4, from);
            ps.setTimestamp(5, timeStamp);
            ps.setInt(6, msgNum);
            ResultSet rs = ps.executeQuery();
            LinkedList<Message> list = new LinkedList<Message>();
            while(rs.next()){
                Message msg = new Message();
                msg.setMFromUserId(rs.getString("M_FromUserID"));
                msg.setMToUserId(rs.getString("M_ToUserID"));
                msg.setMPostMessages(rs.getString("M_PostMessages"));
                msg.setMTime(rs.getTimestamp("M_Time"));
                msg.setMMessagesTypeId(rs.getInt("M_MessagesTypeID"));
                msg.setMStatus(rs.getByte("M_status"));
                list.push(msg);
            }
            if (list.size() == 0){
                list = null;
            }
            return list;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            db.closeConnection();
        }
    }

}
