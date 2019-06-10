package com.mmall.common;

import com.mmall.util.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by geely
 */
@Component
@Slf4j
public class RedissonManager {

    @Value("${redis1.ip}")
    private String redis1Ip;
    @Value("${redis1.port}")
    private String redis1Port;

    private Config config = new Config();

    private Redisson redisson = null;

    public Redisson getRedisson() {
        return redisson;
    }


    @PostConstruct
    private void init(){
        try {
            config.useSingleServer().setAddress(new StringBuilder().append(redis1Ip).append(":").append(redis1Port).toString());

            redisson = (Redisson) Redisson.create(config);


            log.info("初始化Redisson结束");
        } catch (Exception e) {
            log.error("redisson init error",e);
        }
    }



}
