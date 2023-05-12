package fr.utc.mylottery.domain.strategy.service.algorithm.impl;

import fr.utc.mylottery.domain.strategy.service.algorithm.BaseAlgorithm;
import fr.utc.mylottery.domain.strategy.model.vo.AwardRateInfo;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

@Component("defaultRateRandomDrawAlgorithm")
public class DefaultRateRandomDrawAlgorithm extends BaseAlgorithm {
    //返回抽到的奖品ID
    @Override
    public String randomDraw(Long strategyId, List<String> excludeAwardIds) {

        BigDecimal differenceDenominator = BigDecimal.ZERO;//排除已经中奖的奖品的概率
        //比如三个奖品a,b,c，中奖概率分别是0.2,0.3,0.5,若c已被抽中，differenceDenominator = 0.2 + 0.3

        // 排除掉不在抽奖范围的奖品ID集合
        List<AwardRateInfo> differenceAwardRateList = new ArrayList<>();
        List<AwardRateInfo> awardRateIntervalValList = awardRateInfoMap.get(strategyId);
        for (AwardRateInfo awardRateInfo : awardRateIntervalValList) {
            String awardId = awardRateInfo.getAwardId();
            if (excludeAwardIds.contains(awardId)) {
                continue;
            }
            differenceAwardRateList.add(awardRateInfo);
            differenceDenominator = differenceDenominator.add(awardRateInfo.getAwardRate());
        }

        // 前置判断
        if (differenceAwardRateList.size() == 0) return "";
        if (differenceAwardRateList.size() == 1) return differenceAwardRateList.get(0).getAwardId();

        // 获取随机概率值
        SecureRandom secureRandom = new SecureRandom();
        int randomVal = secureRandom.nextInt(100) + 1; // 生成1-100的随机数
        //random.nextInt(range+1)获取指定范围的随机数
        //比如range=9，说明生成的随机数在0~8中产生

        // 循环获取奖品
        String awardId = "";
        int cursorVal = 0;
        for (AwardRateInfo awardRateInfo : differenceAwardRateList) {
            int rateVal = awardRateInfo.getAwardRate()
                    .divide(differenceDenominator, 2, RoundingMode.UP)
                    .multiply(new BigDecimal(100)).intValue();
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
