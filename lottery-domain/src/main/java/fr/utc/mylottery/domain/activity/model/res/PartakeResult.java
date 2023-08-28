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
}
