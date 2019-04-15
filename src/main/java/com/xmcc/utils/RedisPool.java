package com.xmcc.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;


/**
 * @author 张兴林
 * @date 2019-03-30 14:34
 */
@Service
public class RedisPool {
    @Autowired
    private ShardedJedisPool shardedJedisPool;
    //从连接池获取redis对象
    public ShardedJedis instance(){
        return shardedJedisPool.getResource();
    }

    public void close(ShardedJedis shardedJedis){
        if (shardedJedis != null){
            shardedJedis.close();
        }
    }
}
