package cn.bluebubbles.store.util;

import cn.bluebubbles.store.common.RedisPool;
import redis.clients.jedis.Jedis;

/**
 * @author yibo
 * @date 2019-01-19 19:24
 * @description
 */
public class RedisPoolUtil {

    /**
     * 设置key的超时时间,单位为秒
     * @param key 键
     * @param timeout 超时时间(秒)
     * @return
     */
    public static Long expire(String key, int timeout) {
        Long result = null;
        try (Jedis jedis = RedisPool.getJedis()) {
            result = jedis.expire(key, timeout);
        }
        return result;
    }

    /**
     * 设置key,value并设置过期时间,单位为秒
     * @param key 键
     * @param value 值
     * @param timeout 过期时间(秒)
     * @return
     */
    public static String setex(String key, String value, int timeout) {
        String result = null;
        try (Jedis jedis = RedisPool.getJedis()) {
            result = jedis.setex(key, timeout, value);
        }
        return result;
    }

    /**
     * 设置key,value
     * @param key 键
     * @param value 值
     * @return
     */
    public static String set(String key, String value) {
        String result = null;
        try (Jedis jedis = RedisPool.getJedis()) {
            result = jedis.set(key, value);
        }
        return result;
    }

    /**
     * 获取key对应的value
     * @param key 键
     * @return
     */
    public static String get(String key) {
        String result = null;
        try (Jedis jedis = RedisPool.getJedis()) {
            result = jedis.get(key);
        }
        return result;
    }

    /**
     * 删除键值对
     * @param key 键
     * @return
     */
    public static Long del(String key) {
        Long result = null;
        try (Jedis jedis = RedisPool.getJedis()) {
            result = jedis.del(key);
        }
        return result;
    }
}
