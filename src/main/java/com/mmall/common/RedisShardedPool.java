package com.mmall.common;

import com.mmall.util.PropertiesUtil;
import redis.clients.jedis.*;
import redis.clients.util.Hashing;
import redis.clients.util.Sharded;

import java.util.ArrayList;
import java.util.List;

public class RedisShardedPool {
    private static ShardedJedisPool pool;//jedis连接池
    private static Integer maxTotal = Integer.parseInt(PropertiesUtil.getProperty("redis.max.total", "20"));//最大连接总数
    private static Integer maxIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.max.idle", "10"));//最大idle（空闲状态）的jedis实例个数
    private static Integer minIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.min.idle", "5"));//最小idle实例个数
    private static Boolean testOnBorrow = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.borrow", "true"));//是否要验证borrow的jedis实例可用，如果为true，则jedis实例肯定可用
    private static Boolean testOnReturn = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.return", "true"));//是否要验证return的jedis实例可用，如果为true，则jedis实例肯定可用
    private static String redis1Ip = PropertiesUtil.getProperty("redis1.ip");
    private static Integer redis1Port = Integer.parseInt(PropertiesUtil.getProperty("redis1.port"));
    private static String redis2Ip = PropertiesUtil.getProperty("redis2.ip");
    private static Integer redis2Port = Integer.parseInt(PropertiesUtil.getProperty("redis2.port"));

    private static void initPool() {
        JedisPoolConfig config = new JedisPoolConfig();

        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);

        config.setTestOnBorrow(testOnBorrow);
        config.setTestOnReturn(testOnReturn);

        JedisShardInfo jedisShardInfo1=new JedisShardInfo(redis1Ip,redis1Port,1000*2);
        JedisShardInfo jedisShardInfo2=new JedisShardInfo(redis2Ip,redis2Port,1000*2);
        List<JedisShardInfo> jedisShardInfoList=new ArrayList<JedisShardInfo>(2);
        jedisShardInfoList.add(jedisShardInfo1);
        jedisShardInfoList.add(jedisShardInfo2);
        pool =new ShardedJedisPool(config,jedisShardInfoList,Hashing.MURMUR_HASH,Sharded.DEFAULT_KEY_TAG_PATTERN);

    }

    static {
        initPool();
    }

    public static ShardedJedis getJedis() {
        return pool.getResource();
    }

    public static void returnResource(ShardedJedis jedis) {
        pool.returnResource(jedis);
    }

    public static void returnBrokenResource(ShardedJedis jedis) {
        pool.returnBrokenResource(jedis);
    }

    public static void main(String[] args) {
        ShardedJedis jedis=pool.getResource();
        jedis.set("raymond","handsomeraymond");
        returnResource(jedis);

        pool.destroy();
        System.out.println("program is done");
    }
}
