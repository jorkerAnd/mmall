package com.mmall.task;
//LAMBA表达式
//Runnable runnable=new Runnable(
//        ()->System.out.print("jorker is ook")  );
//
//
//

import com.mmall.common.Const;
import com.mmall.common.RedissonManager;
import com.mmall.service.IOrderService;
import com.mmall.util.PropertiesUtil;
import com.mmall.util.RedisShardedPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.annotation.Aspect;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

/**
 * 定时关单，当长时间不下订单的时候，就会将订单关闭，将库存放回到数据库当中
 * 设置多重锁，防止死锁的出现
 * 多进程来竞争分布式锁
 */
@Component
@Slf4j
public class CloseOrderTask {


    @Autowired
    private RedissonManager redissonManager;

    public CloseOrderTask() {
        log.info("Task is starting====================");
    }

    @Autowired
    private IOrderService iOrderService;

    /**
     * 以当前之间为准
     */
    // @Scheduled(cron = "0 */1 * * * ?")
    public void closeOrderTaskV1() {
        log.info("关闭订单定时任务启动");
        int hour = Integer.parseInt(PropertiesUtil.getProperty("close.order.task.time.hour"));
        iOrderService.closeOrder(hour);
        log.info("关闭订单定时任务结束");
    }

    /**
     * 下面的分布式锁会出现问题，如果走到断电处，但是要强行关闭tomcat1的服务器，那么就会
     * 使的锁成为了死锁，tomcat2无法进行了锁的获取，无法释放进行锁的释放
     * setnx是原子性的操作，关闭系统有两个方法，一个是直接kill，一个是采用tonmcat的shutdown方法
     */
    //在tomcat关闭的时候进行调用，但是用kill进程的方法没有办法实现，但是如果锁很多的时候，关闭tomcat会消耗很长的时间
    @PreDestroy
    public void destoryLock() {
        RedisShardedPoolUtil.del(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);//进行锁的释放

    }


//    @Scheduled(cron = "0 */1 * * * ?")
//    public void closeOrderTaskV12() {
//        log.info("关闭订单定时任务启动");
//        Long lockTime=Long.parseLong(PropertiesUtil.getProperty("lock.time"));
//        Long senxResult= RedisShardedPoolUtil.setNx(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK,String.valueOf(System.currentTimeMillis()+lockTime));
//        if(senxResult!=null&&senxResult.intValue()==1){
//            //代表返回值为1的时候，代表设置成功，获取锁
//        closeOrder(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
//
//        }else{
//
//            log.info("没有获得分布式锁：{}",Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
//        }
//        log.info("关闭订单定时任务结束");
//    }

    @Scheduled(cron = "0 */1 * * * ?")
    public void closeOrderTaskV12() {
        log.info("关闭订单定时任务启动");
        Long lockTime = Long.parseLong(PropertiesUtil.getProperty("lock.time"));
        Long senxResult = RedisShardedPoolUtil.setNx(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK, String.valueOf(System.currentTimeMillis() + lockTime));
        if (senxResult != null && senxResult.intValue() == 1) {
            //代表返回值为1的时候，代表设置成功，获取锁
            closeOrder(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);

        } else {

            log.info("没有获得分布式锁：{}", Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
        }
        log.info("关闭订单定时任务结束");
    }

    private void closeOrder(String lockName) {
        RedisShardedPoolUtil.expire(lockName, 5);//时间根据要求进行设定，不断地进行调式，保证任务能够 执行完毕
        log.info("获取{}，ThreadName:{}", Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK, Thread.currentThread().getName());
        int hour = Integer.parseInt(PropertiesUtil.getProperty("close.order.task.time.hour"));
        iOrderService.closeOrder(hour);//执行完毕业务逻辑之后要进行锁的删除
        RedisShardedPoolUtil.del(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);//进行锁的释放
        log.info("释放{}，ThreadName:{}", Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK, Thread.currentThread().getName());
        log.info("======================");
    }


    /**
     * 利用时间戳进行死锁时间的判断
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void closeOrderTaskV3() {
        log.info("关闭订单定时任务启动");
        Long lockTime = Long.parseLong(PropertiesUtil.getProperty("lock.time"));
        Long senxResult = RedisShardedPoolUtil.setNx(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK, String.valueOf(System.currentTimeMillis() + lockTime));
        if (senxResult != null && senxResult.intValue() == 1) {
            closeOrder(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
        } else {
            //为获取到锁继续判断时间戳，判断是否可以重置并获取到锁
            //PS：时间戳一定要设置的小一些，否则会浪费一些时间，导致资源的浪费
            String lockValue = RedisShardedPoolUtil.get(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
            if (lockValue != null && System.currentTimeMillis() > Long.parseLong((lockValue))) {
                //进入到该方法里面就是表名可以进行锁的获取，getset方法是具有原子性的

                //如果没有，则getset返回一个null类型
                String getSetResult = RedisShardedPoolUtil.getset(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK, String.valueOf(System.currentTimeMillis() + lockTime));
                if (getSetResult == null || (getSetResult != null && StringUtils.equals(getSetResult, lockValue))) {
                    //真正获得锁
                    closeOrder(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);

                } else {
                    log.info("没有获得分布式锁：{}", Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
                }

            } else {
                log.info("没有获得分布式锁：{}", Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
            }
        }
        log.info("关闭订单定时任务结束");
    }

    //原生实现再去使用框架
    @Scheduled(cron = "0 */1 * * * ?")
    public void closeOrderTaskV4() {
        RLock lock = redissonManager.getRedisson().getLock(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
        //第一个参数是最多等待多长时间，第二个参数表示多久会释放锁
         boolean getLock=false;
        try {

            if(getLock = lock.tryLock(2, 5, TimeUnit.SECONDS)){
                log.info("Redisson获取到分布式锁：{}，ThreadName:{}",Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK,Thread.currentThread().getName());
                int hour = Integer.parseInt(PropertiesUtil.getProperty("close.order.task.time.hour"));
                iOrderService.closeOrder(hour);
            }else{
                log.info("Redisson没有获取到分布式锁：{}，ThreadName:{}",Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK,Thread.currentThread().getName());
            }

        } catch (InterruptedException e) {
            log.warn("获取分布式锁异常");
        }finally {
            if(!getLock)
            return;
            lock.unlock();
            log.info("Redisson分布式锁进行释放");
        }


    }


}
