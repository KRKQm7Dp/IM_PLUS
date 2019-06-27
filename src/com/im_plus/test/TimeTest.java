package com.im_plus.test;

import com.im_plus.db.DataBase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeTest {
    public static void main(String[] args){
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        String time = df.format(new Date());
        System.out.println(time);
    }
}
