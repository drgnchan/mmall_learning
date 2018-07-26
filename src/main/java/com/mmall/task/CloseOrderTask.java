package com.mmall.task;

import com.mmall.common.Const;
import com.mmall.common.RedissonManager;
import com.mmall.service.IOrderService;
import com.mmall.util.PropertiesUtil;
import com.mmall.util.RedisShardedPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class CloseOrderTask {
    @Autowired
    private IOrderService iOrderService;
    @Autowired
    private RedissonManager redissonManager;

    @PreDestroy
    private void delLock() {
        RedisShardedPoolUtil.del(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
    }

    //    @Scheduled(cron = "0 */1 * * * ?") //每1分钟（每个1分钟的整数倍）
    public void closeOrderTaskV1() {
        int hour = Integer.parseInt(PropertiesUtil.getProperty("close.order.task.time.hour", "2"));
        log.info("关闭订单，定时任务启动");
        iOrderService.closeOrder(hour);
        log.info("关闭订单的定时任务结束");
    }

    public void closeOrderTaskV2() {
        log.info("关闭订单，定时任务启动");
        long lockTimeout = Long.parseLong(PropertiesUtil.getProperty("lock.timeout"));
        Long setnxResult = RedisShardedPoolUtil.setnx(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK, String.valueOf(System.currentTimeMillis() + lockTimeout));
        if (setnxResult != null && setnxResult.intValue() == 1) {
            //返回值是1，代表设置成功，获取锁
            closeOrder(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);

        } else {
            log.info("没有获取分布式锁：{}", Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
        }
        log.info("关闭订单的定时任务结束");

    }

    /**
     * V2版本的时候，有可能出现这种情况：
     * 在锁设置失效时间前出现意外，tomcat终止了，那这个锁就会永久存在。
     * 进化到V3版本，对出现异常状况的锁进一步条件判断，如果当前时间大于异常锁的value值，就进行重置，重新获取锁
     */
    @Scheduled(cron = "0 */1 * * * ?") //每1分钟（每个1分钟的整数倍）
    public void closeOrderTaskV3() {
        log.info("关闭订单，定时任务启动");
        long lockTimeout = Long.parseLong(PropertiesUtil.getProperty("lock.timeout"));
        Long setnxResult = RedisShardedPoolUtil.setnx(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK, String.valueOf(System.currentTimeMillis() + lockTimeout));
        if (setnxResult != null && setnxResult.intValue() == 1) {
            //返回值是1，代表设置成功，获取锁
            closeOrder(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);

        } else {
            String lockValueStr = RedisShardedPoolUtil.get(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
            if (lockValueStr != null && System.currentTimeMillis() > Long.parseLong(lockValueStr)) {
                String getSetResult = RedisShardedPoolUtil.getset(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK, String.valueOf(System.currentTimeMillis() + lockTimeout));
                if (getSetResult == null || (getSetResult != null && StringUtils.equals(lockValueStr, getSetResult))) {
                    //真正获取到锁
                    closeOrder(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
                } else {
                    log.info("没有获取到分布式锁：{}", Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
                }
            } else {
                log.info("没有获取到分布式锁：{}", Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
            }
        }

        log.info("关闭订单的定时任务结束");

    }

//    @Scheduled(cron = "0 */1 * * * ?") //每1分钟（每个1分钟的整数倍）
    public void closeOrderTaskV4() {
        RLock rLock = redissonManager.getRedisson().getLock(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
        boolean getLock = false;
        try {
            if (getLock = rLock.tryLock(0, 5, TimeUnit.SECONDS)) {
                log.info("redisson获取到分布式锁：{}，ThreaName：{}", Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK, Thread.currentThread());
                int hour = Integer.parseInt(PropertiesUtil.getProperty("close.order.task.time.hour", "2"));
                iOrderService.closeOrder(hour);
            } else {
                log.info("redisson没有获取到分布式锁：{}，ThreaName：{}", Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK, Thread.currentThread());
            }
        } catch (InterruptedException e) {
            log.error("redisson获取分布式锁异常", e);
        } finally {
            if (!getLock) {
                return;
            }
            rLock.unlock();
            log.info("redisson分布式锁释放锁");
        }
    }

    private void closeOrder(String lockName) {
        RedisShardedPoolUtil.expire(lockName, 5);//有效期5秒，防止死锁
        log.info("获取{}，ThreadName:{}", Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK, Thread.currentThread());
        int hour = Integer.parseInt(PropertiesUtil.getProperty("close.order.task.time.hour", "2"));
        iOrderService.closeOrder(hour);
        RedisShardedPoolUtil.del(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
        log.info("释放:{},ThreadName:{}", Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK, Thread.currentThread());
        log.info("=============");
    }
}
