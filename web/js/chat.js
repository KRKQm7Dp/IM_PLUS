/**
 * 定义发送消息的类型常量
 * @type {number}
 */
var MESSAGE_TYPE_ORDINARY = 0;
var MESSAGE_TYPE_IMAGE = 1;
var MESSAGE_TYPE_SYSTEMNOTIFY = 2;
var MESSAGE_TYPE_ADDFRIEND = 3;

/**
 * 定义两个字符串，用于辨别存入本地 localStorage 中消息类型（已读/未读）
 */
var LOCALSTORAGE_READ = '%1';
var LOCALSTORAGE_UNREAD = '%0';

/**
 * 定义消息接收状态常量
 * @type {number}
 */
var MESSAGE_STATUS_RECEIVED = 1;
var MESSAGE_STATUS_UNRECEIVED = 0;

/**
 * 全局变量，当前选中的聊天对象
 */
var TO_USER_ID;

/**
 * 全局变量，当前消息类型
 */
var NOW_MSG_TYPE;

/**
 * 控制图灵机器人开关
  */
var TURLING_ROBOT_SWITCH = false;

/**
 * 聊天室 ID
 */
var CHAT_ROOM_ID = 'chatRoom';

/**
 * 返回当前日期时间 "yyyy-MM-dd HH:mm:ss"
 * @returns {string}
 */
function getNowDate(){
    var date = new Date();
    var ymd = date.toLocaleDateString().replace(new RegExp('/','g'),'-');
    var time = date.getHours()+':'+date.getMinutes()+':'+date.getSeconds();
    return ymd + ' ' + time;
}

function showRightChatDiv(){
    if (TO_USER_ID == CHAT_ROOM_ID){  // 如果当前聊天框为公共聊天室，则显示图灵机器人开关
        $('#turling').css('display','block');
    }
    else{
        $('#turling').css('display','none');
    }
    $('.right').css('display','block');
    $('.show_info_div').css('display','none');
    $('.modify_user_info_div').css('display','none');
}
function showShowInfoDiv(){
    $('.right').css('display','none');
    $('.show_info_div').css('display','block');
    $('.modify_user_info_div').css('display','none');
}
function showModifyUserInfoDiv(){
    $('.right').css('display','none');
    $('.show_info_div').css('display','none');
    $('.modify_user_info_div').css('display','block');
}

$(document).ready(function(){
   initConnection();
   getOnlineUser();

   $('#sendMsg-link').click(function () {
       sendImgFile(TO_USER_ID);
       sendMessage(TO_USER_ID, NOW_MSG_TYPE);
   });

    $('.right').keydown(function() {  // 在右侧聊天框监听回车按键事件
        if (event.keyCode == "13") {//keyCode=13是回车键
            sendMessage(TO_USER_ID, NOW_MSG_TYPE);
        }
    });

    $('#search_link').click(function () {
        sendAddFriendReq();
    });

    $('#file').change(function (e) {
        var fileMsg = e.currentTarget.files;
        var fileName = fileMsg[0].name;
        $('#message-content').attr('value', fileName);
    });

    $('#imgFile').change(function (e) {
        var fileMsg = e.currentTarget.files[0];
        var src = window.URL.createObjectURL(fileMsg);
        $('.form_div img').attr('src', src);
    });

    $('.userInfo_div').click(function () {
        showModifyUserInfoDiv();
    });

    $('#submitModify_btn').click(function () {
        modifyUserInfo();
    });

    $('.switch').change(function () {
        TURLING_ROBOT_SWITCH = !TURLING_ROBOT_SWITCH;
        if (TURLING_ROBOT_SWITCH){
            addChatRoomTip("图灵机器人加入到聊天室");
        }
        else{
            addChatRoomTip("图灵机器人离开聊天室");
        }
    });


    $('#emoji').click(function(event){
        if (!$('#sinaEmotion').is(':visible')) {
            $(this).sinaEmotion('#message-content');
            event.stopPropagation();
        }
    });


    // $('#parse').click(function () {
    //     var content = $('#content').val();
    //     $('#article').html(content).parseEmotion();
    // })

});

window.onload = function(){
    $('#' + CHAT_ROOM_ID + '_' + MESSAGE_TYPE_ORDINARY).trigger("click");
};


$(window).unload(function(){
    closeConnection();
});

