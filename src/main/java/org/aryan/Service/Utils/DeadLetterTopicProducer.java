package org.aryan.Service.Utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.ws.rs.core.Response;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.aryan.Entity.Album;
import org.aryan.Exception.BusinessException;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.Properties;

@ApplicationScoped
public class DeadLetterTopicProducer {
    private KafkaProducer<String, String> producer;

    public String TOPIC="dead-letter-topic";

    @ConfigProperty(name = "kafka.bootstrap.servers")
    public String kafkaBootstrapServers;

    public void configureDead(@Observes StartupEvent event) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        this.producer = new KafkaProducer<>(props);
        System.out.println("Dead Letter Producer started");
    }

    public void produce(String message) throws BusinessException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String serialisedmessage = mapper.writeValueAsString(message);
            ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC, serialisedmessage);

            producer.send(record, (metadata, exception) -> {
                if (exception != null) {
                    System.out.println("Error in producing dead letter message: " + exception.getMessage());
                    throw new RuntimeException(exception);
                } else {
                    System.out.println("Dead letter message sent to Kafka: " + message+" at offset "+metadata.offset()+" to topic "+metadata.topic());
                }
            });
        } catch (Exception e) {
            System.out.println("Error in producing dead letter message: " + e.getMessage());
            throw new BusinessException("Error in producing dead letter message: " + e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        }
    }


}
