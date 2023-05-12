package fr.utc.mylottery.strategy.repository.impl;

import fr.utc.mylottery.infrastructure.dao.IAwardDao;
import fr.utc.mylottery.infrastructure.dao.IStrategyDao;
import fr.utc.mylottery.infrastructure.dao.IStrategyDetailDao;
import fr.utc.mylottery.infrastructure.po.Award;
import fr.utc.mylottery.infrastructure.po.Strategy;
import fr.utc.mylottery.infrastructure.po.StrategyDetail;
import fr.utc.mylottery.strategy.model.aggregates.StrategyRich;
import fr.utc.mylottery.strategy.repository.IStrategyRepository;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class StrategyRepository implements IStrategyRepository {
    @Resource
    private IStrategyDao strategyDao;

    @Resource
    private IStrategyDetailDao strategyDetailDao;

    @Resource
    private IAwardDao awardDao;

    @Override
    public StrategyRich queryStrategyRich(Long strategyId) {
        Strategy strategy = strategyDao.queryStrategy(strategyId);
        List<StrategyDetail> strategyDetailList = strategyDetailDao.queryStrategyDetailList(strategyId);
        return new StrategyRich(strategyId, strategy, strategyDetailList);
    }

    @Override
    public Award queryAwardInfo(String awardId) {
        return awardDao.queryAwardInfo(awardId);
    }

}
