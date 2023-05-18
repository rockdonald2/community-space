package edu.pdae.cs.accountmgmt.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class MessagingConfiguration {

    public static final String ACTIVE_STATUS_TOPIC = "cs.account-mgmt.active-status-topic";
    public static final String ACTIVE_STATUS_BROADCAST_TOPIC = "cs.account-mgmt.active-status-broadcast-topic";

    private final KafkaProperties kafkaProperties;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        final Map<String, Object> configs = new HashMap<>(kafkaProperties.buildAdminProperties());
        return new KafkaAdmin(configs);
    }

    @Bean
    public ProducerFactory<String, UserPresenceNotificationDTO> presenceProducerFactory() {
        final Map<String, Object> configs = new HashMap<>(kafkaProperties.buildProducerProperties());

        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(configs);
    }

    @Bean
    public ProducerFactory<String, Void> broadcastProducerFactory() {
        final Map<String, Object> configs = new HashMap<>(kafkaProperties.buildProducerProperties());

        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        return new DefaultKafkaProducerFactory<>(configs);
    }

    @Bean
    public KafkaTemplate<String, UserPresenceNotificationDTO> presenceKafkaTemplate() {
        return new KafkaTemplate<>(presenceProducerFactory());
    }

    @Bean
    public KafkaTemplate<String, Void> broadcastKafkaTemplate() {
        return new KafkaTemplate<>(broadcastProducerFactory());
    }

    @Bean
    public ConsumerFactory<String, UserPresenceNotificationDTO> presenceConsumerFactory() {
        final Map<String, Object> props = new HashMap<>(kafkaProperties.buildConsumerProperties());
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new JsonDeserializer<>(UserPresenceNotificationDTO.class));


    }

    @Bean
    public ConsumerFactory<String, Void> broadcastConsumerFactory() {
        final Map<String, Object> props = new HashMap<>(kafkaProperties.buildConsumerProperties());
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), null);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserPresenceNotificationDTO> presenceKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, UserPresenceNotificationDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(presenceConsumerFactory());
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Void> broadcastKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Void> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(broadcastConsumerFactory());
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

    @Bean
    public NewTopic activeStatusBroadcastTopic() {
        return TopicBuilder.name(ACTIVE_STATUS_BROADCAST_TOPIC).build();

    }
