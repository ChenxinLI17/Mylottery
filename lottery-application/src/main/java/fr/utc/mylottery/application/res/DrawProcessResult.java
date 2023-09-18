package fr.utc.mylottery.application.res;

import fr.utc.mylottery.common.Result;
import fr.utc.mylottery.domain.strategy.model.vo.DrawAwardVO;

/**
 * 抽奖参与结果
 */
public class DrawProcessResult extends Result {
    private DrawAwardVO drawAwardVO;
    private Integer userTakeLeftCount;

    public DrawProcessResult(String code, String info) {
        super(code,info);
    }

    public DrawProcessResult(String code, String info, DrawAwardVO drawAwardVO) {
        super(code,info);
        this.drawAwardVO = drawAwardVO;
    }

    public DrawProcessResult(String code, String info, DrawAwardVO drawAwardVO, Integer userTakeLeftCount) {
        super(code, info);
        this.drawAwardVO = drawAwardVO;
        this.userTakeLeftCount = userTakeLeftCount;
    }

    public DrawAwardVO getDrawAwardInfo() {
        return drawAwardVO;
    }

    public void setDrawAwardInfo(DrawAwardVO drawAwardVO) {
        this.drawAwardVO = drawAwardVO;
    }
    public Integer getUserTakeLeftCount() {
        return userTakeLeftCount;
    }

    public void setUserTakeLeftCount(Integer userTakeLeftCount) {
        this.userTakeLeftCount = userTakeLeftCount;
    }
}
