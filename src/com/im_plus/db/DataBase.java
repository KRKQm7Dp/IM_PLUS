package com.im_plus.db;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DataBase {
    private static final String filePath = "mySql.properties";
    private String url;
    private String dbname;
    private String driver;
    private String username;
    private String psw;
    java.sql.Connection conn = null;

    /**
     * 构造函数，初始化此类时从配置文件中加载数据库相关信息并保存到对应数据域中
     */
    public DataBase() {
        super();
        Properties pps = new Properties();
        try {
            InputStream is = this.getClass().getResourceAsStream(filePath);
            pps.load(is);
            this.url = pps.getProperty("url").trim();
            this.dbname = pps.getProperty("database").trim();
            this.driver = pps.getProperty("driver").trim();
            this.username = pps.getProperty("username").trim();
            this.psw = pps.getProperty("password").trim();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("数据库 properties 文件未找到！");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 连接数据库
     * @return 成功返回 true
     */
    public boolean openConnection()	{
        try {
            Class.forName(this.driver);
            conn = DriverManager.getConnection(this.url+this.dbname, this.username, this.psw);
            if (conn==null)
                return false;
            return true;
        } catch (Exception e) {
            System.err.println(e);
            return false;
        }
    }

    /**
     * 关闭数据库连接
     * @return 成功返回 true
     */
    public boolean closeConnection() {
        if (conn!=null){
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    public Connection getConnection() {
        return conn;
    }
}
