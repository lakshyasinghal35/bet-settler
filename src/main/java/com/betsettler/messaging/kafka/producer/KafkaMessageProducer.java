package com.betsettler.messaging.kafka.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaMessageProducer {

	private static final Logger log = LoggerFactory.getLogger(KafkaMessageProducer.class);

	private final KafkaTemplate<String, String> kafkaTemplate;

	public KafkaMessageProducer(KafkaTemplate<String, String> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}

	public void sendMessage(String topic, String key, String message) {
		kafkaTemplate.send(topic, key, message);
		log.info("Sent message to topic={} using key={}", topic, key);
	}

}
