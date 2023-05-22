package edu.pdae.cs.activitynotificationsmgmt.config;

import edu.pdae.cs.common.model.dto.ActivityFiredDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.support.converter.ByteArrayJsonMessageConverter;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@Slf4j
@EnableKafka
public class MessagingConfiguration {

    public static final String ACTIVITY_TOPIC = "cs.activity-mgmt.activity-topic";

    private final KafkaProperties kafkaProperties;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        final Map<String, Object> configs = new HashMap<>(kafkaProperties.buildAdminProperties());
        return new KafkaAdmin(configs);
    }

    @Bean
    public ConsumerFactory<String, ActivityFiredDTO> activityDTOConsumerFactory() {
        final Map<String, Object> props = new HashMap<>(kafkaProperties.buildConsumerProperties());
        final var jsonDeserializer = new JsonDeserializer<>(ActivityFiredDTO.class);

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), jsonDeserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ActivityFiredDTO> activityDTOConcurrentKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ActivityFiredDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(activityDTOConsumerFactory());
        return factory;
    }

    @Bean
    public RecordMessageConverter messageConverter() {
        return new ByteArrayJsonMessageConverter();
    }

    @Bean
    public NewTopic activityTopic() {
        return TopicBuilder.name(ACTIVITY_TOPIC).build();
    }

}
