package fr.utc.mylottery.rpc;

import fr.utc.mylottery.rpc.req.DrawReq;
import fr.utc.mylottery.rpc.res.DrawRes;

public interface IDrawBooth {
    DrawRes drawalg(DrawReq req);
}
