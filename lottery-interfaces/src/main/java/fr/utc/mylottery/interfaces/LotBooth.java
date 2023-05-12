package fr.utc.mylottery.interfaces;

import fr.utc.mylottery.domain.strategy.service.draw.IDrawExec;
import fr.utc.mylottery.rpc.IDrawBooth;
import fr.utc.mylottery.rpc.req.DrawReq;
import fr.utc.mylottery.rpc.res.DrawRes;

import javax.annotation.Resource;

public class DrawBooth implements IDrawBooth {
    @Resource
    private IDrawExec drawExec;

    @Override
    public DrawRes drawRes(DrawReq req){
        drawExec.doDrawExec(req.getUserId(),req.getStrategyId());

    }
}