var msgArray = [];  // 定义未读消息队列，存放消息 JSON 对象
var hisArray = [];  // 定义历史消息队列，存放消息 JSON 对象
var websocket=null;
var addr = 'localhost';  // 服务器 ip
// var addr = '192.168.43.16';  // 服务器 ip
var port = '8889';           // 端口号
var url = 'ws://'+ addr +':'+ port +'/chat/' + loginedUserID;  // webSocket 连接地址
function initConnection(){
    if ("WebSocket" in window){
        websocket=new WebSocket(url);
    }
    else{
        alert('您当前的浏览器不支持Websocket!');
    }
    websocket.onerror=function(){
        alert('websocke通信协议发生错误');
    }
    websocket.onopen=function(){
        console.log('websocket协议已经打开');
    }
    websocket.onclose=function(){
        // 在此添加注销代码
        // loginout();
        alert('您当前已经被注销！请重新登录');
        // window.location.href="login.html";
    }
    window.onbeforeunload=function(){
        websocket.close();
    }
    websocket.onmessage=function(event){
        if (typeof(event.data) == "string") {
            var msg=JSON.parse(event.data);
            console.log("接收到的消息"+event.data);
            if (msg.messageType == MESSAGE_TYPE_ORDINARY){ // 普通类型消息
                if ((msg.from == TO_USER_ID && msg.to == loginedUserID)
                    || (TO_USER_ID == CHAT_ROOM_ID && msg.from != TO_USER_ID && msg.to == TO_USER_ID)
                    && msg.status == 0){  // 接收到消息的发送者和当前的聊天对象相同, 或接收到聊天室消息
                    addRecievedMessage(msg);  // 直接将消息显示在对话框中
                    scrollToBottom();
                    msg.status = 1;

                    hisArray = getLocalStorage(LOCALSTORAGE_READ);
                    hisArray.push(msg);
                    saveLocalStorage(JSON.stringify(hisArray), LOCALSTORAGE_READ);

                }
                else{
                    if(msg.to == loginedUserID){
                        addMsgRedDot(msg.from, MESSAGE_TYPE_ORDINARY); // 为接收到的消息添加小红点
                    }
                    else if(msg.to == CHAT_ROOM_ID){
                        addMsgRedDot(CHAT_ROOM_ID, MESSAGE_TYPE_ORDINARY);
                    }
                    // 否则保存至消息队列中, 并记录消息来源 msg.from
                    msgArray.push(msg);
                }

            }else if(msg.messageType == MESSAGE_TYPE_ADDFRIEND){ // 如果是请求添加好友的消息
                addMsgRedDot(msg.from, MESSAGE_TYPE_ADDFRIEND); // 为接收到的消息添加小红点
                console.log(event.data);
                addFriendReqToList(msg);

            }else if(msg.messageType == MESSAGE_TYPE_SYSTEMNOTIFY){  // 如果是系统通知
                alert(msg.messageContent);
                getOnlineUser();
            }
            else if(msg.messageType == MESSAGE_TYPE_IMAGE){ // 如果是图片消息
                if ((msg.from == TO_USER_ID && msg.to == loginedUserID)
                    || (TO_USER_ID == CHAT_ROOM_ID && msg.from != TO_USER_ID && msg.to == TO_USER_ID)
                    && msg.status == 0){  // 接收到消息的发送者和当前的聊天对象相同, 或接收到聊天室消息
                    addRecievedImgMsg(msg);  // 接收到图片消息
                    scrollToBottom();
                    msg.status = 1;

                    hisArray = getLocalStorage(LOCALSTORAGE_READ);
                    hisArray.push(msg);
                    saveLocalStorage(JSON.stringify(hisArray), LOCALSTORAGE_READ);

                }
                else{
                    if(msg.to == loginedUserID){
                        addMsgRedDot(msg.from, MESSAGE_TYPE_ORDINARY); // 为接收到的消息添加小红点
                    }
                    // 否则保存至消息队列中, 并记录消息来源 msg.from
                    msgArray.push(msg);
                }
            }
        }
        else{  // 如接收到的数据不是 string 类型，暂时无用
            var reader = new FileReader();
            reader.onload = function(evt){
                if(evt.target.readyState == FileReader.DONE){
                    var url = evt.target.result;
                    alert(url);
                    $('.show_info_div img').attr('src', url);
                }
            }
            reader.readAsDataURL(event.data);
        }

    }
}

