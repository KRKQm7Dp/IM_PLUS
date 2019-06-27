package com.im_plus.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.im_plus.log.MyLog;
import com.im_plus.pojo.User;
import com.im_plus.service.UserService;
import com.im_plus.utils.ResponseInformation;
import org.json.JSONObject;



/**
 * 登录处理
 */
@WebServlet("/login")
public class LoginServlet extends JsonServlet {
	public static final String LOGINED_USER_SESSION_ATTR = "logined_user";
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json;charset=utf-8");
		response.setCharacterEncoding("UTF-8");
		String loginData = ReadFromStream(request);

		JSONObject loginUserJson = new JSONObject(loginData);
		MyLog.log(this.getClass(),"登录账号：" + loginData);

        HttpSession session = request.getSession();
        String systemCode = (String) session.getAttribute("checkcode");
        if(loginUserJson.getString("checkCode") == null || "".equals(loginUserJson.getString("checkCode"))){
            PrintWriter writer = response.getWriter();
            writer.println(ResponseInformation.getErrorInformation("请输入验证码！"));
            writer.close();
        }
        if(!loginUserJson.getString("checkCode").equals(systemCode)){
            PrintWriter writer = response.getWriter();
            writer.println(ResponseInformation.getErrorInformation("验证码输入错误！"));
            writer.close();
        }

		UserService userService = new UserService();
		String loginID = loginUserJson.getString("loginID");
		String pwd = loginUserJson.getString("password");
		User loginedUser = userService.CheckUser(loginID, pwd);
		if (loginedUser != null){
			request.getSession().setAttribute(LOGINED_USER_SESSION_ATTR, loginedUser);
			PrintWriter writer = response.getWriter();
			writer.println(ResponseInformation.getSuccessInformation());
			writer.close();
		}else {
			PrintWriter writer = response.getWriter();
			writer.println(ResponseInformation.getErrorInformation("用户名或密码错误！"));
			writer.close();
		}
	}
}
