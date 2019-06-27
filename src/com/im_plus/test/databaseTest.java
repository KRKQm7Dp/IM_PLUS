package com.im_plus.test;

import com.im_plus.db.DataBase;
import com.im_plus.service.UserService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class databaseTest {
    public static void main(String[] args){
        DataBase db = new DataBase();
        if(db.openConnection()){
            UserService userService = new UserService();
        }

    }
}
