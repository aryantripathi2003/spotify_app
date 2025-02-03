package org.aryan.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.ws.rs.core.Response;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.aryan.Entity.Album;
import org.aryan.Exception.BusinessException;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import java.util.Properties;
import java.util.concurrent.CompletionStage;

@ApplicationScoped
public class KafkaServiceProducer {

//    @Channel("AlbumOut1")
//    Emitter<String> emitter;

    private KafkaProducer<String, String> producer;

    @ConfigProperty(name = "kafka.bootstrap.servers")
    public String kafkaBootstrapServers;

    @ConfigProperty(name = "kafka.topic")
    public String TOPIC;

    public void KafkaProducer(@Observes StartupEvent event) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        this.producer = new KafkaProducer<>(props);
        System.out.println("Producer started");
    }

    public void produce(Album message) throws BusinessException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String serialisedmessage = mapper.writeValueAsString(message);
            ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC, serialisedmessage);

            producer.send(record, (metadata, exception) -> {
                if (exception != null) {
                    System.out.println("Error in producing message: " + exception.getMessage());
                    throw new RuntimeException(exception);
                } else {
                    System.out.println("Message sent to Kafka: " + message+" at offset "+metadata.offset()+" to topic "+metadata.topic());
                }
            });

//            CompletionStage<Void> ackStage = emitter.send(serialisedmessage);
//            ackStage.whenComplete((ack, error) -> {
//                if (error != null) {
//                    System.out.println("Error in sending message: " + error.getMessage());
//                    throw new RuntimeException(error);
//                } else {
//                    System.out.println("Message sent to Kafka: " + message);
//                }
//            });
        } catch (Exception e) {
            System.out.println("Error in producing message: " + e.getMessage());
            throw new BusinessException("Error in producing message: " + e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        }
    }
}