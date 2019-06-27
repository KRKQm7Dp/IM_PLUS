package com.im_plus.checkcode;

import java.util.Random;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/createCheckcode")
public class CreateCheckcode extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");

		response.setDateHeader("Expires", -1);
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Pragma", "no-cache");

		response.setHeader("Content-Type", "image/jpeg");
		
		BufferedImage image = new BufferedImage(80, 30, BufferedImage.TYPE_INT_RGB);
		
		Graphics g = image.getGraphics();
		
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, 80, 30);
		
		g.setColor(Color.BLUE);
		g.setFont(new Font(null, Font.BOLD, 20));

		String num = makeNum();
		HttpSession session = request.getSession(); 
		session.setAttribute("checkcode", num);
		g.drawString(num, 10, 20);

		ImageIO.write(image, "jpg", response.getOutputStream());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
	
	private String makeNum() {
		Random rand = new Random();
		String num = rand.nextInt(9999) + "";
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < 4 - num.length(); i++) {
			sb.append("0");
		}
		num = sb.toString() + num;
		return num;
		
	}

}