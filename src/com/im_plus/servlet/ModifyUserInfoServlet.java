package com.im_plus.servlet;

import com.im_plus.pojo.User;
import com.im_plus.service.UserService;
import com.im_plus.utils.Utils;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import java.io.*;
import java.util.logging.Level;

import static com.sun.xml.internal.ws.spi.db.BindingContextFactory.LOGGER;

@WebServlet("/modifyUserInfo")
@MultipartConfig
public class ModifyUserInfoServlet extends JsonServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter pw = response.getWriter();
        final Part filePart = request.getPart("imgFile");
        final String nickName = request.getParameter("nickName");
        final String password = Utils.getMd5Str(request.getParameter("password"));
        final String email = request.getParameter("email");
        final String sex = request.getParameter("sex");
        final String birthday = request.getParameter("birthday");
        final String sign = request.getParameter("sign");

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute(LoginServlet.LOGINED_USER_SESSION_ATTR);
        if(filePart != null){
            StringBuilder filePath = new StringBuilder();
            String fileFormat = getFileName(filePart).split("\\.")[1];
            String rootPath = session.getServletContext().getRealPath("/");
            filePath.append("users/")
                    .append(user.getULoginId())
                    .append("/head_img/")
                    .append(Utils.getNowDate("yyyyMMddHHmmss"))
                    .append(".")
                    .append(fileFormat);
            File file = new File(rootPath + filePath.toString());
            File fileDir = file.getParentFile();
            if(!fileDir.exists()){
                fileDir.mkdirs();  // 表示如果当前目录不存在就创建，包括所有必须的父目录
            }
            if(!file.exists()){
                file.createNewFile();
            }
            OutputStream out = new FileOutputStream(file);
            InputStream fileContent = filePart.getInputStream();
            int read = 0;
            final byte[] bytes = new byte[1024];
            while((read = fileContent.read(bytes)) != -1){
                out.write(bytes, 0, read);
            }
            if (out != null) {
                out.close();
            }
            if (fileContent != null) {
                fileContent.close();
            }
            user.setUHeadPortrait(filePath.toString());
        }
        if(!"".equals(nickName) && nickName != null){
            user.setUNickName(nickName);
        }
        if(!"".equals(password) && password != null){
            user.setUPassWord(password);
        }
        if(!"".equals(email) && email != null){
            user.setUEmail(email);
        }
        if(!"".equals(sex) && sex != null){
            if("男".equals(sex)){
                user.setUSex((byte)1);
            }
            else if("女".equals(sex)){
                user.setUSex((byte)0);
            }
        }
        if(!"".equals(birthday) && birthday != null){
            user.setUBirthday(birthday);
        }
        if(!"".equals(sign) && sign != null){
            user.setUSignaTure(sign);
        }

        UserService userService = new UserService();
        if (userService.updateUserInfo(user)){
            pw.println("修改用户信息成功");
        }
        else{
            pw.println("修改用户信息失败");
        }
        pw.close();
    }

    private String getFileName(final Part part) {
        final String partHeader = part.getHeader("content-disposition");
        LOGGER.log(Level.INFO, "Part Header = {0}", partHeader);
        for (String content : part.getHeader("content-disposition").split(";")) {
            if (content.trim().startsWith("filename")) {
                return content.substring(
                        content.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
