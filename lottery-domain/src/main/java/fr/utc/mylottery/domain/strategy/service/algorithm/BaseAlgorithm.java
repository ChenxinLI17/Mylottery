package fr.utc.mylottery.domain.strategy.service.algorithm;

import fr.utc.mylottery.domain.strategy.model.vo.AwardRateInfo;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class BaseAlgorithm implements IDrawAlgorithm {

    /** 斐波那契散列增量，逻辑：黄金分割点：(√5 - 1) / 2 = 0.6180339887，Math.pow(2, 32) * 0.6180339887 = 0x61c88647 */
    private final int HASH_INCREMENT = 0x61c88647;

    /** 数组初始化长度 128，保证数据填充时不发生碰撞的最小初始化值 */
    private final int RATE_TUPLE_LENGTH = 128;

    /** 存放概率与奖品对应的散列结果，strategyId -> rateTuple */
    protected Map<Long, String[]> rateTupleMap = new ConcurrentHashMap<>();

    /** 奖品区间概率值，strategyId -> [awardId->begin、awardId->end] */
    protected Map<Long, List<AwardRateInfo>> awardRateInfoMap = new ConcurrentHashMap<>();

    /**
     * 程序启动时初始化概率元祖，在初始化完成后使用过程中不允许修改元祖数据
     * <p>
     * 元祖数据作用在于讲百分比内(0.2、0.3、0.5)的数据，转换为一整条数组上分区数据，如下；
     * 0.2 = 0 ~ 0.2
     * 0.3 = 0 + 0.2 ~ 0.2 + 0.3 = 0.2 ~ 0.5
     * 0.5 = 0.5 ~ 1 （计算方式同上）
     * <p>
     * 通过数据拆分为整条后，再根据0-100中各个区间的奖品信息，使用斐波那契散列计算出索引位置，把奖品数据存放到元祖中。比如：
     * <p>
     * 1. 把 0.2 转换为 20
     * 2. 20 对应的斐波那契值哈希值：（20 * HASH_INCREMENT + HASH_INCREMENT）= -1549107828 HASH_INCREMENT = 0x61c88647
     * 3. 再通过哈希值计算索引位置：hashCode & (rateTuple.length - 1) = 12
     * 4. 那么tup[14] = 0.2 中奖概率对应的奖品
     * 5. 当后续通过随机数获取到1-100的值后，可以直接定位到对应的奖品信息，通过这样的方式把轮训算奖的时间复杂度从O(n) 降低到 0(1)
     *
     * @param strategyId        策略ID
     * @param awardRateInfoList 奖品概率配置集合 「值示例：AwardRateInfo.awardRate = 0.04」
     */

    @Override
    public void initRateTuple(Long strategyId, List<AwardRateInfo> awardRateInfoList) {
        // 保存奖品概率信息
        awardRateInfoMap.put(strategyId, awardRateInfoList);

        //map.computeIfAbsent 如果key不存在，即当前strategyId没有保存在rateTupleMap中，创建一个String[]存入map，
        //并返回。
        //对返回的rateTuple操作就是对map中的那个String[]操作
        String[] rateTuple = rateTupleMap.computeIfAbsent(strategyId, k -> new String[RATE_TUPLE_LENGTH]);

        int cursorVal = 0;
        for (AwardRateInfo awardRateInfo : awardRateInfoList) {

            //中奖概率awardRate是0.2 => rateVal是20 BigDecimal类型的高精度计算multiply
            int rateVal = awardRateInfo.getAwardRate().multiply(new BigDecimal(100)).intValue();

            //循环填充概率范围值
            //数组索引均匀散落
            for (int i = cursorVal + 1; i <= (rateVal + cursorVal); i++) {
                rateTuple[hashIdx(i)] = awardRateInfo.getAwardId();
            }
            cursorVal += rateVal;

        }
    }
    /**
     * 判断是否已经，做了数据初始化
     * @param strategyId
     * @return
     */
    @Override
    public boolean isExistRateTuple(Long strategyId) {
        return rateTupleMap.containsKey(strategyId);
    }

    /**
     * 斐波那契（Fibonacci）散列法，计算哈希索引下标值
     *
     * @param val 值
     * @return 索引
     */
    protected int hashIdx(int val) {
        int hashCode = val * HASH_INCREMENT + HASH_INCREMENT;
        return hashCode & (RATE_TUPLE_LENGTH - 1);
    }

    protected int generateSecureRandomIntCode(int bound){
        return new SecureRandom().nextInt(bound) + 1;
    }
    //random.nextInt(range+1)获取指定范围的随机数
    //比如range=9，说明生成的随机数在0~8中产生
}