function addChatRoomTip(tipContent) {
    var htmlTemplate = '<div class="conversation-start">' +
                '<span>{{content}}</span>' +
                '</div>'
    var html = htmlTemplate.replace(/{{content}}/, tipContent);
    $('#dialogBox').append(html);
}

/**
 * 为接收到的消息添加小红点提示
 * @param fLoginID
 */
function addMsgRedDot(fLoginID, messageContent){
    var id = '#'+ fLoginID + '_' + messageContent;
    $(id).css('background-image', 'url(../img/msg_redDot.png)');
}

/**
 * 图灵机器人
 * @param message
 */
function turling(message){
    if(TURLING_ROBOT_SWITCH){  // 控制开关
        $.getJSON("http://www.tuling123.com/openapi/api?key=6ad8b4d96861f17d68270216c880d5e3&info=" + message,function(data){
            var msg = {
                time: getNowDate(),
                status: 1,
                from: 'IM_ROBOT',
                messageType: '',
                to: CHAT_ROOM_ID,
                messageContent: data.text
            };

            if(data.code == 100000){
                msg.messageType = MESSAGE_TYPE_ORDINARY;
                addRecievedMessage(msg);
            }
            if(data.code == 200000){
                msg.messageType = MESSAGE_TYPE_IMAGE;
                addRecievedImgMsg(msg);
            }

            hisArray = getLocalStorage(LOCALSTORAGE_READ);
            hisArray.push(msg);
            saveLocalStorage(JSON.stringify(hisArray), LOCALSTORAGE_READ);

            scrollToBottom();
        });
    }
}

/**
 *  ajax 提交修改用户信息表单
 */
function modifyUserInfo(){
    console.log("modifyUserInfo");
    var formData = new FormData($('#modifyUserInfo_form')[0]);
    $.ajax({
        url: 'modifyUserInfo',
        type: 'POST',
        data: formData,
        processData:false,
        contentType: false,
        cache: false,
        success: function(data){
            alert('成功修改个人信息');
        },
        error: function(){
            alert('修改个人信息失败');
        }
    })
}

/**
 * 把好友添加请求增加到好友列表中
 * @param msg
 */
function addFriendReqToList(msg){
    var onlineUserTemplate = '<li class="person" data-chat="person" id="'+ msg.from +'_' + msg.messageType +'" fNickName="'+ msg.fromUserNickName +'">' +
        '<img src='+ msg.fromUserHeadPortrait +' alt="" />' +
        '<span class="name">请求好友消息</span>' +
        '<span class="time">{{time}}</span>' +
        '<span class="preview">{{messageContent}}</span>' +
        '</li>';
    var onlineUser = onlineUserTemplate.replace(/{{messageContent}}/, msg.fromUserNickName + msg.messageContent)
        .replace(/{{time}}/,msg.time.split(' ')[1]);
    $(".people").append(onlineUser);
}

/**
 * 发生需要断开连接事件的时候，调用此方法
 * 本方法将完成：
 * 1.断开 websocket 连接
 * 2.将消息队列中的未读消息保存到本地
 */
function closeConnection(){
    websocket.close();
    if(msgArray.length != 0 && msgArray != undefined) {  // 判断当前消息队列中是否还有消息
        var unreadMsg_JsonArray = getLocalStorage(LOCALSTORAGE_UNREAD);

        for (var i in msgArray){
            unreadMsg_JsonArray.push(msgArray[i]);
        }

        saveLocalStorage(JSON.stringify(unreadMsg_JsonArray), LOCALSTORAGE_UNREAD);  // 保存到 localStorage 中必须为字符串类型！！！
    }

}

/**
 * 通过 websocket 发送消息
 * @param toUserLoginID
 * @returns {boolean}
 */
function sendMessage(toUserLoginID, messageType){
    var content=$('#message-content').val();
    if (content==null || content.trim().length<=0){
        $('#message-content')[0].focus();
        console.log('请输入消息内容！');
        return false;
    }
    console.log('发送者ID:'+loginedUserID + ' 接收者ID:'+toUserLoginID);
    var msg={
        time: getNowDate(),
        messageContent: content.toString(),
        messageType: messageType,
        status: 0,
        from: loginedUserID.toString(),
        to: toUserLoginID.toString()
    }
    addSentMessage(msg);   // 添加发送的消息到消息框中

    if (toUserLoginID == CHAT_ROOM_ID){
        turling(msg.messageContent);
    }

    $('#message-content').val('');
    if (websocket==null){
        initConnection();
    }
    websocket.send(JSON.stringify(msg));  // websocket 发送消息

    hisArray = getLocalStorage(LOCALSTORAGE_READ);
    hisArray.push(msg);
    saveLocalStorage(JSON.stringify(hisArray), LOCALSTORAGE_READ);

    $('#message-content')[0].focus();
    scrollToBottom();
    return true;
}

