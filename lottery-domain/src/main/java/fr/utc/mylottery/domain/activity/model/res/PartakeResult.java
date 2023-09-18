package fr.utc.mylottery.domain.activity.model.res;

import fr.utc.mylottery.common.Result;

/**
 * 活动参与结果
 */
public class PartakeResult extends Result {
    /** 策略ID */
    private Long strategyId;
    /** 活动领取ID */
    private Long takeId;
    /** 库存 */
    private Integer stockCount;
    /** activity 库存剩余 */
    private Integer stockSurplusCount;

    /** 用户领取活动的剩余次数 */
    private Integer userTakeLeftCount;


    public PartakeResult(String code, String info, Long takeId) {
        super(code, info);
        this.takeId = takeId;
    }

    public PartakeResult(String code, String info) {
        super(code, info);
    }

    public Long getStrategyId() {
        return strategyId;
    }

    public void setStrategyId(Long strategyId) {
        this.strategyId = strategyId;
    }
    public Long getTakeId() { return takeId; }

    public void setTakeId(Long takeId) { this.takeId = takeId;}
    public Integer getStockCount() {
        return stockCount;
    }

    public void setStockCount(Integer stockCount) {
        this.stockCount = stockCount;
    }
    public Integer getStockSurplusCount() {
        return stockSurplusCount;
    }

    public void setStockSurplusCount(Integer stockSurplusCount) {
        this.stockSurplusCount = stockSurplusCount;
    }
    public Integer getUserTakeLeftCount() {
        return userTakeLeftCount;
    }

    public void setUserTakeLeftCount(Integer userTakeLeftCount) {
        this.userTakeLeftCount = userTakeLeftCount;
    }

}
