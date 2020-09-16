package fr.leconsulat.api.redis;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.api.listener.MessageListener;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;

public class RedisManager {
    
    private static RedisManager instance;
    
    private final RedissonClient client;
    
    public RedisManager(String host, int port, String password, String clientName){
        if(instance != null){
            throw new IllegalStateException();
        }
        instance = this;
        Config redisConfig = new Config();
        redisConfig.setThreads(2);
        redisConfig.setNettyThreads(2);
        redisConfig.setCodec(new JsonJacksonCodec());
        redisConfig.useSingleServer()
                .setAddress("redis://" + host + ":" + port)
                .setPassword(password)
                .setClientName(clientName);
        client = Redisson.create(redisConfig);
    }
    
    public <M> int register(String channel, Class<M> c, MessageListener<? extends M> messageListener){
        return RedisManager.getInstance().getRedis().getTopic(channel).addListener(c, messageListener);
    }
    
    public RedissonClient getRedis(){
        return client;
    }
    
    public static RedisManager getInstance(){
        return instance;
    }
}
