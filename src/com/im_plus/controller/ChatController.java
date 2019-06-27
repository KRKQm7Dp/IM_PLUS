package com.im_plus.controller;

import java.io.*;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import com.im_plus.log.MyLog;
import com.im_plus.pojo.Message;
import com.im_plus.pojo.User;
import com.im_plus.service.MsgService;
import com.im_plus.service.RedisService;
import com.im_plus.service.UserService;
import com.im_plus.servlet.GetHttpSessionConfigurator;
import com.im_plus.utils.Utils;
import org.json.JSONObject;
import sun.misc.BASE64Decoder;

/**
 * 主要用于处理 websocket 通信消息的控制器，每个用户对应一个此类
 */
@ServerEndpoint(value="/chat/{loginUserID}",configurator=GetHttpSessionConfigurator.class)
public class ChatController {

    // 定义一个socket 连接列表，每个用户登录都会创建连接并保存到此列表中
	private static List<ChatController> connectedUsers = new CopyOnWriteArrayList<>();
	private Session session;
    private HttpSession httpSession;
	private User user;
	private UserService userService;
	private MsgService msgService;

	public static List<ChatController> getConnectedUsers() {
		return connectedUsers;
	}

	public User getUser() {
		return user;
	}

    /**
     * 当连接创建的时候
     * @param session
     * @param loginUserID
     */
	@OnOpen
	public void onOpen(Session session, EndpointConfig config, @PathParam(value="loginUserID") String loginUserID) {
		this.session = session;
        this.httpSession= (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
        int maxSize = 400 * 1024; // 设置接收缓存区的大小
        session.setMaxTextMessageBufferSize(maxSize);
        this.userService = new UserService();
        this.msgService = new MsgService();

		this.user = userService.queryUserByLoginID(loginUserID);  // 从数据库中查询此用户
		if(user == null){
//            sendNotifyMessage("errorInfo");  // 给用户提示错误信息
        }

		connectedUsers.add(this); // 将连接“句柄”添加到列表中

        MyLog.log(this.getClass(), "ID:" + user.getULoginId() + "上线！");

//        ByteBuffer byteBuffer = readFileToByteBuffer("E:\\壁纸\\20181003192811.jpg");
//        try {
//            sendBinary(byteBuffer);
//        } catch (IOException e) {
//            e.printStackTrace();
//            MyLog.log(this.getClass(),"发送二进制数据出错" );
//        }

        // 遍历离线消息队列， 将离线消息发送给对应用户
        RedisService redisService = new RedisService();
        List<String> offlineMsg = redisService.getOfflineMsg(loginUserID);
        for (String msg_json : offlineMsg){
            try {
                sendMessageText(msg_json);

                JSONObject messageJson = new JSONObject(msg_json);
                Message msg = new Message();
                msg.readFromJson(messageJson);
                msg.setMStatus(Message.MESSAGE_STATUS_RECEIVED);
                if(msg.getMMessagesTypeId() == Message.MESSAGE_TYPE_ORDINARY){
                    msgService.insertMsgToDB(msg); // 将消息保存到消息记录中
                }

            } catch (IOException e) {
                e.printStackTrace();
                MyLog.log(this.getClass(), "发送离线消息给 " + loginUserID + "失败");
            }
        }
        if (redisService.removeOffLineMsg(loginUserID)){
            MyLog.log(this.getClass(),"成功将所有离线消息都发送给 " + loginUserID +" 并清空了离线消息队列");
        }

	}

    /**
     * 当连接关闭时
     */
	@OnClose
	public void onClose(){
		connectedUsers.remove(this); // 从连接列表中删除此连接
//		sendNotifyMessage();
//		System.out.println("ID:" +user.getULoginId() + "下线！");
        MyLog.log(this.getClass(), "ID:" +user.getULoginId() + "下线！");
			// 下线后，应该发送通知
	}


    /**
     * 接收到的消息，主要处理通知的部分
     * @param message
     * @param session
     */
	@OnMessage
	public void onMessage(String message, Session session){
	    MyLog.log(this.getClass(), message);
		try {
			JSONObject messageJson = new JSONObject(message);
            Message msg = new Message();
            msg.readFromJson(messageJson);  // 创建消息对象，并将接收到的消息内容解析为对应域
            if(msg.getMFromUserId() == null){  // 如果对应的发送者域非空，说明为有效消息
                MyLog.log(this.getClass(), "发送者为空！");
                return;
            }
            MyLog.log(this.getClass(), "消息的 json 串：" + msg.toJson());

            if(msg.getMToUserId() != null){  // 说明有接收者，为有效消息
                if(msg.getMMessagesTypeId() == Message.MESSAGE_TYPE_ORDINARY){  // 如果消息类型为普通消息
                    if(msg.getMToUserId().equals("chatRoom")){
                        sendToAllMsg(msg);
                    }
                    else{
                        sendNotifyMessage(msg);
                    }
                    msg.setMStatus(Message.MESSAGE_STATUS_RECEIVED);  // 已发送出去的的消息默认对方已收到
                    msgService.insertMsgToDB(msg); // 将消息添加到消息记录中
                }
                else if(msg.getMMessagesTypeId() == Message.MESSAGE_TYPE_ADDFRIEND){ // 如果为添加好友
                    User toUser = userService.queryUserByLoginID(msg.getMToUserId());
                    if (toUser != null){
                        User fromUser = userService.queryUserByLoginID(msg.getMFromUserId());
                        msg.setmFromUserHeadPortrait(fromUser.getUHeadPortrait());
                        msg.setmFromUserNickName(fromUser.getUNickName());
                        MyLog.log(this.getClass(), msg.toJson().toString());
                        sendNotifyMessage(msg);
                    }
                    else{
                        Message hintMsg = new Message();
                        hintMsg.setMFromUserId("");
                        hintMsg.setMToUserId(msg.getMFromUserId());
                        hintMsg.setMStatus(Message.MESSAGE_STATUS_UNRECEIVED);
                        hintMsg.setMMessagesTypeId(Message.MESSAGE_TYPE_SYSTEMNOTIFY);
                        hintMsg.setMTime(new Timestamp(new Date().getTime()));
                        hintMsg.setMPostMessages("此用户不存在");
                        sendNotifyMessage(hintMsg);
                    }
                }
                else if(msg.getMMessagesTypeId() == Message.MESSAGE_TYPE_IMAGE){  // 如果消息类型为图片类型
                    MyLog.log(this.getClass(), "接收到图片消息" + msg.getMPostMessages());
                    String filePath = DecodeBase64ToImg(msg.getMPostMessages());
                    if(filePath != null){
                        msg.setMPostMessages(filePath);
                        if(msg.getMToUserId().equals("chatRoom")){  // 如果是聊天室消息
                            sendToAllMsg(msg);
                        }
                        else{
                            sendNotifyMessage(msg);
                        }
                        msg.setMStatus(Message.MESSAGE_STATUS_RECEIVED);  // 已发送出去的的消息默认对方已收到
                        msgService.insertMsgToDB(msg); // 将消息添加到消息记录中
                    }
                }

            }


		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    /**
     * 用于解析接收到的非普通文本内容
     * 接收到的消息内容格式 {data:image/jpeg;base64,*******（base64 编码后的文件内容）}
     * @param imgStr
     * @return 返回文件保存路径
     */
	public String DecodeBase64ToImg(String imgStr){
	    String fileType = "";  // 文件类型 （图片，文本...）
	    String fileFormat = ""; // 文件格式 (jpg, jpeg, png...)
        Pattern pattern = Pattern.compile("(?<=data:).*(?=;)");
        Matcher matcher = pattern.matcher(imgStr);
        if(matcher.find()){
            fileType = matcher.group().split("/")[0];
            fileFormat = matcher.group().split("/")[1];
        }
        BASE64Decoder d = new BASE64Decoder();
        try {
            byte[] bytes = d.decodeBuffer(imgStr.split(",")[1]);  // 文件中数据

            if ("image".equals(fileType)){
                StringBuilder filePath = new StringBuilder();
                String rootPath = httpSession.getServletContext().getRealPath("/");
                filePath.append(rootPath)
                        .append("users\\")
                        .append(this.user.getULoginId())
                        .append("\\msg_img\\")
                        .append(Utils.getNowDate("yyyyMMddHHmmss"))
                        .append(".")
                        .append(fileFormat);
                File file = new File(filePath.toString());
                File fileDir = file.getParentFile();
                if(!fileDir.exists()){
                    fileDir.mkdirs();  // 表示如果当前目录不存在就创建，包括所有必须的父目录
                }
                if(!file.exists()){
                    file.createNewFile();
                }
                FileOutputStream fs = new FileOutputStream(file, true);
                fs.write(bytes);
                fs.flush();
                fs.close();
                MyLog.log(this.getClass(), "接收到图片消息，已保存到：" + filePath);

                Pattern pattern2 = Pattern.compile("(?=users).*");
                Matcher matcher2 = pattern2.matcher(filePath);
                if(matcher2.find()){
                    return matcher2.group();
                }
            }

            return null;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 发送消息给 msg 中 to 用户，如果 to 不在线，则保存到其消息队列中
     * @param msg
     * @throws IOException
     */
    public void sendNotifyMessage(Message msg) throws IOException {
        boolean toUserOnline = false;
        for (ChatController chatController : connectedUsers){
            if (chatController.getUser().getULoginId().trim().equals(msg.getMToUserId())){  // 发送给 to
                toUserOnline = true;
                chatController.sendMessageText(msg.toJson().toString());
            }
        }
        if(!toUserOnline){ // 说明 接收者 不在线
            // 将离线消息写到 redis 中的离线消息队列中
            RedisService redisService = new RedisService();
            redisService.insertOfflineMsg(msg.getMToUserId(), msg.toJson().toString());
            redisService.close();
        }
    }

    public void sendToAllMsg(Message msg) throws IOException {
        for (ChatController chatController : connectedUsers){
            if(!chatController.getUser().getULoginId().equals(this.user.getULoginId())){
                chatController.sendMessageText(msg.toJson().toString());
            }
        }
    }

    public void sendMessageText(String content) throws IOException {
        this.session.getBasicRemote().sendText(content);
    }


    public void sendBinary(ByteBuffer byteBuffer) throws IOException {
	    this.session.getBasicRemote().sendBinary(byteBuffer);
    }


	@OnError
	public void onError(Throwable throwalble) {
		MyLog.log(this.getClass(), throwalble.getMessage());
	}

}