/**
 * 发送图片消息
 * @param toUserLoginID
 */
function sendImgFile(toUserLoginID){
    $('#message-content').attr('value', '');
    var inputElement = document.getElementById("file");
    var fileList = inputElement.files;
    var file=fileList[0];
    if(!file) return;
    // websocket.send(file.name+":fileStart");
    var reader = new FileReader();
    //以二进制形式读取文件
    reader.readAsDataURL(file);
    //文件读取完毕后该函数响应
    reader.onload = function loaded(evt) {
        var msg={
            time: getNowDate(),
            messageContent:evt.target.result,
            messageType:MESSAGE_TYPE_IMAGE,
            status:0,
            from:loginedUserID.toString(),
            to:toUserLoginID.toString()
        }
        addSentImgMsg(msg);
        scrollToBottom();
        // var blob = evt.target.result;
        //发送二进制表示的文件
        websocket.send(JSON.stringify(msg));
        console.log('发送文件数据结束');
    }
    inputElement.outerHTML=inputElement.outerHTML; //清空<input type="file">的值
}

/**
 * 发送添加好友请求
 * @returns {boolean}
 */
function sendAddFriendReq() {
    var findID = $("#search_box").val().trim(); // 搜索框中的内容
    if(findID == null){
        // 如果搜索框内容为空，则显示好友列表
    }
    var msg={
        time: getNowDate(),
        messageContent:"请求添加为好友",
        messageType:MESSAGE_TYPE_ADDFRIEND,
        status:0,
        from:loginedUserID.toString(),
        to:findID
    }
    if (websocket==null){
        initConnection();
    }
    websocket.send(JSON.stringify(msg));  // websocket 发送消息
    return true;
}

/**
 * 将接收到的图片消息显示到聊天框中
 * @param msg
 */
function addSentImgMsg(msg){
    var sentMsgTemplate = '<div class="bubble me"><img style="width: 200px" src="'+ msg.messageContent +'"></div>';
    $('#dialogBox').append(sentMsgTemplate);
}

/**
 * 将发送的图片消息显示到聊天框中
 * @param msg
 */
function addRecievedImgMsg(msg){
    var sentMsgTemplate = '<div class="bubble you"><img style="width: 200px" src="'+ msg.messageContent +'"></div>';
    $('#dialogBox').append(sentMsgTemplate);
}

/**
 * 将发送的消息显示到聊天框中
 * @param msg 发送消息的内容
 */
function addSentMessage(msg) {
    var content = msg.messageContent;
    $('#hiddenDiv').empty();
    $("#hiddenDiv").parseEmotion();
    $('#hiddenDiv').html(content).parseEmotion();
    $('#article').html(content).parseEmotion();
    var html = $('#hiddenDiv').html();
    console.log('sent:' + html);

    var sentMsgTemplate = '<div class="bubble me">{{content}}</div>';
    var msgText = sentMsgTemplate.replace(/{{content}}/, html);
    $('#dialogBox').append(msgText);
}

/**
 * 将接收到的消息显示到聊天框中
 * @param msg 接收到消息的内容
 */
function addRecievedMessage(msg) {
    var content = msg.messageContent;
    $('#hiddenDiv').empty();
    $("#hiddenDiv").parseEmotion();
    $('#hiddenDiv').html(content).parseEmotion();
    var html = $('#hiddenDiv').html();
    console.log('recv:' + html);

    var recMsgTemplate = '<div class="bubble you">{{content}}</div>';
    var msgText = recMsgTemplate.replace(/{{content}}/, msg.from + ':\n' + html);
    $('#dialogBox').append(msgText);
}

/**
 * 将消息框中的滚动条至于底部
 */
function scrollToBottom() {
    $("#dialogBox").scrollTop($("#dialogBox")[0].scrollHeight);
}

/**
 * ajax 从服务器加载好友列表
 */
