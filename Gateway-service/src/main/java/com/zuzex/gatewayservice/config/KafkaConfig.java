package com.zuzex.gatewayservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.TopicConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.internals.DefaultKafkaReceiver;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConfig {

    private final String bootstrapAddress = "localhost:29092";
    private final String groupId = "foo";

    @Bean
    Map<String, Object> kafkaConsumerConfiguration() {

        Map<String, Object> configuration = new HashMap<>();
        configuration.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        configuration.put(ConsumerConfig.GROUP_ID_CONFIG, "google1");
//        configuration.put(ConsumerConfig.CLIENT_ID_CONFIG, "trans-string-consumer-egen-new");
        configuration.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        configuration.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,StringDeserializer.class.getName());
        configuration.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        configuration.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return configuration;
    }

    @Bean
    ReceiverOptions<String, String> kafkaReceiverOptions() {
        ReceiverOptions<String, String> options = ReceiverOptions.create(kafkaConsumerConfiguration());
        return options.subscription(Collections.singleton("bookRs"));
    }

    @Bean
    KafkaReceiver<String, String> kafkaReceiver(ReceiverOptions<String, String> kafkaReceiverOptions) {
        return new DefaultKafkaReceiver<>(reactor.kafka.receiver.internals.ConsumerFactory.INSTANCE,kafkaReceiverOptions);
    }

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapAddress);
        configProps.put(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);
        configProps.put(
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }

    @Bean
    public Map<String,String> topicConfig() {
        Map<String,String> map = new HashMap<>();
        map.put(TopicConfig.RETENTION_MS_CONFIG,"60000");
        return map;
    }

    @Bean
    public NewTopic topic1() {
        return TopicBuilder.name("bookRq")
                .partitions(2)
                .replicas(2)
                .configs(topicConfig())
                .build();
    }

    @Bean
    public NewTopic topic2() {
        return TopicBuilder.name("bookRs")
                .partitions(2)
                .replicas(2)
                .configs(topicConfig())
                .build();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}

