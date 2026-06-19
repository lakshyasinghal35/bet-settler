package com.betsettler.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.betsettler.dto.BetResponse;
import com.betsettler.dto.CreateBetRequest;
import com.betsettler.model.Bet;
import com.betsettler.repository.BetRepository;

@Service
public class BetService {

	private static final Logger log = LoggerFactory.getLogger(BetService.class);

	private final BetRepository betRepository;

	public BetService(BetRepository betRepository) {
		this.betRepository = betRepository;
	}

	public Bet createBet(Bet bet) {
		return betRepository.save(bet);
	}

	public List<BetResponse> createBets(List<CreateBetRequest> requests) {
		log.info("Creating bets for {} requests", requests.size());
		List<BetResponse> created = new ArrayList<>();
		for (CreateBetRequest request : requests) {
			try {
				Bet saved = betRepository.save(new Bet(request));
				created.add(new BetResponse(saved));
			}
			catch (RuntimeException ex) {
				log.warn("Failed to create bet betId={}", request.getBetId(), ex);
			}
		}
		return created;
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
