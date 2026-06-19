package com.betsettler.messaging.kafka.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import com.betsettler.model.EventOutcome;

@Configuration
public class KafkaConsumerConfig {

	@Bean
	ConsumerFactory<String, EventOutcome> eventOutcomeConsumerFactory(KafkaProperties kafkaProperties) {
		var props = kafkaProperties.buildConsumerProperties(null);
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
		props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.betsettler.domain.model");
		props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, EventOutcome.class.getName());
		return new DefaultKafkaConsumerFactory<>(props);
	}

	@Bean
	ConcurrentKafkaListenerContainerFactory<String, EventOutcome> eventOutcomeKafkaListenerContainerFactory(
			ConsumerFactory<String, EventOutcome> eventOutcomeConsumerFactory) {
		ConcurrentKafkaListenerContainerFactory<String, EventOutcome> factory =
				new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(eventOutcomeConsumerFactory);
		return factory;
	}

}
