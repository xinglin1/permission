package com.xmcc.service;

import com.xmcc.beans.CacheKeyPrefix;
import com.xmcc.utils.RedisPool;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.ShardedJedis;


/**
 * @author 张兴林
 * @date 2019-03-30 14:38
 */
@Service
@Log4j
public class SysCacheService {
    @Autowired
    private RedisPool redisPool;

    //写缓存
    public void saveCache(String toSaveValue, int timeoutSeconds, String key, CacheKeyPrefix prefix){
        if (StringUtils.isBlank(toSaveValue)){
            return;
        }
        ShardedJedis shardedJedis = null;
        try {
            String cacheKey = getCacheKey(key, prefix);
            shardedJedis = redisPool.instance();
            shardedJedis.setex(cacheKey,timeoutSeconds,toSaveValue);
        } catch (Exception e){
            log.error("save cache error prefix{} ,key{}");
        } finally {
            redisPool.close(shardedJedis);
        }
    }

    //读缓存
    public String getInfoFromCache(String key, CacheKeyPrefix prefix){
        String cacheKey = getCacheKey(key, prefix);
        String value = null;
        ShardedJedis shardedJedis = null;
        try {
            shardedJedis = redisPool.instance();
            value = shardedJedis.get(cacheKey);
        } catch (Exception e){
            log.error("save cache error prefix{} ,key{}");
        } finally {
            redisPool.close(shardedJedis);
        }

        return value;
    }

    private String getCacheKey(String key, CacheKeyPrefix prefix){
        if (key != null){
            return prefix+"_"+key;
        }
        return "";
    }
}
