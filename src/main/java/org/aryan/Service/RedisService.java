package org.aryan.Service;

import io.quarkus.redis.client.RedisClient;
import io.quarkus.runtime.StartupEvent;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.List;

@Singleton
public class RedisService {

    @Inject
    RedisClient redisClient;

    @PostConstruct
    public void init(){
        System.out.println("Checking connection to Redis");
        if(!isConnected()){
            System.out.println("Redis is not connected");
        } else {
            System.out.println("Redis is connected");
        }
    }

    public boolean isConnected(){
        var response = redisClient.ping(List.of("Pinging Redis"));
        return response != null && response.toString().contains("Pinging Redis");
    }

    public void setHashKey(String key, String field, String value) {
        redisClient.hset(List.of(key, field, value));
    }

    public boolean checkHashKey(String key, String field) {
        return redisClient.hexists(key, field).toBoolean();
    }

    public Long getHashKey(String key, String field) {
        return redisClient.hget(key, field).toLong();
    }
}
