package com.zhao.common.utils;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: GuavaCacheUtil
 * @Author: zhaolianqi
 * @Date: 2022/11/3 10:56
 * @Version: v1.0
 */
public class GuavaCacheUtil {

    private Logger logger = LoggerFactory.getLogger(GuavaCacheUtil.class);
    private static GuavaCacheUtil instance = null;
    private Map<String, Cache<String, Object>> cacheMap = new HashMap<>(16);

    private GuavaCacheUtil(){

    }

    public static GuavaCacheUtil getInstance(){
        if (instance == null){
            synchronized (GuavaCacheUtil.class){
                if (instance == null){
                    instance = new GuavaCacheUtil();
                }
            }
        }
        return instance;
    }


    /**
     * 缓存设置
     * 缓存项最大数量：10000
     * 缓存有效时间（天）：1
     */
    private Cache<String, Object> loadCache(Long seconds) {
        CacheBuilder<Object, Object> builder = CacheBuilder.newBuilder()
                .concurrencyLevel(Runtime.getRuntime().availableProcessors())
                // 设置初始容量
                .initialCapacity(1000)
                // 缓存池大小
                .maximumSize(10000);
                // 设置 设定时间 后 刷新缓存
//                .refreshAfterWrite(1, TimeUnit.DAYS);

        if (seconds != null){
            builder.expireAfterWrite(seconds, TimeUnit.SECONDS);
        }
        return builder.build();
    }

    /**
     * 设置缓存值
     * 若已有该key值，则会先移除(会触发removalListener移除监听器)，再添加
     * @param key key
     * @param value value
     * @param timeLv 缓存时长，单位：秒，为空则永不过期
     */
    public void put(String key, Object value, Long timeLv) {
        if (StringUtils.isBlank(key) || value == null) {
            return;
        }
        try {
            Cache<String, Object> cache = cacheMap.get(timeLv+"");
            if (cache == null){
                synchronized (GuavaCacheUtil.class){
                    cache = cacheMap.get(timeLv+"");
                    if (cache == null){
                        cache = loadCache(timeLv);
                        cacheMap.put(timeLv+"", cache);
                    }
                }
            }
            cache.put(key, value);
        } catch (Exception e) {
            logger.error("设置缓存值出错", e);
        }
    }

    /**
     * 设置缓存，默认时长不限
     */
    public void put(String key, Object value) {
        put(key, value, null);
    }

    /**
     * 获取缓存值
     */
    public Object get(String key, Long timeLv) {
        try {
            if (timeLv != null){
                Cache<String, Object> cache = cacheMap.get(timeLv+"");
                if (cache == null)
                    return null;
                return cache.getIfPresent(key);
            } else {
                Cache<String, Object> cache;
                for (String s : cacheMap.keySet()) {
                    cache = cacheMap.get(s);
                    Object res = cache.getIfPresent(key);
                    if (res != null)
                        return res;
                }
                return null;
            }
        } catch (Exception e) {
            logger.error("获取缓存值出错", e);
            return null;
        }
    }

    public Object get(String key) {
        return get(key, null);
    }

}
