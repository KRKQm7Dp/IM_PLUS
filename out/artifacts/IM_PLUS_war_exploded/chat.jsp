<%@ page import="com.im_plus.pojo.User" %>
<%@ page import="com.im_plus.servlet.LoginServlet" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="en" >

<head>
  <meta charset="UTF-8">
  <%
    User user = (User) session.getAttribute(LoginServlet.LOGINED_USER_SESSION_ATTR);
    if(user == null){  // 判断是否存在登录用户 若不存在则返回登录页面
        request.getRequestDispatcher("login.html").forward(request, response);
    }
  %>

  <title>loginUser:<%= user.getUNickName() %></title>

  <link href="https://fonts.googleapis.com/css?family=Source+Sans+Pro:400,600" rel="stylesheet">

  <meta name="viewport" content="width=device-width, initial-scale=1">

  <link rel="stylesheet" href="css/reset.min.css">

  <link rel="stylesheet" href="css/chat.css">

  <link rel="stylesheet" href="css/turlingRobotSwitch.css">

    <script  src="js/jquery-2.1.4.min.js"></script>

    <link rel="stylesheet" href="emoji_plugin/dist/jquery-sina-emotion.min.css"/>
    <script src="emoji_plugin/src/jquery-sina-emotion.js"></script>
    <script async src="https://www.googletagmanager.com/gtag/js?id=UA-57981079-1"></script>

    <link rel="shortcut icon" type="image/x-icon" href="../img/IM_PLUS_logo.jpg" />

  <script>
    var loginedUserID = '<%= user.getULoginId() %>';
    var loginedUserNickName = '<%= user.getUNickName() %>';
    var loginedUserSex = <%= user.getUSex() %>;
    var loginedUserBirthday = '<%= user.getUBirthday() %>';
    var loginedUserEmail = '<%= user.getUEmail() %>';
    var loginedUserHead = '<%= user.getUHeadPortrait() %>';
    var loginedUserSign = '<%= user.getUSignaTure() %>';
  </script>

</head>

<body>

<div class="wrapper">
  <div class="container">
    <%-- 用户列表 --%>
    <div class="left">
      <div class="top">
        <div class="userInfo_div">
            <img src="<%=user.getUHeadPortrait() %>"/>
            <span><%=user.getUNickName() %></span>
        </div>
        <div class="search_div">
          <input type="text" placeholder="Search" id="search_box" />
          <a href="javascript:;" class="search" id="search_link"></a>
        </div>
      </div>
      <ul class="people">
        <%--<li class="person" data-chat="person" id="aaaaaa">--%>
          <%--<img src="img/dog.png" alt="" />--%>
          <%--<span class="name">Thomas Bangalter</span>--%>
          <%--<span class="time">2:09 PM</span>--%>
          <%--<span class="preview">I was wondering...</span>--%>
        <%--</li>--%>
      </ul>
    </div>
        <%-- 聊天框 --%>
        <div class="right">
      <div class="top">
          <span class="to">To: </span><span class="name"></span>
          <span class="turling" id="turling">图灵机器人
            <label class="switch">
              <input type="checkbox">
              <div class="slider round"></div>
            </label>
          </span>
      </div>

      <div class="chat" data-chat="person" id="dialogBox">
        <div class="conversation-start">
          <span>Today, 5:38 PM</span>
        </div>
        <div class="bubble me">hello</div>
        <div class="bubble you">hi</div>
      </div>

      <div class="write">
        <%--<a href="javascript:;" class="write-link attach" id="sendImg-link"></a>--%>
          <div class="fileDiv"><input type="file" class="inputFile" id="file"/></div>
        <input type="text" class="inputText" id="message-content"/>
            <div class="emojiDiv"><button id="emoji" class="emoji"></button></div>
        <%--<a href="javascript:;" class="write-link smiley" id="sendSmile-link"></a>--%>
        <a href="javascript:;" class="write-link send" id="sendMsg-link"></a>
      </div>
    </div>
        <%-- 初始显示内容 div --%>
      <div class="show_info_div">

      </div>

      <div class="modify_user_info_div">
          <div class="top">
              <span class="to">用户资料：</span>
              <span class="name"><%=user.getULoginId() %></span>
          </div>
          <div class="form_div">
              <img id="modifyHeadImg" src="<%=user.getUHeadPortrait() %>" alt="用户头像">
              <table>
                  <form id="modifyUserInfo_form" enctype="multipart/form-data">
                      <tr><td colspan="2"><input type="file" name="imgFile" id="imgFile"></td></tr>
                      <tr><td>账　号：</td><td><label><%=user.getULoginId() %></label></td></tr>
                      <tr><td>昵　称：</td><td><input style="width: 200px" type="text" name="nickName" value="<%=user.getUNickName()%>"/></td></tr>
                      <tr><td>密　码：</td><td><input style="width: 200px" type="password" name="password" value="123456"/></td></tr>
                      <tr><td>邮　箱：</td><td><input style="width: 200px" type="email" name="email" value="<%=user.getUEmail() %>"/></td></tr>
                      <tr><td>性　别：</td><td><input type="radio" id="radio_male" name="sex" value="男"/>男
                      <input type="radio" id="radio_female" name="sex" value="女"/>女</td></tr>
                      <tr><td>生　日：</td><td><input style="width: 200px" name="birthday" type="date" value=""/></td></tr>
                      <tr><td>简　介：</td><td><textarea style="width: 200px" name="sign" rows="6" cols="30">这个人很优秀！！</textarea></td></tr>
                  </form>
                  <tr><td></td><td style="text-align: left;"><button id="submitModify_btn">确认修改</button></td></tr>
              </table>
          </div>
      </div>
  </div>
    <%-- 此 div 用于每次收发表情的中转，实现 emoji 的解析 --%>
    <div id="hiddenDiv" style="display: none;border: 1px solid red; width: 300px; height: 100px;">hhhhhhhhhhhhhh</div>
</div>



<script  src="js/chat.js" charset="UTF-8"></script>


</body>

</html>
