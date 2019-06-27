package com.im_plus.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 退出登录处理
 */
@WebServlet("/logout")
public class LogoutServlet extends JsonServlet {
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.getSession().removeAttribute(LoginServlet.LOGINED_USER_SESSION_ATTR);
	}
}
