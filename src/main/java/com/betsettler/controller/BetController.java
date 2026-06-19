package com.betsettler.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.betsettler.dto.BetResponse;
import com.betsettler.dto.CreateBetRequest;
import com.betsettler.domain.service.BetService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

@RestController
@RequestMapping("/api/v1/bets")
public class BetController {

	private final BetService betService;

	public BetController(BetService betService) {
		this.betService = betService;
	}

	@PostMapping
	public ResponseEntity<List<BetResponse>> createBets(
			@Valid @NotEmpty @RequestBody List<@Valid CreateBetRequest> requests) {
		List<BetResponse> created = betService.createBets(requests);
		return ResponseEntity.status(HttpStatus.CREATED).body(created);
	}

	@GetMapping
	public ResponseEntity<List<BetResponse>> getBetsByEventId(@RequestParam String eventId) {
		List<BetResponse> bets = betService.findByEventId(eventId);
		return ResponseEntity.ok(bets);
	}

}
