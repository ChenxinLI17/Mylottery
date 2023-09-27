package fr.utc.mylottery.rpc;

import fr.utc.mylottery.rpc.req.DrawReq;
import fr.utc.mylottery.rpc.req.InitReq;
import fr.utc.mylottery.rpc.res.DrawRes;
import fr.utc.mylottery.rpc.res.InitRes;

import java.util.concurrent.ExecutionException;

public interface IAPIBooth {
    DrawRes doDraw(DrawReq drawReq) throws ExecutionException, InterruptedException;
    InitRes initialize(InitReq initReq);
}
