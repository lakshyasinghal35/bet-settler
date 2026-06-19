package com.betsettler.messaging.rocketmq.producer;

import com.betsettler.model.Bet;

public interface BetSettlementProducer {

	void send(Bet bet);

}
