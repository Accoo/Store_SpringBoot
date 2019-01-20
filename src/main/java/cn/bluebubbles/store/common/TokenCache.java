package cn.bluebubbles.store.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @author yibo
 * @date 2019-01-11 17:03
 * @description 缓存用户重置密码时的token(已经不用了,用Redis缓存代替)
 */
public class TokenCache {
    public static final String TOKEN_PREFIX = "FORGET_PASSWORD_TOKEN_";
    private static Logger logger = LoggerFactory.getLogger(TokenCache.class);
    private static LoadingCache<String, String> loadingCache = CacheBuilder.newBuilder().initialCapacity(1000)
            .maximumSize(10000).expireAfterAccess(30, TimeUnit.MINUTES)
            .build(new CacheLoader<String, String>() {
                // 默认的数据加载实现，当调用get取值时，如果key没有对应的值，就返回null字符串
                @Override
                public String load(String s) throws Exception {
                    return "null";
                }
            });
    public static void setKey(String key, String value) {
        loadingCache.put(key, value);
    }

    public static String getKey(String key) {
        String value = null;
        try {
            value = loadingCache.get(key);
            if("null".equals(value)) {
                return null;
            }
            return value;
        } catch (Exception e) {
            logger.error("localCache get error");
        }
        return null;
    }
}
