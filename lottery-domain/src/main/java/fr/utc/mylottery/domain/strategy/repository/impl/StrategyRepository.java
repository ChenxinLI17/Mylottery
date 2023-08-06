package fr.utc.mylottery.domain.strategy.repository.impl;

import fr.utc.mylottery.infrastructure.dao.IAwardDao;
import fr.utc.mylottery.infrastructure.dao.IStrategyDao;
import fr.utc.mylottery.infrastructure.dao.IStrategyDetailDao;
import fr.utc.mylottery.infrastructure.po.Award;
import fr.utc.mylottery.infrastructure.po.Strategy;
import fr.utc.mylottery.infrastructure.po.StrategyDetail;
import fr.utc.mylottery.domain.strategy.model.aggregates.StrategyRich;
import fr.utc.mylottery.domain.strategy.repository.IStrategyRepository;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 策略表仓储服务
 */
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

    //这个strategyId的奖品列表中没有库存的awardId
    @Override
    public List<String> queryNoStockStrategyAwardList(Long strategyId) {
        return strategyDetailDao.queryNoStockStrategyAwardList(strategyId);
    }

    //更新库存
    //对于mybatis的update、insert的操作，操作成功后会得到一个int类型的影响结果条数，
    //直接在dao层返回就可以得到，可以通过这个返回值做成功与否的操作。
    @Override
    public boolean deductStock(Long strategyId, String awardId) {
        StrategyDetail req = new StrategyDetail();
        req.setStrategyId(strategyId);
        req.setAwardId(awardId);
        int count = strategyDetailDao.deductStock(req);
        return count == 1;
    }
}
