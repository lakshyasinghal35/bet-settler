package com.betsettler.api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.betsettler.api.dto.BetResponse;
import com.betsettler.api.dto.CreateBetRequest;
import com.betsettler.domain.model.Bet;
import com.betsettler.domain.service.BetService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/bets")
public class BetController {

	private final BetService betService;

	public BetController(BetService betService) {
		this.betService = betService;
	}

	@PostMapping
	public ResponseEntity<String> createBet(@Valid @RequestBody CreateBetRequest request) {
		Bet saved = betService.createBet(new Bet(request));
		return ResponseEntity.status(HttpStatus.CREATED).body("Created bet with ID: " + saved.getBetId());
	}

	@GetMapping
	public ResponseEntity<List<BetResponse>> getBetsByEventId(@RequestParam String eventId) {
		List<BetResponse> bets = betService.findByEventId(eventId);
		return ResponseEntity.ok(bets);
	}

}
