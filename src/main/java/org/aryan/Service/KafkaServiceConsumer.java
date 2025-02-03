package org.aryan.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.runtime.StartupEvent;
import io.smallrye.reactive.messaging.kafka.IncomingKafkaRecord;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.aryan.Entity.Album;
import org.aryan.Exception.BusinessException;
import org.aryan.Repository.AlbumRepository;
import org.aryan.Service.Utils.CustomRebalanceListener;
import org.aryan.Service.Utils.DeadLetterTopicProducer;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Acknowledgment;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ApplicationScoped
public class KafkaServiceConsumer {
    @Inject
    RedisService redisService;

    @Inject
    AlbumRepository albumRepository;

    @Inject
    ElasticsearchService elasticsearchService;

    @Inject
    DeadLetterTopicProducer deadLetterTopicProducer;

    @ConfigProperty(name = "kafka.topic")
    public String TOPIC;

    @ConfigProperty(name = "kafka.bootstrap.servers")
    public String kafkaBootstrapServers;

    private KafkaConsumer<String, String> consumer;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public void configureConsumer() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers); // Update with actual host
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "albumgroup");
        this.consumer = new KafkaConsumer<>(props);
        this.consumer.subscribe(Collections.singletonList(TOPIC),new CustomRebalanceListener(this.consumer,this.redisService));
        System.out.println("Consumer configured");
    }

    public void consume() {
        executorService.submit(() -> {
        System.out.println("Consuming messages from Kafka");
        configureConsumer();
        try {
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                for (ConsumerRecord<String, String> record : records) {
                    String key = record.key();
                    String value = record.value();
                    ObjectMapper mapper = new ObjectMapper();
                    Album deserialisedmessage = mapper.readValue(value, Album.class);
                    try {
                        TopicPartition partition = new TopicPartition(record.topic(), record.partition());
                        String idMongodb = albumRepository.addAlbum(deserialisedmessage);
                        String idElasticsearch = elasticsearchService.index(deserialisedmessage);
                        updateProcessedOffset(partition, record.offset());
                        System.out.println("Message acknowledged: MongoDB id is" + idMongodb + " and Elasticsearch id is " + idElasticsearch);
                    } catch (Exception e) {
                        deadLetterTopicProducer.produce(e.getMessage());
                        throw new BusinessException("Error in consuming message: " + e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
                    }
                }
                consumer.commitSync();
            }
        } catch (Exception e) {
            System.out.println("Error in consuming message: " + e.getMessage());
        } finally {
            consumer.close();
            System.out.println("Consumed messages");
        }
        });
    }


    public void updateProcessedOffset(TopicPartition topicPartition, long offset) {
        String topic=topicPartition.topic();
        String partition=String.valueOf(topicPartition.partition());
        Long redisOffset= 0L;
        if(redisService.checkHashKey(topic, partition)){
            redisOffset = redisService.getHashKey(topic, partition);
        }
        redisService.setHashKey(topic, partition, String.valueOf(offset));
        System.out.println("The topic is "+topic+" and the partition is "+partition+" and the current message offset is "+offset+ " and the redis offset was "+redisOffset);
    }
}
