package com.betsettler.config;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import com.betsettler.domain.model.Bet;
import com.betsettler.domain.model.BetStatus;
import com.betsettler.domain.service.BetService;
import com.betsettler.repository.BetRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class BetSeedConfig implements ApplicationRunner {

	private static final Logger log = LoggerFactory.getLogger(BetSeedConfig.class);

	private final boolean seedEnabled;
	private final String seedLocation;
	private final BetRepository betRepository;
	private final BetService betService;
	private final ResourceLoader resourceLoader;
	private final ObjectMapper objectMapper;

	public BetSeedConfig(
			@Value("${app.seed.enabled:true}") boolean seedEnabled,
			@Value("${app.seed.location:classpath:data/sample-bets.json}") String seedLocation,
			BetRepository betRepository,
			BetService betService,
			ResourceLoader resourceLoader,
			ObjectMapper objectMapper) {
		this.seedEnabled = seedEnabled;
		this.seedLocation = seedLocation;
		this.betRepository = betRepository;
		this.betService = betService;
		this.resourceLoader = resourceLoader;
		this.objectMapper = objectMapper;
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		if (!seedEnabled) {
			log.info("Bet seeding disabled");
			return;
		}

		if (betRepository.hasAnyBets()) {
			log.info("Skipping bet seeding; bets already exist in Redis");
			return;
		}

		List<SeedBet> seedBets = loadSeedBets();
		for (SeedBet seedBet : seedBets) {
			betService.createBet(toBet(seedBet));
		}
		log.info("Seeded {} bets from {}", seedBets.size(), seedLocation);
	}

	private Bet toBet(SeedBet seedBet) {
		Bet bet = new Bet();
		bet.setBetId(seedBet.betId());
		bet.setUserId(seedBet.userId());
		bet.setEventId(seedBet.eventId());
		bet.setEventMarketId(seedBet.eventMarketId());
		bet.setEventWinnerId(seedBet.eventWinnerId());
		bet.setBetAmount(seedBet.betAmount());
		bet.setOdds(seedBet.odds());
		bet.setStatus(BetStatus.OPEN);
		return bet;
	}

	private List<SeedBet> loadSeedBets() throws IOException {
		Resource resource = resourceLoader.getResource(seedLocation);
		try (InputStream inputStream = resource.getInputStream()) {
			return objectMapper.readValue(inputStream, new TypeReference<>() {
			});
		}
	}

	private record SeedBet(
			String betId,
			String userId,
			String eventId,
			String eventMarketId,
			String eventWinnerId,
			BigDecimal betAmount,
			BigDecimal odds) {
	}

}
