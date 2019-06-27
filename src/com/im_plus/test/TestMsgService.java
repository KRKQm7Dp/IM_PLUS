package com.im_plus.test;

import com.im_plus.pojo.Message;
import com.im_plus.service.MsgService;
import com.im_plus.utils.Utils;

import java.util.LinkedList;
import java.util.List;

public class TestMsgService {
    public static void main(String[] args){

//        Message msg = new Message();
//        msg.setMFromUserId("10000");
//        msg.setMToUserId("10001");
//        msg.setMPostMessages("你好啊，现在是2019年5月3日17:23:03，今天天气特别好");
//        msg.setMStatus((byte)1);
//        msg.setMTime(Utils.strToSqlDate("2019-5-3 17:23:03" ,"yyyy-MM-dd HH:mm:ss"));
//        msg.setMMessagesTypeId(Message.MESSAGE_TYPE_ORDINARY);


        MsgService msgService = new MsgService();
//        if(msgService.insertMsgToDB(msg)){
//            System.out.println("success");
//        }

        LinkedList<Message> list = msgService.queryMsgByPage("10000" , "10001", Utils.strToSqlDate("2019-04-05 20:34:00" ,"yyyy-MM-dd HH:mm:ss"), 20);
        while(!list.isEmpty()){
            System.out.println(list.pop());
        }

    }
}
