package fr.utc.mylottery.rpc;

import fr.utc.mylottery.rpc.req.LotReq;
import fr.utc.mylottery.rpc.res.LotRes;

public interface ILotBooth {
    LotRes drawalg(LotReq req);
}
