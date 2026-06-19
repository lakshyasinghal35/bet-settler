package com.betsettler.messaging.kafka.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import com.betsettler.model.EventOutcome;

@Configuration
public class KafkaProducerConfig {

	@Bean
	ProducerFactory<String, EventOutcome> eventOutcomeProducerFactory(KafkaProperties kafkaProperties) {
		var props = kafkaProperties.buildProducerProperties(null);
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
		return new DefaultKafkaProducerFactory<>(props);
	}

	@Bean
	KafkaTemplate<String, EventOutcome> eventOutcomeKafkaTemplate(
			ProducerFactory<String, EventOutcome> eventOutcomeProducerFactory) {
		return new KafkaTemplate<>(eventOutcomeProducerFactory);
	}

}
