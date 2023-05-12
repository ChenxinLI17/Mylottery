package fr.utc.mylottery.strategy.service.algorithm.impl;

import fr.utc.mylottery.strategy.service.algorithm.BaseAlgorithm;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.List;

/**
 * 必中奖策略抽奖，排掉已经中奖的概率，重新计算中奖范围
 */
@Component("singleRateRandomDrawAlgorithm")
public class SingleRateRandomDrawAlgorithm extends BaseAlgorithm {
    @Override
    public String randomDraw(Long strategyId, List<String> excludeAwardIds) {

        // 获取策略对应的元祖
        String[] rateTuple = super.rateTupleMap.get(strategyId);
        assert rateTuple != null;

        // 随机索引
        int randomVal = new SecureRandom().nextInt(100) + 1;
        int idx = super.hashIdx(randomVal);

        // 返回结果
        String awardId = rateTuple[idx];
        if (excludeAwardIds.contains(awardId)) return "未中奖";

        return awardId;
    }

}
