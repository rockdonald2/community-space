package edu.pdae.cs.activitynotificationsmgmt.config;

import edu.pdae.cs.activitynotificationsmgmt.model.dto.DueMemoDTO;
import edu.pdae.cs.activitynotificationsmgmt.model.dto.NotificationMessageDTO;
import edu.pdae.cs.common.model.dto.ActivityFiredDTO;
import edu.pdae.cs.common.model.dto.HubMemberMutationDTO;
import edu.pdae.cs.common.model.dto.HubMutationDTO;
import edu.pdae.cs.common.model.dto.MemoMutationDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
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
@Slf4j
@EnableKafka
public class MessagingConfiguration {

    public static final String ACTIVITY_TOPIC = "cs.activity-mgmt.activity-topic";
    public static final String DUE_MEMO_TOPIC = "cs.activity-mgmt.due-memo-topic";
    public static final String MEMBER_MUTATION_TOPIC = "cs.hub-mgmt.hub-member-mutation-topic";
    public static final String HUB_MUTATION_TOPIC = "cs.hub-mgmt.hub-mutation-topic";
    public static final String MEMO_MUTATION_TOPIC = "cs.memo-mgmt.memo-mutation-topic";
    public static final String NOTIFICATIONS_TOPIC = "cs.activity-mgmt.notifications-topic";


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
    public ConsumerFactory<String, HubMemberMutationDTO> hubMemberMutationDTOConsumerFactory() {
        final Map<String, Object> props = new HashMap<>(kafkaProperties.buildConsumerProperties());
        final var jsonDeserializer = new JsonDeserializer<>(HubMemberMutationDTO.class);

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), jsonDeserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, HubMemberMutationDTO> hubMemberMutationDTOConcurrentKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, HubMemberMutationDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(hubMemberMutationDTOConsumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, HubMutationDTO> hubMutationDTOConsumerFactory() {
        final Map<String, Object> props = new HashMap<>(kafkaProperties.buildConsumerProperties());
        final var jsonDeserializer = new JsonDeserializer<>(HubMutationDTO.class);

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), jsonDeserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, HubMutationDTO> hubMutationDTOConcurrentKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, HubMutationDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(hubMutationDTOConsumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, MemoMutationDTO> memoMutationDTOConsumerFactory() {
        final Map<String, Object> props = new HashMap<>(kafkaProperties.buildConsumerProperties());
        final var jsonDeserializer = new JsonDeserializer<>(MemoMutationDTO.class);

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), jsonDeserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, MemoMutationDTO> memoMutationDTOConcurrentKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, MemoMutationDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(memoMutationDTOConsumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, NotificationMessageDTO> notificationDTOConsumerFactory() {
        final Map<String, Object> props = new HashMap<>(kafkaProperties.buildConsumerProperties());
        final var jsonDeserializer = new JsonDeserializer<>(NotificationMessageDTO.class);

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), jsonDeserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, NotificationMessageDTO> notificationDTOConcurrentKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, NotificationMessageDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(notificationDTOConsumerFactory());
        return factory;
    }

    @Bean
    public ProducerFactory<String, NotificationMessageDTO> notificationDTOProducerFactory() {
        final Map<String, Object> configs = new HashMap<>(kafkaProperties.buildProducerProperties());

        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(configs);
    }

    @Bean
    public KafkaTemplate<String, NotificationMessageDTO> notificationDTOKafkaTemplate() {
        return new KafkaTemplate<>(notificationDTOProducerFactory());
    }

    @Bean
    public ProducerFactory<String, DueMemoDTO> dueMemoDTOProducerFactory() {
        final Map<String, Object> configs = new HashMap<>(kafkaProperties.buildProducerProperties());

        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(configs);
    }

    @Bean
    public KafkaTemplate<String, DueMemoDTO> dueMemoDTOKafkaTemplate() {
        return new KafkaTemplate<>(dueMemoDTOProducerFactory());
    }

    @Bean
    public ConsumerFactory<String, DueMemoDTO> dueMemoDTOConsumerFactory() {
        final Map<String, Object> props = new HashMap<>(kafkaProperties.buildConsumerProperties());
        final var jsonDeserializer = new JsonDeserializer<>(DueMemoDTO.class);

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), jsonDeserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, DueMemoDTO> dueMemoDTOConcurrentKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, DueMemoDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(dueMemoDTOConsumerFactory());
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

    @Bean
    public NewTopic dueMemoTopic() {
        return TopicBuilder.name(DUE_MEMO_TOPIC).build();
    }

}