function getOnlineUser(){
    var loginUserID_json = {
        loginUserID: loginedUserID+""
    };
    console.log(JSON.stringify(loginUserID_json));
    $.ajax({
        url:'userlist',
        type:'POST',
        data: JSON.stringify(loginUserID_json),
        dataType: 'JSON',
        contentType: 'application/json;charset=utf-8',
        success: function(data){
            $(".people").empty();
            addChatRoom();
            $.each(data,function (key, val) {  // key:分组类型  val:分组中的用户
                console.log("key="+key);
                // $.each(val, function (k, v) {
                //     console.log("k="+k);
                //     console.log("v="+v);
                // });
                console.log("我的好友:"+ val);
                var onlineUserTemplate = '<li class="person" data-chat="person" id="'+ val["uLoginId"] + '_' + MESSAGE_TYPE_ORDINARY +'" fNickName="'+ val["uNickName"] +'">' +
                    '<img src='+ val["uHeadPortrait"] +' alt="" />' +
                    '<span class="name">{{UNickName}}</span>' +
                    '<span class="time">{{time}}</span>' +
                    '<span class="preview">{{historyMsg}}</span>' +
                    '</li>';
                var onlineUser = onlineUserTemplate.replace(/{{UNickName}}/, val["uNickName"])
                                                .replace(/{{time}}/,"12:00")
                                                .replace(/{{historyMsg}}/,"历史消息...");
                $(".people").append(onlineUser);

            })

        },
        error: function(){
            alert("获取好友列表失败");
            console.log("获取好友列表失败");
        }
    })
}

/**
 * 添加公共聊天室
 */
function addChatRoom() {
    var onlineUserTemplate = '<li class="person" data-chat="person" id="' + CHAT_ROOM_ID + '_' + MESSAGE_TYPE_ORDINARY +'" fNickName="公共聊天室">' +
        '<img src="img/ChatRoom.jpg" alt="" />' +
        '<span class="name">{{UNickName}}</span>' +
        '<span class="time">{{time}}</span>' +
        '<span class="preview">{{historyMsg}}</span>' +
        '</li>';
    var onlineUser = onlineUserTemplate.replace(/{{UNickName}}/, "公共聊天室")
        .replace(/{{time}}/,"12:00")
        .replace(/{{historyMsg}}/,"历史消息...");
    $(".people").append(onlineUser);

}

/**
 * ajax 从服务器查询历史消息
 */
function loadHisMsgFromServer(earliestTime) {
    console.log("loadHisMsgFromServer " + earliestTime);
    var reqJson = {
        from: loginedUserID + '',
        to: TO_USER_ID + '',
        timeStamp: earliestTime,
        msgNum: 10
    };
    $.ajax({
        url:'loadHisMsg',
        type:'POST',
        data: JSON.stringify(reqJson),
        dataType: 'JSON',
        contentType: 'application/json;charset=utf-8',
        success: function(data){
            // console.log(data);
            // 将从服务器查询到的历史记录保存到本地
            addServerMsgToLocal(data);
            refreshChatBox();
        },
        error: function(){
            alert('从服务器获取历史记录失败');
            console.log("从服务器获取历史记录失败");
        }
    });
}

/**
 * 将从服务器获取的历史消息保存至本地
 * @param msgArr
 */
function addServerMsgToLocal(msgArr){
    var localMsgArr = getLocalStorage(LOCALSTORAGE_READ);
    var newMsgArr = msgArr.concat(localMsgArr); // 从服务器获取的历史消息和本地的历史消息相连接
    saveLocalStorage(JSON.stringify(newMsgArr), LOCALSTORAGE_READ);
}

/**
 *  为好友列表中的每一项添加事件
 */
$('.people').on('click','li',function () {
    $(".show_info_div").css('background-image', 'none');
    $(this).css('background-image', 'none');
    var f_loginId = $(this).attr('id').split('_')[0];
    TO_USER_ID = f_loginId;
    var f_nickName = $(this).attr('fNickName');
    var msg_type = $(this).attr('id').split('_')[1];
    NOW_MSG_TYPE = msg_type;  // 给全局变量 当前聊天消息类型 赋值
    console.log("当前选中好友的 ID：" + f_loginId + " 当前选中的好友昵称：" + f_nickName + " 选中的消息类型:" + msg_type);
    if (msg_type == MESSAGE_TYPE_ADDFRIEND){ // 如果是添加好友提示
        showShowInfoDiv();
        var reqContent = $(this).find('span')[2].innerText;
        var reqTime = $(this).find('span')[1].innerText;
        var reqHead = $(this).find('img').attr('src');
        addReqFriendMsgToShowInfoDiv(f_loginId, f_nickName, reqContent, reqTime, reqHead);
    }
    else if(msg_type == MESSAGE_TYPE_ORDINARY){  // 正常的好友列表 或 公共聊天室
        showRightChatDiv();
        refreshChatBox();
        scrollToBottom();
        chat.name.innerHTML = f_nickName;     // 聊天框上显示名字
        this.classList.contains('active') || setAciveChat(this);
    }
});

