package com.im_plus.pojo;


import com.im_plus.utils.Utils;
import org.json.JSONObject;

import java.sql.Timestamp;


public class Message implements IJsonSeriserialize{

    // 定义消息类型常量
  public static final int MESSAGE_TYPE_ORDINARY = 0;
  public static final int MESSAGE_TYPE_IMAGE = 1;
  public static final int MESSAGE_TYPE_SYSTEMNOTIFY = 2;
  public static final int MESSAGE_TYPE_ADDFRIEND = 3;

  // 定义消息状态常量
    public static final byte MESSAGE_STATUS_RECEIVED = 1;
    public static final byte MESSAGE_STATUS_UNRECEIVED = 0;

    // 定义 Json key 常量
    public static final String FROM = "from";
    public static final String TO = "to";
    public static final String MESSSAGE_CONTENT = "messageContent";
    public static final String MESSAGE_TYPE = "messageType";
    public static final String SEND_TIME = "time";
    public static final String MESSAGE_STATUS = "status";
    public static final String FROM_USER_HEADPORTRAIT = "fromUserHeadPortrait";
    public static final String FROM_USER_NICKNAME = "fromUserNickName";

  private int mId;
  private String mPostMessages;
  private byte mStatus;
  private Timestamp mTime;
  private int mMessagesTypeId;
  private String mFromUserId;
  private String mToUserId;
  private String mFromUserHeadPortrait;
  private String mFromUserNickName;


    public Message(){
    }

  public Message(String from, String to, String messageContent,String time, int typeID){
      this.mFromUserId = from;
      this.mToUserId = to;
      this.mPostMessages = messageContent;
      this.mStatus = MESSAGE_STATUS_UNRECEIVED;
      this.mMessagesTypeId = typeID;
      this.mTime = Utils.strToSqlDate(time,"yyyy-MM-dd HH:mm:ss");
  }


  @Override
  public String toString() {
    return "Message{" +
            "mId=" + mId +
            ", mPostMessages='" + mPostMessages + '\'' +
            ", mStatus='" + mStatus + '\'' +
            ", mTime=" + mTime +
            ", mMessagesTypeId=" + mMessagesTypeId +
            ", mFromUserId=" + mFromUserId +
            ", mToUserId=" + mToUserId +
            '}';
  }

    public String getmFromUserNickName() {
        return mFromUserNickName;
    }

    public void setmFromUserNickName(String mFromUserNickName) {
        this.mFromUserNickName = mFromUserNickName;
    }

    public String getmFromUserHeadPortrait() {
        return mFromUserHeadPortrait;
    }

    public void setmFromUserHeadPortrait(String mFromUserHeadPortrait) {
        this.mFromUserHeadPortrait = mFromUserHeadPortrait;
    }

  public int getMId() {
    return mId;
  }

  public void setMId(int mId) {
    this.mId = mId;
  }


  public String getMPostMessages() {
    return mPostMessages;
  }

  public void setMPostMessages(String mPostMessages) {
    this.mPostMessages = mPostMessages;
  }


  public byte getMStatus() {
    return mStatus;
  }

  public void setMStatus(byte mStatus) {
    this.mStatus = mStatus;
  }


  public Timestamp getMTime() {
    return mTime;
  }

  public void setMTime(Timestamp mTime) {
    this.mTime = mTime;
  }


  public int getMMessagesTypeId() {
    return mMessagesTypeId;
  }

  public void setMMessagesTypeId(int mMessagesTypeId) {
    this.mMessagesTypeId = mMessagesTypeId;
  }


  public String getMFromUserId() {
    return mFromUserId;
  }

  public void setMFromUserId(String mFromUserId) {
    this.mFromUserId = mFromUserId;
  }


  public String getMToUserId() {
    return mToUserId;
  }

  public void setMToUserId(String mToUserId) {
    this.mToUserId = mToUserId;
  }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put(FROM, this.mFromUserId);
        json.put(TO, this.mToUserId);
        json.put(MESSSAGE_CONTENT, this.mPostMessages);
        json.put(MESSAGE_TYPE, this.mMessagesTypeId);
        json.put(SEND_TIME, Utils.dateToStr(this.mTime,"yyyy-MM-dd HH:mm:ss"));
        json.put(MESSAGE_STATUS,this.mStatus);
        json.put(FROM_USER_HEADPORTRAIT,this.mFromUserHeadPortrait);
        json.put(FROM_USER_NICKNAME,this.mFromUserNickName);
        return json;
    }

    @Override
    public void readFromJson(JSONObject json) {
        if(json.has(FROM)){
            this.mFromUserId = json.getString(FROM);
        }
        if(json.has(TO)){
            this.mToUserId = json.getString(TO);
        }
        if(json.has(MESSSAGE_CONTENT)){
            this.mPostMessages = json.getString(MESSSAGE_CONTENT);
        }
        if(json.has(MESSAGE_TYPE)){
            this.mMessagesTypeId = json.getInt(MESSAGE_TYPE);
        }
        if(json.has(SEND_TIME)){
            this.mTime = Utils.strToSqlDate(json.getString(SEND_TIME),"yyyy-MM-dd HH:mm:ss");
        }
        if(json.has(MESSAGE_STATUS)){
            this.mStatus = (byte) json.getInt(MESSAGE_STATUS);
        }
        if(json.has(FROM_USER_HEADPORTRAIT)){
            this.mFromUserHeadPortrait = json.getString(FROM_USER_HEADPORTRAIT);
        }
        if(json.has(FROM_USER_NICKNAME)){
            this.mFromUserNickName = json.getString(FROM_USER_NICKNAME);
        }

    }
}
