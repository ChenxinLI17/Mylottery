package fr.utc.mylottery.domain.strategy.service.algorithm;

import java.util.List;
import fr.utc.mylottery.domain.strategy.model.vo.AwardRateInfo;

public interface IDrawAlgorithm {

    void initRateTuple(Long strategyId, List<AwardRateInfo> awardRateInfoList);

    boolean isExistRateTuple(Long strategyId);

    /**
     * SecureRandom 生成随机数，索引到对应的奖品信息返回结果
     *
     * @param strategyId 策略ID
     * @param excludeAwardIds 排除掉已经不能作为抽奖的奖品ID，留给风控和空库存使用
     * @return 中奖结果
     */
    String randomDraw(Long strategyId, List<String> excludeAwardIds);

}
