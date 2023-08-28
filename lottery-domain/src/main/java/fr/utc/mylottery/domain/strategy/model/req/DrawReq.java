package fr.utc.mylottery.domain.strategy.model.req;

public class DrawReq {
    /** 用户ID */
    private String uId;

    /** 策略ID*/
    private Long strategyId;

    /** 领取动作ID */
    private Long takeId;
    public DrawReq(String uId, Long strategyId) {
        this.uId = uId;
        this.strategyId = strategyId;
    }

    public DrawReq(String uId, Long strategyId, Long takeId) {
        this.uId = uId;
        this.strategyId = strategyId;
        this.takeId = takeId;
    }


    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public Long getStrategyId() {
        return strategyId;
    }

    public void setStrategyId(Long strategyId) {
        this.strategyId = strategyId;
    }

    public Long getTakeId() {
        return takeId;
    }

    public void setTakeId(Long takeId) {
        this.takeId = takeId;
    }
}
