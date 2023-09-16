package fr.utc.mylottery.rpc;

import fr.utc.mylottery.rpc.req.DrawReq;
import fr.utc.mylottery.rpc.req.InitReq;
import fr.utc.mylottery.rpc.res.DrawRes;
import fr.utc.mylottery.rpc.res.InitRes;

public interface IAPIBooth {
    DrawRes doDraw(DrawReq drawReq);
    InitRes initialize(InitReq initReq);
}
