package cn.bluebubbles.store.common;

import cn.bluebubbles.store.util.PropertiesUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author yibo
 * @date 2019-01-19 18:10
 * @description Redis连接池
 */
public class RedisPool {

    /**
     * Jedis连接池
     */
    private static JedisPool pool;

    /**
     * 和Redis服务器的最大连接数
     */
    private static Integer maxTotal = Integer.parseInt(PropertiesUtil.getProperty("redis.max.total", "20"));

    /**
     * JedisPool中的最多空闲连接
     */
    private static Integer maxIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.max.idle", "10"));

    /**
     * JedisPool中最少空闲连接数
     */
    private static Integer minIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.min.idle", "2"));

    /**
     * 在JedisPool中取Jedis实例的时测试连接是否可用
     */
    private static Boolean testOnBorrow = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.borrow", "true"));

    /**
     * 在向JedisPool返还Jedis实例时测试连接是否可用
     */
    private static Boolean testOnReturn = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.return", "true"));

    /**
     * Redis服务器的IP
     */
    private static String redisIp = PropertiesUtil.getProperty("redis.ip");

    /**
     * Redis服务器的端口号
     */
    private static Integer redisPort = Integer.parseInt(PropertiesUtil.getProperty("redis.port", "6379"));

    private static void initPool() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);
        config.setTestOnBorrow(testOnBorrow);
        config.setTestOnReturn(testOnReturn);
        pool = new JedisPool(config, redisIp, redisPort, 1000 * 2);
    }

    static {
        initPool();
    }

    /**
     * 获取Jedis连接
     * @return
     */
    public static Jedis getJedis() {
        return pool.getResource();
    }
}
