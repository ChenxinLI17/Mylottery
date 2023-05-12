package fr.utc.mylottery.strategy.repository;

import fr.utc.mylottery.infrastructure.po.Award;
import fr.utc.mylottery.strategy.model.aggregates.StrategyRich;

public interface IStrategyRepository {
    StrategyRich queryStrategyRich(Long strategyId);

    Award queryAwardInfo(String awardId);
}
