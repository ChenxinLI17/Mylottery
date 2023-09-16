package fr.utc.mylottery.application.res;

import fr.utc.mylottery.common.Result;
import fr.utc.mylottery.domain.strategy.model.vo.DrawAwardInfo;

/**
 * 抽奖参与结果
 */
public class DrawProcessResult extends Result {
    private DrawAwardInfo drawAwardInfo;
    private Integer userTakeLeftCount;

    public DrawProcessResult(String code, String info) {
        super(code,info);
    }

    public DrawProcessResult(String code, String info, DrawAwardInfo drawAwardInfo) {
        super(code,info);
        this.drawAwardInfo = drawAwardInfo;
    }

    public DrawProcessResult(String code, String info, DrawAwardInfo drawAwardInfo, Integer userTakeLeftCount) {
        super(code, info);
        this.drawAwardInfo = drawAwardInfo;
        this.userTakeLeftCount = userTakeLeftCount;
    }

    public DrawAwardInfo getDrawAwardInfo() {
        return drawAwardInfo;
    }

    public void setDrawAwardInfo(DrawAwardInfo drawAwardInfo) {
        this.drawAwardInfo = drawAwardInfo;
    }
    public Integer getUserTakeLeftCount() {
        return userTakeLeftCount;
    }

    public void setUserTakeLeftCount(Integer userTakeLeftCount) {
        this.userTakeLeftCount = userTakeLeftCount;
    }
}
