package com.im_plus.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegxTest {
    public static void main(String[] args){
        String imgStr = "C:\\Users\\li128\\Desktop\\IM_PLUS\\out\\artifacts\\IM_PLUS_war_exploded\\users\\10000\\msg_img\\20190509162128.png";
        Pattern pattern = Pattern.compile("(?=users).*");
        Matcher matcher = pattern.matcher(imgStr);
        if(matcher.find()){
            System.out.println(matcher.group());
        }
    }
}
