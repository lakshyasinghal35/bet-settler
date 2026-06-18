package com.betsettler.repository.redis;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.betsettler.domain.model.Bet;
import com.betsettler.domain.model.BetStatus;
import com.redis.testcontainers.RedisContainer;

@Testcontainers
@SpringBootTest
class RedisBetRepositoryTest {

	@Container
	static RedisContainer redisContainer = new RedisContainer(DockerImageName.parse("redis:7-alpine"));

	@Autowired
	private RedisBetRepository betRepository;

	@DynamicPropertySource
	static void configureProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.data.redis.host", redisContainer::getHost);
		registry.add("spring.data.redis.port", () -> redisContainer.getMappedPort(6379));
		registry.add("app.seed.enabled", () -> false);
	}

	@BeforeEach
	void setUp() throws Exception {
		redisContainer.execInContainer("redis-cli", "FLUSHDB");
	}

	@Test
	void savesAndFindsBetByEventId() {
		Bet bet = new Bet("bet-10", "user-10", "evt-99", "market-winner", "team-a",
				new BigDecimal("20.00"), new BigDecimal("1.50"), BetStatus.OPEN, null);
		betRepository.save(bet);

		List<Bet> bets = betRepository.findByEventId("evt-99");
		assertThat(bets).hasSize(1);
		assertThat(bets.get(0).getBetId()).isEqualTo("bet-10");
		assertThat(bets.get(0).getStatus()).isEqualTo(BetStatus.OPEN);
	}

	@Test
	void markSettledIfOpenIsIdempotent() {
		Bet bet = new Bet("bet-11", "user-11", "evt-100", "market-winner", "team-b",
				new BigDecimal("15.00"), new BigDecimal("2.00"), BetStatus.OPEN, null);
		betRepository.save(bet);

		Instant settledAt = Instant.parse("2026-06-17T12:00:00Z");
		assertThat(betRepository.markSettledIfOpen("bet-11", settledAt)).isTrue();
		assertThat(betRepository.markSettledIfOpen("bet-11", settledAt)).isFalse();

		Optional<Bet> updated = betRepository.findById("bet-11");
		assertThat(updated).isPresent();
		assertThat(updated.get().getStatus()).isEqualTo(BetStatus.SETTLED);
		assertThat(updated.get().getSettledAt()).isEqualTo(settledAt);
	}

}
