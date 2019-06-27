package com.im_plus.servlet;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;


/**
 * 所有 servlet 继承此类，用于解析前端 ajax 提交的内容
 */
public class JsonServlet extends HttpServlet {
	public String ReadFromStream(HttpServletRequest request) {
		try {
			BufferedReader bufferedReader = request.getReader();
			char []tmpbuf = new char[2 * 1024];
			StringBuffer buffer = new StringBuffer();
			while (bufferedReader.read(tmpbuf) != -1) {
				buffer.append(tmpbuf);
			}
			return buffer.toString();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
