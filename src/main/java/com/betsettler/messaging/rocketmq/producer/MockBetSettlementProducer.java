package com.betsettler.messaging.rocketmq.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.betsettler.domain.model.BetSettlement;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class MockBetSettlementProducer implements BetSettlementProducer {

	private static final Logger log = LoggerFactory.getLogger(MockBetSettlementProducer.class);

	private final String betSettlementsTopic;
	private final ObjectMapper objectMapper;

	public MockBetSettlementProducer(
			@Value("${app.rocketmq.topics.bet-settlements}") String betSettlementsTopic,
			ObjectMapper objectMapper) {
		this.betSettlementsTopic = betSettlementsTopic;
		this.objectMapper = objectMapper;
	}

	@Override
	public void send(BetSettlement settlement) {
		try {
			String payload = objectMapper.writeValueAsString(settlement);
			log.info("Mock RocketMQ publish topic={} payload={}", betSettlementsTopic, payload);
		}
		catch (JsonProcessingException ex) {
			log.error("Failed to serialize bet settlement betId={}", settlement.getBetId(), ex);
		}
	}

}
