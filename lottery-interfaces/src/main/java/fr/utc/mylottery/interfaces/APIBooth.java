package fr.utc.mylottery.interfaces;

import com.alibaba.fastjson.JSON;
import fr.utc.mylottery.application.IActivityProcess;
import fr.utc.mylottery.application.req.DrawProcessReq;
import fr.utc.mylottery.application.res.DrawProcessResult;
import fr.utc.mylottery.common.Constants;
import fr.utc.mylottery.common.Result;
import fr.utc.mylottery.domain.activity.repository.impl.ActivityRepository;
import fr.utc.mylottery.domain.award.repository.impl.AwardRepository;
import fr.utc.mylottery.domain.strategy.model.aggregates.StrategyRich;
import fr.utc.mylottery.domain.strategy.repository.impl.StrategyRepository;
import fr.utc.mylottery.infrastructure.po.StrategyDetail;
import fr.utc.mylottery.rpc.IAPIBooth;
import fr.utc.mylottery.rpc.dto.DrawDto;
import fr.utc.mylottery.rpc.dto.InitDto;
import fr.utc.mylottery.rpc.req.DrawReq;
import fr.utc.mylottery.rpc.req.InitReq;
import fr.utc.mylottery.rpc.res.DrawRes;
import fr.utc.mylottery.rpc.res.InitRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/** API 接口实现 */
@Service@org.apache.dubbo.config.annotation.Service(interfaceClass = IAPIBooth.class, version = "1.0.0")
public class APIBooth implements IAPIBooth {
    private Logger logger = LoggerFactory.getLogger(APIBooth.class);
    @Resource
    private IActivityProcess activityProcess;
    @Resource
    private StrategyRepository strategyRepository;
    @Resource
    private ActivityRepository activityRepository;
    @Resource
    private AwardRepository awardRepository;
    @Override
    public InitRes initialize(InitReq initReq) {
        Long strategyId = activityRepository.queryStrategyIdByActivityId(initReq.getActivityId());
        StrategyRich strategyRich =strategyRepository.queryStrategyRich(strategyId);
        List<String> awards = new ArrayList<>();
        for(StrategyDetail strategyDetail:strategyRich.getStrategyDetailList()){
            awards.add(awardRepository.queryAwardInfo(strategyDetail.getAwardId()).getAwardName());
        }
        InitDto initDto = new InitDto(awards);
        logger.info("测试结果：{}", JSON.toJSONString(initDto.getAwardName()));
        return new InitRes(new Result(Constants.ResponseCode.SUCCESS.getCode(), Constants.ResponseCode.SUCCESS.getInfo()), initDto);
    }
    @Override
    public DrawRes doDraw(DrawReq drawReq) throws ExecutionException, InterruptedException {
        DrawProcessReq req = new DrawProcessReq(drawReq.getuId(), drawReq.getActivityId());
        DrawProcessResult result = activityProcess.doDrawProcess(req);
        if (!Constants.ResponseCode.SUCCESS.getCode().equals(result.getCode())) {
            return new DrawRes(new Result(result.getCode(),result.getInfo()),null);
        }
        DrawDto drawDto = new DrawDto();
        drawDto.setAwardId(result.getDrawAwardInfo().getAwardId());
        drawDto.setAwardType(result.getDrawAwardInfo().getAwardType());
        drawDto.setAwardName(result.getDrawAwardInfo().getAwardName());
        drawDto.setAwardContent(result.getDrawAwardInfo().getAwardContent());
        drawDto.setGrantDate(result.getDrawAwardInfo().getGrantDate());
        drawDto.setUserTakeLeftCount(result.getUserTakeLeftCount());
        logger.info("请求入参：{}", JSON.toJSONString(req));
        logger.info("测试结果：{}", JSON.toJSONString(drawDto));
        return new DrawRes(new Result(result.getCode(),result.getInfo()), drawDto);
    }

}
