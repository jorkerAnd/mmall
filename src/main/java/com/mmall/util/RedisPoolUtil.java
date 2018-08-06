package com.mmall.util;

import com.mmall.common.RedisPool;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

/**
 * @author Jorker
 * @date 2018/8/4 8:57
 */
@Slf4j
public class RedisPoolUtil {
    /**
     * 根据返回结果是否为null来进行判断是否出现了异常
     *
     * @param key
     * @param value
     * @return
     */
    public static String set(String key, String value) {
        Jedis jedis = null;
        String result = null;
        try {
            jedis = RedisPool.getJedis();
            result = jedis.set(key, value);
        } catch (Exception e) {
            /**
             *
             */
            log.error("set key:{} value:{}", key, value, e);
            RedisPool.returnBrokenResource(jedis);
            return result;
        }
        RedisPool.returnResource(jedis);
        return result;
    }

    public static String get(String key) {
        Jedis jedis = null;
        String result = null;
        try {
            jedis = RedisPool.getJedis();
            result = jedis.get(key);
        } catch (Exception e) {
            /**
             * 能否只系欸
             */
            log.error(" value:{}", key, e);
            RedisPool.returnBrokenResource(jedis);
            return result;
        }
        RedisPool.returnResource(jedis);
        return result;
    }

    /**
     * 设置时间,exTime存的单位为s
     *
     * @param key
     * @param value
     * @param exTime
     * @return
     */
    public static String setEx(String key, String value, int exTime) {
        Jedis jedis = null;
        String result = null;
        try {
            jedis = RedisPool.getJedis();
            result = jedis.setex(key, exTime, value);
        } catch (Exception e) {
            /**
             *
             */
            log.error("setEx key:{} value:{}", key, value, e);
            RedisPool.returnBrokenResource(jedis);
            return result;
        }
        RedisPool.returnResource(jedis);
        return result;
    }

    /**
     * 重新设置key的有效期.时间为秒，如果想设置成20分钟，则为20*60
     *
     * @param key
     * @param exTime
     * @return
     */
    public static Long setEx(String key, int exTime) {
        Jedis jedis = null;
        Long result = null;
        try {
            jedis = RedisPool.getJedis();
            result = jedis.expire(key, exTime);//返回值为1则成功 0则代表失败
        } catch (Exception e) {
            /**
             *
             */
            log.error("setEx key:{}", key, e);
            RedisPool.returnBrokenResource(jedis);
            return result;
        }
        RedisPool.returnResource(jedis);
        return result;
    }


    /**
     * 删除对应的key,value
     *
     * @param key
     * @return
     */
    public static Long del(String key) {
        Jedis jedis = null;
        Long result = null;
        try {
            jedis = RedisPool.getJedis();
            result = jedis.del(key);//返回值为1则成功 0则代表失败
        } catch (Exception e) {
            log.error("setEx key:{}", key, e);
            RedisPool.returnBrokenResource(jedis);
            return result;
        }
        RedisPool.returnResource(jedis);
        return result;
    }


    public static void main(String[] args) {
        String result = RedisPoolUtil.set("sh", "sh");
        String value = RedisPoolUtil.get("j");
        System.out.print(result + "   " + value);
      //  Long long1 = RedisPoolUtil.setEx("sh", 20 * 60);
       //String long2 = RedisPoolUtil.setEx("mybest", "yorbest", 20 * 60);
       Long long3 = RedisPoolUtil.del("sh");


    }

}
