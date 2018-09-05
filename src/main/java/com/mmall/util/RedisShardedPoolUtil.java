package com.mmall.util;

import com.mmall.common.RedisSharedPool;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ShardedJedis;

/**
 * @author Jorker
 * @date 2018/8/4 8:57
 */
@Slf4j
public class RedisShardedPoolUtil {
    /**
     * 根据返回结果是否为null来进行判断是否出现了异常
     *
     * @param key
     * @param value
     * @return
     */
    public static String set(String key, String value) {
        ShardedJedis jedis = null;
        String result = null;
        try {
            jedis = RedisSharedPool.getJedis();
            result = jedis.set(key, value);
        } catch (Exception e) {
            /**
             *
             */
            log.error("set key:{} value:{}", key, value, e);
            RedisSharedPool.returnBrokenResource(jedis);
            return result;
        }
        RedisSharedPool.returnResource(jedis);
        return result;
    }

    /**
     * redis中的getset是具有原子性的，set的同时返回get的值
     *
     * @param key
     * @param value
     * @return
     */
    public static String getset(String key, String value) {
        ShardedJedis jedis = null;
        String result = null;
        try {
            jedis = RedisSharedPool.getJedis();
            result = jedis.getSet(key, value);
        } catch (Exception e) {
            /**
             *
             */
            log.error("set key:{} value:{}", key, value, e);
            RedisSharedPool.returnBrokenResource(jedis);
            return result;
        }
        RedisSharedPool.returnResource(jedis);
        return result;
    }


    public static String get(String key) {
        ShardedJedis jedis = null;
        String result = null;
        try {
            jedis = RedisSharedPool.getJedis();
            result = jedis.get(key);
        } catch (Exception e) {
            /**
             * 能否只系欸
             */
            log.error(" value:{}", key, e);
            RedisSharedPool.returnBrokenResource(jedis);
            return result;
        }
        RedisSharedPool.returnResource(jedis);
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
        ShardedJedis jedis = null;
        String result = null;
        try {
            jedis = RedisSharedPool.getJedis();
            result = jedis.setex(key, exTime, value);
        } catch (Exception e) {
            /**
             *
             */
            log.error("setEx key:{} value:{}", key, value, e);
            RedisSharedPool.returnBrokenResource(jedis);
            return result;
        }
        RedisSharedPool.returnResource(jedis);
        return result;
    }

    /**
     * 重新设置key的有效期.时间为秒，如果想设置成20分钟，则为20*60
     *
     * @param key
     * @param exTime
     * @return
     */
    public static Long expire(String key, int exTime) {
        ShardedJedis jedis = null;
        Long result = null;
        try {
            jedis = RedisSharedPool.getJedis();
            result = jedis.expire(key, exTime);//返回值为1则成功 0则代表失败
        } catch (Exception e) {
            /**
             *
             */
            log.error("setEx key:{}", key, e);
            RedisSharedPool.returnBrokenResource(jedis);
            return result;
        }
        RedisSharedPool.returnResource(jedis);
        return result;
    }


    /**
     * 删除对应的key,value
     *
     * @param key
     * @return
     */
    public static Long del(String key) {
        ShardedJedis jedis = null;
        Long result = null;
        try {
            jedis = RedisSharedPool.getJedis();
            result = jedis.del(key);//返回值为1则成功 0则代表失败
        } catch (Exception e) {
            log.error("setEx key:{}", key, e);
            RedisSharedPool.returnBrokenResource(jedis);
            return result;
        }
        RedisSharedPool.returnResource(jedis);
        return result;
    }


    public static void main(String[] args) {
        String result = RedisShardedPoolUtil.set("sh", "sh");
        String value = RedisShardedPoolUtil.get("j");
        System.out.print(result + "   " + value);
        //  Long long1 = RedisSharedPoolUtil.setEx("sh", 20 * 60);
        //String long2 = RedisSharedPoolUtil.setEx("mybest", "yorbest", 20 * 60);
        Long long3 = RedisShardedPoolUtil.del("sh");


    }


    public static Long setNx(String key, String keyValue) {
        ShardedJedis jedis = null;
        Long result = null;
        try {
            jedis = RedisSharedPool.getJedis();
            result = jedis.setnx(key, key);//返回值为1则成功 0则代表失败
        } catch (Exception e) {
            /**
             *
             */
            log.error("setEx key:{}", key, e);
            RedisSharedPool.returnBrokenResource(jedis);
            return result;
        }
        RedisSharedPool.returnResource(jedis);
        return result;
    }


}
