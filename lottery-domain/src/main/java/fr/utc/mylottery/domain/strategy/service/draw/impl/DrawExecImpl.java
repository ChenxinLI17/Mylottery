package fr.utc.mylottery.domain.strategy.service.draw.impl;


import fr.utc.mylottery.domain.strategy.service.algorithm.IDrawAlgorithm;
import fr.utc.mylottery.domain.strategy.service.draw.AbstractDrawBase;
import fr.utc.mylottery.domain.strategy.service.draw.IDrawExec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSON;

import java.util.List;


@Service("drawExec")
public class DrawExecImpl extends AbstractDrawBase implements IDrawExec {
    private final Logger logger = LoggerFactory.getLogger(DrawExecImpl.class);

    @Override
    protected List<String> queryExcludeAwardIds(Long strategyId) {
        List<String> awardList = strategyRepository.queryNoStockStrategyAwardList(strategyId);
        logger.info("执行抽奖策略 strategyId：{}，无库存排除奖品列表ID集合 awardList：{}", strategyId, JSON.toJSONString(awardList));
        return awardList;
    }

    @Override
    protected String drawAlgorithm(Long strategyId, IDrawAlgorithm drawAlgorithm, List<String> excludeAwardIds) {
        // 执行抽奖
        String awardId = drawAlgorithm.randomDraw(strategyId, excludeAwardIds);
        // 判断抽奖结果
        if (null == awardId) {
            return null;
        }

        /***
         * 扣减库存，暂时采用数据库行级锁的方式进行扣减库存，后续优化为 Redis 分布式锁扣减 decr/incr
         * 数据库直接锁行记录的方式并不能支撑较大体量的并发
         * 因为在分库分表下的正常数据流量下的个人数据记录中，是可以使用行级锁的，
         * 因为他只影响到自己的记录，不会影响到其他人
         */
        boolean isSuccess = strategyRepository.deductStock(strategyId, awardId);

        // 返回结果，库存扣减成功返回奖品ID，否则返回NULL
        return isSuccess ? awardId : null;
    }

}
