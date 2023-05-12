package fr.utc.mylottery.rpc.req;

import java.io.Serializable;

public class LotReq implements Serializable {
    private String userId;
    private Long strategyId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getStrategyId() {
        return strategyId;
    }

    public void setStrategyId(Long strategyId) {
        this.strategyId = strategyId;
    }
}
