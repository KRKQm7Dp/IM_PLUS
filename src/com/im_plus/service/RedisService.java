package com.im_plus.service;

import com.im_plus.db.RedisUtil;
import redis.clients.jedis.Jedis;

import java.util.List;

public class RedisService {
    private Jedis jedis;

    public RedisService(){
        jedis = RedisUtil.getJedis();
    }

    public void close(){
        RedisUtil.returnResource(jedis);
    }

    public void insertOfflineMsg(String toUserLoginID, String msg){
        jedis.rpush(toUserLoginID, msg);   // 后进
    }

    public List<String> getOfflineMsg(String loginUserID){
        return jedis.lrange(loginUserID,0, -1);  // 先出
    }

    public boolean removeOffLineMsg(String loginUserID){
        if(jedis.del(loginUserID) > 0) {
            return true;
        }
        return false;
    }


}
