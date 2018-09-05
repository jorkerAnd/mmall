package com.mmall.common;

import com.mmall.util.PropertiesUtil;
import redis.clients.jedis.*;

import redis.clients.util.Hashing;
import redis.clients.util.Sharded;

import java.util.ArrayList;
import java.util.List;

public class RedisSharedPool {

    private static ShardedJedisPool pool;//sharedPool的连接池
    private static Integer maxTotal = Integer.parseInt(PropertiesUtil.getProperty("redis.max.total", "20"));//最大连接数
    private static Integer maxIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.max.idle", "10"));//在pool当中最大的idle状态的jedis实例的个数
    private static Integer minIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.min.idle", "2"));//同上最小的实例个数
    private static Boolean testOnBorrow = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.borrow", "true"));//再去拿jedis实例的时候，如果赋值为true，那么会进行验证，验证得到的一定是可以用jedis实例
    private static Boolean testOnReturn = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.return", "true"));//在return一个jedis实例的时候，是否进行验证操作，如果赋值为true.则放回的pool的jedis实例肯定是可以用的
    private static Integer redis1Port = Integer.parseInt(PropertiesUtil.getProperty("redis1.port"));
    private static String redis1Ip = PropertiesUtil.getProperty("redis1.ip");
    private static Integer redis2Port = Integer.parseInt(PropertiesUtil.getProperty("redis2.port"));
    private static String redis2Ip = PropertiesUtil.getProperty("redis2.ip");
    private static String redis2Password = PropertiesUtil.getProperty("redis2.paaword");

    static {

        initPool();


    }

    //声明为私有，防止外部方法再进行一次调用
    private static void initPool() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(maxTotal);
        jedisPoolConfig.setMaxIdle(maxIdle);
        jedisPoolConfig.setMinIdle(minIdle);
        jedisPoolConfig.setTestOnBorrow(testOnBorrow);
        jedisPoolConfig.setTestOnReturn(testOnReturn);
        //连接耗尽的时候，是否阻塞，false会抛出异常，true.阻塞到超时
        jedisPoolConfig.setBlockWhenExhausted(true);
        JedisShardInfo info1 = new JedisShardInfo(redis1Ip, redis1Port, 2 * 1000);
        JedisShardInfo info2 = new JedisShardInfo(redis2Ip, redis2Port, 2 * 1000);
        info2.setPassword(redis2Password);
        List<JedisShardInfo> jedisShardInfos = new ArrayList<>(2);
        jedisShardInfos.add(info1);
        jedisShardInfos.add(info2);
        //Hashing.MURMUR_HASH为一致性算法
        pool = new ShardedJedisPool(jedisPoolConfig, jedisShardInfos, Hashing.MURMUR_HASH, Sharded.DEFAULT_KEY_TAG_PATTERN);

    }

    public static ShardedJedis getJedis() {
        return pool.getResource();
    }

    /**
     * 因为为了将不可用的redis实例放回pool当中，所有需要将testOnReturn设置为false。否则不会将错误的redis实例放回，提高效率
     *
     * @param jedis
     */
    public static void returnBrokenResource(ShardedJedis jedis) {
        /**
         * 调用的方法已经加入了null判断，如果为null，不做操作，有的话就放回
         */
        pool.returnBrokenResource(jedis);
    }

    public static void returnResource(ShardedJedis jedis) {
        pool.returnResource(jedis);
    }

    public static void main(String[] args) {
        ShardedJedis jedis = pool.getResource();
        for (int i = 0; i < 10; i++) {
            //里面用了一致性算法
            jedis.set("key:" + i, "value" + i);
        }
        returnResource(jedis);
    }

}
