package com.im_plus.log;

import org.apache.log4j.Logger;

public class MyLog {

    private static final boolean on_off = true;

    /**
     * 输出日志，可将此方法改进，将日志信息保存至数据库中，而不用改动其他代码
     * 在此是将日志信息保存到 D:\\logs\log.log 中
     * @param myClass
     * @param logInfo
     */
    public static void log(Class myClass, String logInfo){
        if(on_off){
//            System.out.println(myClass.getName() + ": " + logInfo);
            Logger logger = Logger.getLogger(myClass);
            logger.info(logInfo);
        }
    }
}
