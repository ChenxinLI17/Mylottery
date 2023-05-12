package fr.utc.mylottery.domain.strategy.repository;

import fr.utc.mylottery.infrastructure.po.Award;
import fr.utc.mylottery.domain.strategy.model.aggregates.StrategyRich;

import java.util.List;

public interface IStrategyRepository {//数据访问
    StrategyRich queryStrategyRich(Long strategyId);

    Award queryAwardInfo(String awardId);

    List<String> queryNoStockStrategyAwardList(Long strategyId);

    /**
     * 扣减库存
     * @param strategyId 策略ID
     * @param awardId    奖品ID
     * @return           扣减结果
     */
    boolean deductStock(Long strategyId, String awardId);
}
