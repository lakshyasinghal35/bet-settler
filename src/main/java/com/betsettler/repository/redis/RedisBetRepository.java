package com.betsettler.repository.redis;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import com.betsettler.domain.model.Bet;
import com.betsettler.domain.model.BetStatus;
import com.betsettler.repository.BetRepository;

@Repository
public class RedisBetRepository implements BetRepository {

	private static final String BET_KEY_PREFIX = "bet:";
	private static final String EVENT_BETS_KEY_PREFIX = "event:";
	private static final String EVENT_BETS_KEY_SUFFIX = ":betIds";

	private final StringRedisTemplate redisTemplate;

	public RedisBetRepository(StringRedisTemplate redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	@Override
	public Bet save(Bet bet) {
		String betKey = betKey(bet.getBetId());
		Map<String, String> fields = toHash(bet);
		redisTemplate.opsForHash().putAll(betKey, fields);
		redisTemplate.opsForSet().add(eventBetIdsKey(bet.getEventId()), bet.getBetId());
		return bet;
	}

	@Override
	public Optional<Bet> findById(String betId) {
		Map<Object, Object> entries = redisTemplate.opsForHash().entries(betKey(betId));
		if (entries.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(fromHash(entries));
	}

	@Override
	public List<Bet> findByEventId(String eventId) {
		Set<String> betIds = redisTemplate.opsForSet().members(eventBetIdsKey(eventId));
		if (betIds == null || betIds.isEmpty()) {
			return List.of();
		}

		List<Bet> bets = new ArrayList<>();
		for (String betId : betIds) {
			findById(betId).ifPresent(bets::add);
		}
		return bets;
	}

	@Override
	public boolean markSettledIfOpen(String betId, Instant settledAt) {
		String key = betKey(betId);
		Boolean updated = redisTemplate.execute(new SessionCallback<>() {
			@Override
			@SuppressWarnings("unchecked")
			public Boolean execute(RedisOperations operations) throws DataAccessException {
				operations.watch(key);
				String status = (String) operations.opsForHash().get(key, "status");
				if (!BetStatus.OPEN.name().equals(status)) {
					operations.unwatch();
					return false;
				}

				operations.multi();
				operations.opsForHash().put(key, "status", BetStatus.SETTLED.name());
				operations.opsForHash().put(key, "settledAt", settledAt.toString());
				List<Object> results = operations.exec();
				return results != null;
			}
		});
		return Boolean.TRUE.equals(updated);
	}

	@Override
	public boolean hasAnyBets() {
		Set<String> keys = redisTemplate.keys(BET_KEY_PREFIX + "*");
		return keys != null && !keys.isEmpty();
	}

	private String betKey(String betId) {
		return BET_KEY_PREFIX + betId;
	}

	private String eventBetIdsKey(String eventId) {
		return EVENT_BETS_KEY_PREFIX + eventId + EVENT_BETS_KEY_SUFFIX;
	}

	private Map<String, String> toHash(Bet bet) {
		Map<String, String> fields = new HashMap<>();
		fields.put("betId", bet.getBetId());
		fields.put("userId", bet.getUserId());
		fields.put("eventId", bet.getEventId());
		fields.put("eventMarketId", bet.getEventMarketId());
		fields.put("eventWinnerId", bet.getEventWinnerId());
		fields.put("betAmount", bet.getBetAmount().toPlainString());
		fields.put("odds", bet.getOdds().toPlainString());
		fields.put("status", bet.getStatus().name());
		if (bet.getSettledAt() != null) {
			fields.put("settledAt", bet.getSettledAt().toString());
		}
		return fields;
	}

	private Bet fromHash(Map<Object, Object> entries) {
		Bet bet = new Bet();
		bet.setBetId(stringValue(entries.get("betId")));
		bet.setUserId(stringValue(entries.get("userId")));
		bet.setEventId(stringValue(entries.get("eventId")));
		bet.setEventMarketId(stringValue(entries.get("eventMarketId")));
		bet.setEventWinnerId(stringValue(entries.get("eventWinnerId")));
		bet.setBetAmount(new BigDecimal(stringValue(entries.get("betAmount"))));
		bet.setOdds(new BigDecimal(stringValue(entries.get("odds"))));
		bet.setStatus(BetStatus.valueOf(stringValue(entries.get("status"))));

		String settledAt = stringValue(entries.get("settledAt"));
		if (settledAt != null && !settledAt.isBlank()) {
			bet.setSettledAt(Instant.parse(settledAt));
		}
		return bet;
	}

	private String stringValue(Object value) {
		return value == null ? null : value.toString();
	}

}
