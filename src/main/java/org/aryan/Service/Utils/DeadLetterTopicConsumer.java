package org.aryan.Service.Utils;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.aryan.Exception.BusinessException;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ApplicationScoped
public class DeadLetterTopicConsumer {

    public String TOPIC="dead-letter-topic";

    @ConfigProperty(name = "kafka.bootstrap.servers")
    public String kafkaBootstrapServers;

    private KafkaConsumer<String, String> consumer;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public void configureDead() {
        try {
            Properties props = new Properties();
            props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
            props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            props.put(ConsumerConfig.GROUP_ID_CONFIG,"dead-letter-group");
            this.consumer = new KafkaConsumer<>(props);
            this.consumer.subscribe(Collections.singletonList(TOPIC));
        } catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void dead() {
        executorService.submit(() -> {
            configureDead();
            System.out.println("Dead letter queue configured");
            try{
                while(true){
                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                    for (ConsumerRecord<String, String> record : records) {
                        try{
                            System.out.println("Dead letter reason "+record.value());
                        } catch (Exception e) {
                            throw new BusinessException(e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Error in consuming dead letter message: " + e.getMessage());
                throw new BusinessException(e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
            } finally {
                consumer.close();
            }
        });
    }
}