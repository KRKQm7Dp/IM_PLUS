package com.im_plus.servlet;

import com.im_plus.log.MyLog;
import com.im_plus.pojo.Message;
import com.im_plus.service.MsgService;
import com.im_plus.utils.ResponseInformation;
import com.im_plus.utils.Utils;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;


/**
 * 此 servlet 用于读取用户历史记录
 * 请求消息格式：
 * req{
 *      from："10000",
 *      to: "10001",
 *      timeStamp:"当前时间",  // 表示获取当前时间之前的 msgNum 条消息
 *      msgNum: 10
 *    }
 */
@WebServlet("/loadHisMsg")
public class LoadHisMsgServlet extends JsonServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=utf-8");
        response.setCharacterEncoding("UTF-8");

        String requestStr = ReadFromStream(request);
        JSONObject reqJson = new JSONObject(requestStr);

        MyLog.log(this.getClass(), "用户：" + reqJson.getString("from") + " 请求查询 "
                + reqJson.getString("timeStamp") + "之前的 "+ reqJson.getInt("msgNum") +" 条历史消息");

        MsgService msgService = new MsgService();
        LinkedList<Message> msgStack = msgService.queryMsgByPage(
                reqJson.getString("from"),
                reqJson.getString("to"),
                Utils.strToSqlDate(reqJson.getString("timeStamp"), "yyyy-MM-dd HH:mm:ss"),
                reqJson.getInt("msgNum")
        );
        JSONArray msgJsonArray = new JSONArray();
        if (msgStack != null){
            while(!msgStack.isEmpty()){
                msgJsonArray.put(msgStack.pop().toJson());
            }
        }

        PrintWriter writer = response.getWriter();
        writer.println(msgJsonArray.toString());
        writer.close();

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
