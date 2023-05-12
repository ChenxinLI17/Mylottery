package fr.utc.mylottery.rpc.req;

import java.io.Serializable;

public class DrawReq implements Serializable {
    private Long userId;
    private Long strategyId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getStrategyId() {
        return strategyId;
    }

    public void setStrategyId(Long strategyId) {
        this.strategyId = strategyId;
    }
}
