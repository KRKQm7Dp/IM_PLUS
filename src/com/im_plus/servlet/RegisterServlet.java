package com.im_plus.servlet;

import com.im_plus.log.MyLog;
import com.im_plus.pojo.User;
import com.im_plus.service.UserService;
import com.im_plus.utils.ResponseInformation;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 注册处理
 */
@WebServlet("/register")
public class RegisterServlet extends JsonServlet {
	public static final String LOGINED_USER_SESSION_ATTR = "logined_user";
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json;charset=utf-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer = response.getWriter();
		String postData = ReadFromStream(request);
		if (postData==null) {
			String responseStr = ResponseInformation.getErrorInformation("数据请求为空");
			writer.println(responseStr);
			writer.close();
			return;
		}

        MyLog.log(this.getClass(), "注册请求信息：" + postData);
        JSONObject regUserJson = new JSONObject(postData);
		String loginId = regUserJson.getString("loginID");
		String nickName = regUserJson.getString("nickName");
		String pwd1 = regUserJson.getString("pwd1");
		String pwd2 = regUserJson.getString("pwd2");

        if (loginId == null || "".equals(loginId)){
            String responseStr = ResponseInformation.getErrorInformation("登录账号不能为为空");
            writer.println(responseStr);
            writer.close();
            return;
        }
		if (nickName == null || "".equals(nickName)){
            String responseStr = ResponseInformation.getErrorInformation("昵称不能为空");
            writer.println(responseStr);
            writer.close();
            return;
        }
        if(pwd1 == null || pwd1 == null || "".equals(pwd1) || "".equals(pwd2)) {
            String responseStr = ResponseInformation.getErrorInformation("请输入密码和确认密码");
            writer.println(responseStr);
            writer.close();
            return;
        }
        if (!pwd1.equals(pwd2)){
            System.out.println(pwd1 + " " + pwd2);
            String responseStr = ResponseInformation.getErrorInformation("两次密码输入不一致");
            writer.println(responseStr);
            writer.close();
            return;
        }

        User user = new User();
        user.setULoginId(loginId);
        user.setUNickName(nickName);
        user.setUPassWord(pwd1);
        user.setUHeadPortrait("img/defaultHead.jpg");  // 设置默认头像
        UserService userService = new UserService();
        if (userService.registerUser(user)){
            writer.println(ResponseInformation.getSuccessInformation());
            writer.close();
        }
        else{
            String responseStr = ResponseInformation.getErrorInformation("此账号已被注册");
            writer.println(responseStr);
            writer.close();
        }


	}
}
