package fr.utc.mylottery.interfaces;

import fr.utc.mylottery.common.Constants;
import fr.utc.mylottery.common.Result;
import fr.utc.mylottery.domain.strategy.model.req.DrawReq;
import fr.utc.mylottery.domain.strategy.model.res.DrawResult;
import fr.utc.mylottery.domain.strategy.service.draw.IDrawExec;
import fr.utc.mylottery.rpc.IActivityBooth;
import fr.utc.mylottery.rpc.ILotBooth;
import fr.utc.mylottery.rpc.dto.LotDto;
import fr.utc.mylottery.rpc.req.LotReq;
import fr.utc.mylottery.rpc.res.LotRes;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
@Service@org.apache.dubbo.config.annotation.Service(interfaceClass = ILotBooth.class, version = "1.0.0")
public class LotBooth implements ILotBooth {
    @Resource
    private IDrawExec drawExec;

    @Override
    public LotRes drawalg(LotReq req){
        DrawResult result = drawExec.doDrawExec(new DrawReq(req.getUserId(),req.getStrategyId()));
        LotDto lotDto = new LotDto();
        lotDto.setAwardId(result.getDrawAwardInfo().getAwardId());
        lotDto.setAwardName(result.getDrawAwardInfo().getAwardName());
        lotDto.setStrategyId(result.getStrategyId());
        lotDto.setUserId(result.getuId());
        return new LotRes(new Result(Constants.ResponseCode.SUCCESS.getCode(), Constants.ResponseCode.SUCCESS.getInfo()), lotDto);
    }
}
