package edu.pdae.cs.accountmgmt.config;

import edu.pdae.cs.common.model.dto.UserMutationDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.converter.ByteArrayJsonMessageConverter;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@Slf4j
@EnableKafka
public class MessagingConfiguration {

    public static final String USERS_TOPIC = "cs.account-mgmt.users-mutation-topic";

    private final KafkaProperties kafkaProperties;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        final Map<String, Object> configs = new HashMap<>(kafkaProperties.buildAdminProperties());
        return new KafkaAdmin(configs);
    }

    @Bean
    public ProducerFactory<String, UserMutationDTO> userMutationDTOProducerFactory() {
        final Map<String, Object> configs = new HashMap<>(kafkaProperties.buildProducerProperties());

        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(configs);
    }

    @Bean
    public KafkaTemplate<String, UserMutationDTO> notificationDTOKafkaTemplate() {
        return new KafkaTemplate<>(userMutationDTOProducerFactory());
    }

    @Bean
    public RecordMessageConverter messageConverter() {
        return new ByteArrayJsonMessageConverter();
    }

    @Bean
    public NewTopic activityTopic() {
        return TopicBuilder.name(USERS_TOPIC).build();
    }

}
