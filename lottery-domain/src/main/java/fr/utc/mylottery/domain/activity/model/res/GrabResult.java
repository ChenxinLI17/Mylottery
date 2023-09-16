package fr.utc.mylottery.domain.activity.model.res;

import fr.utc.mylottery.common.Result;

public class GrabResult extends Result {
    /** 用户领取活动的剩余次数 */
    private Integer userTakeLeftCount;
    public GrabResult(String code, String info, Integer userTakeLeftCount) {
        super(code, info);
        this.userTakeLeftCount = userTakeLeftCount;
    }
    public GrabResult(Result result, Integer userTakeLeftCount) {
        super(result.getCode(), result.getInfo());
        this.userTakeLeftCount = userTakeLeftCount;
    }

    public Integer getUserTakeLeftCount() {
        return userTakeLeftCount;
    }

    public void setUserTakeLeftCount(Integer userTakeLeftCount) {
        this.userTakeLeftCount = userTakeLeftCount;
    }
}
