package com.betsettler.repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.betsettler.model.Bet;
import com.betsettler.model.BetResult;
import com.betsettler.redis.service.RedisDBService;

@Repository
public class RedisBetRepository implements BetRepository {

	private static final String BET_KEY_PREFIX = "bet:";
	private static final String EVENT_BETS_KEY_PREFIX = "event:";
	private static final String EVENT_BETS_KEY_SUFFIX = ":betIds";

	private final RedisDBService redisDBService;

	public RedisBetRepository(RedisDBService redisDBService) {
		this.redisDBService = redisDBService;
	}

	@Override
	public Bet save(Bet bet) {
		String betKey = betKey(bet.getBetId());
		Map<String, String> fields = toHash(bet);
		redisDBService.hashPutAll(betKey, fields);
		redisDBService.setAdd(eventBetIdsKey(bet.getEventId()), bet.getBetId());
		return bet;
	}

	@Override
	public Optional<Bet> findById(String betId) {
		Map<Object, Object> entries = redisDBService.hashGetAll(betKey(betId));
		if (entries.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(fromHash(entries));
	}

	@Override
	public List<Bet> findByEventId(String eventId) {
		Set<String> betIds = redisDBService.setMembers(eventBetIdsKey(eventId));
		if (betIds == null || betIds.isEmpty()) {
			return List.of();
		}

		List<Bet> bets = new ArrayList<>();
		for (String betId : betIds) {
			findById(betId).ifPresent(bets::add);
		}
		return bets;
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
		if (bet.getResult() != null) {
			fields.put("result", bet.getResult().name());
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

		String result = stringValue(entries.get("result"));
		if (result != null && !result.isBlank()) {
			bet.setResult(BetResult.valueOf(result));
		}
		return bet;
	}

	private String stringValue(Object value) {
		return value == null ? null : value.toString();
	}

}
