package org.aryan.Service.Utils;

import io.quarkus.redis.client.RedisClient;
import io.smallrye.common.annotation.Identifier;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.aryan.Service.KafkaServiceConsumer;
import org.aryan.Service.RedisService;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@ApplicationScoped
public class CustomRebalanceListener implements ConsumerRebalanceListener {

    RedisService redisService;

    private KafkaConsumer<String, String> consumer;

    @Inject
    KafkaServiceConsumer kafkaConsumer;

    public CustomRebalanceListener() {
    }

    public CustomRebalanceListener( KafkaConsumer<String, String> consumer, RedisService redisService) {
        this.consumer = consumer;
        this.redisService = redisService;
    }

    @Override
    public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
        System.out.println("Partitions assigned: " + partitions);
        for (TopicPartition partition : partitions) {
            String topic = partition.topic();
            String partitionkey=String.valueOf(partition.partition());
            if(redisService.checkHashKey(topic,partitionkey)){
                Long offset = redisService.getHashKey(topic, partitionkey);
                System.out.println("Partition assigned: " + partition + " Offset: " + offset);
                if(offset!=null){
                    System.out.println("Seeking to offset " + offset + " for partition " + partition);
                    consumer.seek(partition, offset+1);
                }
            } else{
                System.out.println("No offset found for partition " + partition + " Seeking to beginning");
                consumer.seekToBeginning(Collections.singletonList(partition));
            }
        }
    }

    @Override
    public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
        System.out.println("Partitions revoked: " + partitions);
    }
}
