package com.mmall.task;

import com.mmall.common.Const;
import com.mmall.service.IOrderService;
import com.mmall.util.PropertiesUtil;
import com.mmall.util.RedisShardedPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时关单，当长时间不下订单的时候，就会将订单关闭，将库存放回到数据库当中
 */
@Component
@Slf4j
public class CloseOrderTask {

    public CloseOrderTask(){
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

    @Scheduled(cron = "0 */1 * * * ?")
    public void closeOrderTaskV12() {
        log.info("关闭订单定时任务启动");
        Long lockTime=Long.parseLong(PropertiesUtil.getProperty("lock.time"));
        Long senxResult= RedisShardedPoolUtil.setNx(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK,String.valueOf(System.currentTimeMillis()+lockTime));
        if(senxResult.intValue()==1){
            //代表返回值为1的时候，代表设置成功，获取锁
   closeOrder(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);

        }else{

            log.info("没有获得分布式锁：{}",Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
        }
        log.info("关闭订单定时任务结束");
    }



    private void closeOrder(String lockName){
        RedisShardedPoolUtil.expire(lockName,50);//锁的有效期为50秒
        log.info("获取{}，ThreadName:{}",Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK,Thread.currentThread().getName());
        int hour = Integer.parseInt(PropertiesUtil.getProperty("close.order.task.time.hour"));
        iOrderService.closeOrder(hour);//执行完毕业务逻辑之后要进行锁的删除
        log.info("释放{}，ThreadName:{}",Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK,Thread.currentThread().getName());
        log.info("======================");
    }




}
