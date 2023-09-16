package fr.utc.mylottery.rpc.req;

import java.io.Serializable;

public class InitReq implements Serializable {
    private Long activityId;
    public InitReq(Long activityId) { this.activityId = activityId; }
    public Long getActivityId() { return activityId; }
    public void setActivityId(Long activityId) { this.activityId = activityId; }

}