/**
 * 添加好友请求信息到 show_info_div 中
 */
function addReqFriendMsgToShowInfoDiv(f_loginId, f_nickName, reqContent, reqTime, reqHead){
    $('.show_info_div').empty();
    $('.show_info_div').css('background-color', '#eeeeee');
    var template = '<div class="top"><span><span class="name">添加好友请求ID: '+ f_loginId+'</span></span></div>' +
        '<div class="info">' +
        '<img src='+ reqHead +' />' +
        '<div class="reqFriendMsg">{{content}}</div>' +
        '<div class="time">{{time}}</div>' +
        '<button class="btn_agree" onclick="agreeFriendReq(\''+ f_loginId +'\')">同意</button>' +
        '<button class="btn_refuse" onclick="refuseFriendReq(\'' + f_loginId +'\')">拒绝</button>' +
        '</div>';
    var templateHTML = template.replace(/{{content}}/,reqContent).replace(/{{time}}/, reqTime);
    $('.show_info_div').append(templateHTML);
}

function agreeFriendReq(reqID) {
    console.log('agree' + reqID);
    responseAddFriendReq(reqID.toString(), 1);
}

function refuseFriendReq(reqID) {
    console.log('refuse' + reqID);
    responseAddFriendReq(reqID.toString(), 0);
}

/**
 * ajax 提交回应好友请求的消息（接收/拒绝）
 * @param reqID
 * @param responseCode 回应请求码：1 同意 0 拒绝
 */
function responseAddFriendReq(reqID, responseCode){
    var msg = {
        from:loginedUserID+'',
        to: reqID+'',
        responseCode: responseCode,
        time: getNowDate(),
    };
    $.ajax({
        url:'responseAddFriendServlet',
        type:'POST',
        data: JSON.stringify(msg),
        dataType: 'JSON',
        contentType: 'application/json;charset=utf-8',
        success: function(data){
            if(data.status == 'success'){
                getOnlineUser();

                $('.right').css({'display': 'block'});
                $('.show_info_div').css({'display': 'none'});
            }else if(data.status == 'error'){
                alert(data.reason);
            }
        },
        error: function(){
            console.log("回应好友请求失败");
        }
    })
}


/**
 * 执行所有消息加载方法
 */
function refreshChatBox(){
    $('#dialogBox').empty();  // 清空消息对话框
    addLoadHisMsgLink();
    loadMsgFromStorageRead();  // 从本地历史记录中读取消息
    loadMsgFromStorageUnread(); // 先查看本地记录中是否有未读消息
    loadMsgFromArray();  // 再查看当消息队列中是否有未读消息
}

/**
 * 在聊天界面的最上部添加加载历史消息的超链接
 */
function addLoadHisMsgLink(){
    var earliestTime = "'"+getEarliestTime()+"'";
    var loadHisMsgtemplate = '<div class="conversation-start">' +
        '<span><a href="javascript:loadHisMsgFromServer('+earliestTime+');" >同步历史消息记录</a></span>' +
        '</div>';
    $('#dialogBox').append(loadHisMsgtemplate);
}

/**
 * 获取和当前聊天对象最早一条本地聊天记录的时间
 * @returns {string|number|((label?: string) => void)}
 */
function getEarliestTime(){
    var earliestMsg = loadEarliestMsgFromStorageRead();
    var earliestTime; // 用于记录最早一条聊天记录的时间
    if(earliestMsg == null) {
        earliestTime = getNowDate(); // 如果本地没有历史消息，则最早时间为当前
    }
    else{
        earliestTime = earliestMsg.time;
    }
    return earliestTime;
}

/**
 * 加载和当前聊天对象最早的一条本地聊天记录
 * @returns {*} 如果在本地有聊天记录则返回最早的一条，否则返回 null
 */
