package com.betsettler.repository;

import java.util.List;
import java.util.Optional;

import com.betsettler.domain.model.Bet;

public interface BetRepository {

	Bet save(Bet bet);

	Optional<Bet> findById(String betId);

	List<Bet> findByEventId(String eventId);

}
