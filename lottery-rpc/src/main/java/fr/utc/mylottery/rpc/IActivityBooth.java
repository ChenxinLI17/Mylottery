package fr.utc.mylottery.rpc;

import fr.utc.mylottery.rpc.req.ActivityReq;
import fr.utc.mylottery.rpc.res.ActivityRes;
/** 给外界提供的接口描述 */
public interface IActivityBooth {
    ActivityRes queryActivityById(ActivityReq req);
}
