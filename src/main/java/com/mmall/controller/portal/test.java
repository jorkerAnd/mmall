package com.mmall.controller.portal;

import com.mmall.util.RedisPoolUtil;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.JedisPool;

@Slf4j
public class test {
    public static void main(String[] args) {
        String result = RedisPoolUtil.get("2D042DE08088A09058071B08544FF3A9");
        log.info(result);
    }


}