function loadEarliestMsgFromStorageRead(){
    var msgJsonArr = getLocalStorage(LOCALSTORAGE_READ);
    var earliestMsg = null;
    for (var i in msgJsonArr){
        if(msgJsonArr[i].from == TO_USER_ID){
            earliestMsg = msgJsonArr[i];
            break;
        }
        else if (msgJsonArr[i].from == loginedUserID && msgJsonArr[i].to == TO_USER_ID){
            earliestMsg = msgJsonArr[i];
            break;
        }
    }
    return earliestMsg;
}

/**
 * 查看当前消息队列中是否有此好友发送来的消息
 */
function loadMsgFromArray(){
    for (var i = 0; i < msgArray.length; i++){
        // console.log("******** " + msgArray[i]);
        if(msgArray[i].from == TO_USER_ID && msgArray[i].to == loginedUserID && msgArray[i].status == 0){  // 查找当前消息队列中是否有此好友发来的消息
            if(msgArray[i].messageType == MESSAGE_TYPE_ORDINARY){
                addRecievedMessage(msgArray[i]);  // 直接将消息显示在对话框中
            }
            else if(msgArray[i].messageType == MESSAGE_TYPE_IMAGE){
                addRecievedImgMsg(msgArray[i]);
            }
            msgArray[i].status = 1;  // 将消息状态置为已读

            hisArray = getLocalStorage(LOCALSTORAGE_READ);
            hisArray.push(msgArray[i]);
            saveLocalStorage(JSON.stringify(hisArray), LOCALSTORAGE_READ);

            msgArray.splice(i--, 1); // 将此条消息从消息队列中删除，注：此处一定要 “i--”,不能 “--i”，
                                    // 思路为先将数组中此元素删除，然后将索引位置向前移动一位（很关键，有利于维护本地消息队列的大小防止内存溢出）
        }
        else if(msgArray[i].from != loginedUserID && TO_USER_ID == CHAT_ROOM_ID && msgArray[i].to == TO_USER_ID && msgArray[i].status == 0){
            if(msgArray[i].messageType == MESSAGE_TYPE_ORDINARY){
                addRecievedMessage(msgArray[i]);  // 直接将消息显示在对话框中
            }
            else if(msgArray[i].messageType == MESSAGE_TYPE_IMAGE){
                addRecievedImgMsg(msgArray[i]);
            }
            msgArray[i].status = 1;  // 将消息状态置为已读

            hisArray = getLocalStorage(LOCALSTORAGE_READ);
            hisArray.push(msgArray[i]);
            saveLocalStorage(JSON.stringify(hisArray), LOCALSTORAGE_READ);

            msgArray.splice(i--, 1); // 将此条消息从消息队列中删除，注：此处一定要 “i--”,不能 “--i”，
                                        // 思路为先将数组中此元素删除，然后将索引位置向前移动一位（很关键，有利于维护本地消息队列的大小防止内存溢出）
        }
    }

}

/**
 * 从 localStorage 中加载历史消息
 */
function loadMsgFromStorageRead(){
    var msgJsonArr = getLocalStorage(LOCALSTORAGE_READ);
    for (var i in msgJsonArr){
        if(msgJsonArr[i].from == TO_USER_ID && msgJsonArr[i].to == loginedUserID){
            if(msgJsonArr[i].messageType == MESSAGE_TYPE_ORDINARY){
                addRecievedMessage(msgJsonArr[i]);
            }
            else if(msgJsonArr[i].messageType == MESSAGE_TYPE_IMAGE){
                addRecievedImgMsg(msgJsonArr[i]);
            }
        }
        else if (msgJsonArr[i].from == loginedUserID && msgJsonArr[i].to == TO_USER_ID){
            if(msgJsonArr[i].messageType == MESSAGE_TYPE_ORDINARY){
                addSentMessage(msgJsonArr[i]);
            }
            else if(msgJsonArr[i].messageType == MESSAGE_TYPE_IMAGE){
                addSentImgMsg(msgJsonArr[i]);
            }
        }
        else if(msgJsonArr[i].from != loginedUserID && TO_USER_ID == "chatRoom" && msgJsonArr[i].to == TO_USER_ID){ // 接收到聊天室消息
            if(msgJsonArr[i].messageType == MESSAGE_TYPE_ORDINARY){
                addRecievedMessage(msgJsonArr[i]);
            }
            else if(msgJsonArr[i].messageType == MESSAGE_TYPE_IMAGE){
                addRecievedImgMsg(msgJsonArr[i]);
            }
        }
        var isReadMsg = msgJsonArr[i];
        // console.log("从本地消息记录中读取的信息：" + isReadMsg.messageContent);
    }
}

