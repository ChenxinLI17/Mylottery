package fr.utc.mylottery.rpc;

import fr.utc.mylottery.rpc.req.ActivityReq;
import fr.utc.mylottery.rpc.res.ActivityRes;

public interface IActivityBooth {//给外界提供的接口描述
    ActivityRes queryActivityById(ActivityReq req);
}
