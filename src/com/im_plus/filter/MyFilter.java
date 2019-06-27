package com.im_plus.filter;

import com.im_plus.log.MyLog;
import com.im_plus.pojo.User;
import com.im_plus.servlet.LoginServlet;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


/**
 * Servlet Filter implementation class MyFilter
 */
public class MyFilter implements Filter {

	private FilterConfig config = null;

    /**
     * Default constructor. 
     */
    public MyFilter() {
        // TODO Auto-generated constructor stub
    }
    
	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
		this.config = fConfig;
	}

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
		this.config = null;
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		// TODO Auto-generated method stub

		// pass the request along the filter chain
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		String uri = httpServletRequest.getRequestURI();
        MyLog.log(this.getClass(), uri);
		if(uri.startsWith("/login.html") || uri.equals("/")) {
//            MyLog.log(this.getClass(), "Filter: IM_PlUS/login.html");
			chain.doFilter(httpServletRequest, response);
		}
		else if (uri.equals("/login") || uri.equals("/register")){
//		    MyLog.log(this.getClass()," 注册 或 登录");
            chain.doFilter(httpServletRequest, response);
        }
		else if(uri.contains(".css") || uri.contains(".js")
                || uri.contains(".png") || uri.contains(".jpg") || uri.contains("jpeg")){
//            MyLog.log(this.getClass(), "Filter: css js img");
            chain.doFilter(httpServletRequest, response);
        }
        else if(uri.startsWith("/createCheckcode")){
            chain.doFilter(httpServletRequest, response);
        }
		else {
			HttpSession session = httpServletRequest.getSession();
			User user = (User) session.getAttribute(LoginServlet.LOGINED_USER_SESSION_ATTR);
			System.out.println("UserId: "+user.getULoginId());
			if(user != null) {
//                MyLog.log(this.getClass(), "Filter: user not null");
				chain.doFilter(httpServletRequest, response);
			}
			else {
//                MyLog.log(this.getClass(), "Filter: 请先登录");
				request.getRequestDispatcher("/login.html").forward(httpServletRequest, response);
			}
		}
	}

}