/**
 * 从 localStorage 中加载未读消息
 */
function loadMsgFromStorageUnread() {
    var msgJsonArr = getLocalStorage(LOCALSTORAGE_UNREAD);
    // for (var i in msgJsonArr){
    for (var i = 0; i < msgJsonArr.length; i++){
        if(msgJsonArr[i].from == TO_USER_ID && msgJsonArr[i].to == loginedUserID && msgJsonArr[i].status == 0){
            addRecievedMessage(msgJsonArr[i]);
            msgJsonArr[i].status = 1;
            var isReadMsg = msgJsonArr[i];
            // console.log("从本地未读历史中读取的消息：" + isReadMsg.messageContent);
            // 此处 把本地 localStorage 中的未读消息，保存到本地已读消息中
            hisArray = getLocalStorage(LOCALSTORAGE_READ);
            hisArray.push(isReadMsg);
            saveLocalStorage(JSON.stringify(hisArray), LOCALSTORAGE_READ);

            msgJsonArr.splice(i--, 1);  // 从本地读出的未读消息队列中删除本条
        }
        else if(msgJsonArr[i].from != loginedUserID && TO_USER_ID == "chatRoom" && msgJsonArr[i].to == TO_USER_ID){
            addRecievedMessage(msgJsonArr[i]);
            msgJsonArr[i].status = 1;
            var isReadMsg = msgJsonArr[i];
            // console.log("从本地未读历史中读取的消息：" + isReadMsg.messageContent);
            // 此处 把本地 localStorage 中的未读消息，保存到本地已读消息中
            hisArray = getLocalStorage(LOCALSTORAGE_READ);
            hisArray.push(isReadMsg);
            saveLocalStorage(JSON.stringify(hisArray), LOCALSTORAGE_READ);

            msgJsonArr.splice(i--, 1);  // 从本地读出的未读消息队列中删除本条
        }
    }
    saveLocalStorage(JSON.stringify(msgJsonArr), LOCALSTORAGE_UNREAD); // 将更改后的未读消息存储到本地
}

/**
 * 获取本地 localStorage
 * @param flag 为消息类型（已读/未读）
 * @returns {any} 返回 json 数组
 */
function getLocalStorage(flag) {
    var storage = window.localStorage.getItem(loginedUserID + flag);
    if (null == storage) {
        storage = [];
    }else {
        storage = JSON.parse(storage); // 将从 localStorage 中读出的 json 字符串转换成 JSON 数组
    }
    return storage;
}

/**
 * // 将接收到的消息保存到 localStorage，并区分未读消息和历史消息
 * @param msgString 消息内容（json 格式的字符串）
 * @param flag  消息类型（已读/未读）
 * @returns {boolean} 返回 true 说明保存成功
 */
function saveLocalStorage(msgString, flag){
    if(!window.localStorage){
        alert("当前浏览器不支持 localStorage ，请切换浏览器！");
        return false;
    }
    else{
        window.localStorage.setItem(loginedUserID + flag , msgString);
        return true;
    }
}


// document.querySelector('.chat[data-chat=person]').classList.add('active-chat');
// document.querySelector('.person[data-chat=person]').classList.add('active');

var friends = {
        list: document.querySelector('ul.people'),
        all: document.querySelectorAll('.left .person'),
        name: '' },
    chat = {
        container: document.querySelector('.container .right'),  // 聊天框
        current: null,
        person: null,
        name: document.querySelector('.container .right .top .name') // 聊天框上显示名字
    };

/**
 * 为好友列表和消息气泡添加动画效果
 * @param f 当前点击的 dom 对象
 */
function setAciveChat(f) {
    if (friends.list.querySelector('.active') != null){
        friends.list.querySelector('.active').classList.remove('active');
    }
    f.classList.add('active');
    chat.current = chat.container.querySelector('.active-chat');
    chat.person = f.getAttribute('data-chat');
    if (chat.current != null){
        chat.current.classList.remove('active-chat');
    }
    chat.container.querySelector('[data-chat="' + chat.person + '"]').classList.add('active-chat');
}
