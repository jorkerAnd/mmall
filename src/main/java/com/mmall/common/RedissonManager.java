package com.mmall.common;

import com.mmall.util.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.config.Config;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Slf4j
public class RedissonManager {

    private Config config = new Config();

    private Redisson redisson = null;
    private static Integer redis1Port = Integer.parseInt(PropertiesUtil.getProperty("redis1.port"));
    private static String redis1Ip = PropertiesUtil.getProperty("redis1.ip");

    /**
     * redisson现在只支持一个redis
     */
    private static Integer redis2Port = Integer.parseInt(PropertiesUtil.getProperty("redis2.port"));
    private static String redis2Ip = PropertiesUtil.getProperty("redis2.ip");
    private static String redis2Password = PropertiesUtil.getProperty("redis2.paaword");

    @PostConstruct//在调用构造器之后调用该方法
    private void init() {
        //setAdress中传入的参数为host:port的格式
        try {
            config.useSingleServer().setAddress(new StringBuilder().append("redis://").append(redis1Ip).append(":").append(redis1Port).toString());
            redisson = (Redisson) Redisson.create(config);
            log.info("Redisson成功启动");
        } catch (Exception e) {
            e.printStackTrace();

        }

    }


    public Redisson getRedisson() {
        return redisson;
    }

}
