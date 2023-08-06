package fr.utc.mylottery.domain.strategy.service.algorithm.impl;

import fr.utc.mylottery.domain.strategy.service.algorithm.BaseAlgorithm;
import fr.utc.mylottery.domain.strategy.model.vo.AwardRateInfo;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * @description: 必中奖策略抽奖，排掉已经中奖的概率，重新计算中奖范围
 */
@Component("entiretyRateRandomDrawAlgorithm")
public class EntiretyRateRandomDrawAlgorithm extends BaseAlgorithm {
    //返回抽到的奖品ID
    @Override
    public String randomDraw(Long strategyId, List<String> excludeAwardIds) {

        BigDecimal currentDenominator = BigDecimal.ZERO;//排除已经中奖的奖品的概率
        //比如三个奖品a,b,c，中奖概率分别是0.2,0.3,0.5,若c已被抽中，differenceDenominator = 0.2 + 0.3

        // 排除掉不在抽奖范围的奖品ID集合
        List<AwardRateInfo> currentAwardRateList = new ArrayList<>();
        List<AwardRateInfo> awardRateIntervalValList = awardRateInfoMap.get(strategyId);

        for (AwardRateInfo awardRateInfo : awardRateIntervalValList) {
            String awardId = awardRateInfo.getAwardId();
            if (excludeAwardIds.contains(awardId)) {
                continue;
            }
            currentAwardRateList.add(awardRateInfo);
            currentDenominator = currentDenominator.add(awardRateInfo.getAwardRate());
        }

        // 前置判断
        if (currentAwardRateList.size() == 0) return null;
        if (currentAwardRateList.size() == 1) return currentAwardRateList.get(0).getAwardId();

        // 获取随机概率值
        int randomVal = this.generateSecureRandomIntCode(100); // 生成1-100的随机数


        // 循环获取奖品
        String awardId = null;
        int cursorVal = 0;
        for (AwardRateInfo awardRateInfo : currentAwardRateList) {
            int rateVal = awardRateInfo.getAwardRate()
                    .divide(currentDenominator, 2, RoundingMode.UP)
                    .multiply(new BigDecimal(100)).intValue();
            // divide(currentDenominator, 2, RoundingMode.UP)
            // 该奖品的中奖率除以当前剩余的奖品的中奖总概率，保留两位小数并远离零方向舍入
            //ROUND_UP 远离零方向舍入的舍入模式， 1.55=1.6，-1.55=-1.6

            if (randomVal <= (cursorVal + rateVal)) {//判断随机数掉在哪个区间
                awardId = awardRateInfo.getAwardId();
                break;
            }
            cursorVal += rateVal;
        }

        // 返回中奖结果
        return awardId;
    }

}
