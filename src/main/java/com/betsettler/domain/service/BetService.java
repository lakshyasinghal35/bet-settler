package com.betsettler.domain.service;

import java.util.List;
import java.util.Optional;

import com.betsettler.api.dto.BetResponse;
import org.springframework.stereotype.Service;

import com.betsettler.domain.model.Bet;
import com.betsettler.repository.BetRepository;

@Service
public class BetService {

	private final BetRepository betRepository;

	public BetService(BetRepository betRepository) {
		this.betRepository = betRepository;
	}

	public Bet createBet(Bet bet) {
		return betRepository.save(bet);
	}

	public Optional<Bet> findById(String betId) {
		return betRepository.findById(betId);
	}

	public List<BetResponse> findByEventId(String eventId) {
		return betRepository.findByEventId(eventId).stream()
				.map(BetResponse::new)
				.toList();
	}

}
