package com.betsettler.messaging.rocketmq.producer;

import com.betsettler.domain.model.BetSettlement;

public interface BetSettlementProducer {

	void send(BetSettlement settlement);

}
