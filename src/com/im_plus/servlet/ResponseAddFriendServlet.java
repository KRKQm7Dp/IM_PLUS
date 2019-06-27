package com.im_plus.servlet;

import com.im_plus.controller.ChatController;
import com.im_plus.log.MyLog;
import com.im_plus.pojo.Message;
import com.im_plus.service.FriendService;
import com.im_plus.utils.ResponseInformation;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * 用于处理回应好友请求
 * msg = {
 *    from:loginedUserID+'',
 *    to: reqID+'',
 *    responseCode: responseCode,
 *    time: getNowDate(),
 * }
 */
@WebServlet("/responseAddFriendServlet")
public class ResponseAddFriendServlet extends JsonServlet {

    private static final int RESPONSECODE_AGREE = 1;
    private static final int RESPONSECODE_REFUSE = 0;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=utf-8");
        response.setCharacterEncoding("UTF-8");
        PrintWriter writer = response.getWriter();
        String data = ReadFromStream(request);
        JSONObject json_data = new JSONObject(data);
        if (json_data.getInt("responseCode") == RESPONSECODE_AGREE){  // 接受
            MyLog.log(this.getClass(), "agree");
            FriendService friendService = new FriendService();
            boolean flag = friendService.addFriendRelationship(json_data.getString("from"), json_data.getString("to"));
            if(flag){
                Message hintMsg = new Message();
                hintMsg.setMFromUserId("");
                hintMsg.setMToUserId(json_data.getString("to"));
                hintMsg.setMMessagesTypeId(Message.MESSAGE_TYPE_SYSTEMNOTIFY);
                hintMsg.setMTime(new Timestamp(new Date().getTime()));
                hintMsg.setMPostMessages("ID:" + json_data.getString("from") + " 接受了你的好友请求，你们已成为好友啦");
                sendMsgToRequester(hintMsg);

                writer.println(ResponseInformation.getSuccessInformation());
                writer.close();
            }
            else{
                writer.println(ResponseInformation.getErrorInformation("添加好与关系失败"));
                writer.close();
            }
        }
        else if(json_data.getInt("responseCode") == RESPONSECODE_REFUSE){  // 拒绝
            MyLog.log(this.getClass(), "refuse");

            Message hintMsg = new Message();
            hintMsg.setMFromUserId("");
            hintMsg.setMToUserId(json_data.getString("to"));
            hintMsg.setMMessagesTypeId(Message.MESSAGE_TYPE_SYSTEMNOTIFY);
            hintMsg.setMTime(new Timestamp(new Date().getTime()));
            hintMsg.setMPostMessages("ID:" + json_data.getString("from") + " 拒绝了你的好友请求");
            sendMsgToRequester(hintMsg);

            writer.println(ResponseInformation.getSuccessInformation());
            writer.close();
        }
    }

    /**
     * 通过 websocket 通知请求者添加好友请求被接收或拒绝
     */
    private void sendMsgToRequester(Message msg){
        List<ChatController> connectedUsers = ChatController.getConnectedUsers();
        for (ChatController chatController : connectedUsers){
            if (msg.getMToUserId().equals(chatController.getUser().getULoginId())){
                try {
                    chatController.sendMessageText(msg.toJson().toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
