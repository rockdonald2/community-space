package edu.pdae.cs.accountmgmt.config;

import edu.pdae.cs.accountmgmt.model.dto.UserPresenceDTO;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.converter.ByteArrayJsonMessageConverter;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class MessagingConfiguration {

    public static final String ACTIVE_STATUS_TOPIC = "cs.account-mgmt.active-status-topic";
    private final KafkaProperties kafkaProperties;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        final Map<String, Object> configs = new HashMap<>(kafkaProperties.buildAdminProperties());
        return new KafkaAdmin(configs);
    }

    @Bean
    public ProducerFactory<String, UserPresenceDTO> userPresenceDTOProducerFactory() {
        final Map<String, Object> configs = new HashMap<>(kafkaProperties.buildProducerProperties());

        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(configs);
    }

    @Bean
    public KafkaTemplate<String, UserPresenceDTO> userPresenceDTOKafkaTemplate() {
        return new KafkaTemplate<>(userPresenceDTOProducerFactory());
    }

    @Bean
    public ConsumerFactory<String, UserPresenceDTO> userPresenceDTOConsumerFactory() {
        final Map<String, Object> props = new HashMap<>(kafkaProperties.buildConsumerProperties());
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new JsonDeserializer<>(UserPresenceDTO.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserPresenceDTO> userPresenceDTOKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, UserPresenceDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(userPresenceDTOConsumerFactory());
        return factory;
    }

    @Bean
    public RecordMessageConverter messageConverter() {
        return new ByteArrayJsonMessageConverter();
    }

    @Bean
    public NewTopic activeStatusTopic() {
        return TopicBuilder.name(ACTIVE_STATUS_TOPIC).build();
    }

}
