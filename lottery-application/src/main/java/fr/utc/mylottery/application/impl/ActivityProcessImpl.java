package fr.utc.mylottery.application.impl;

import fr.utc.mylottery.application.IActivityProcess;
import fr.utc.mylottery.application.mq.producer.KafkaProducer;
import fr.utc.mylottery.application.req.DrawProcessReq;
import fr.utc.mylottery.application.res.DrawProcessResult;
import fr.utc.mylottery.common.Constants;
import fr.utc.mylottery.common.Result;
import fr.utc.mylottery.domain.activity.model.req.PartakeReq;
import fr.utc.mylottery.domain.activity.model.res.PartakeResult;
import fr.utc.mylottery.domain.activity.model.vo.ActivityPartakeVO;
import fr.utc.mylottery.domain.activity.model.vo.DrawOrderVO;
import fr.utc.mylottery.domain.activity.model.vo.InvoiceVO;
import fr.utc.mylottery.domain.activity.service.partake.IActivityPartake;
import fr.utc.mylottery.domain.strategy.model.req.DrawReq;
import fr.utc.mylottery.domain.strategy.model.res.DrawResult;
import fr.utc.mylottery.domain.strategy.model.vo.DrawAwardVO;
import fr.utc.mylottery.domain.strategy.service.draw.IDrawExec;
import fr.utc.mylottery.domain.support.ids.IIdGenerator;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @description: 活动抽奖流程编排
 */
@Service
public class ActivityProcessImpl implements IActivityProcess {
    @Resource
    private IActivityPartake activityPartake;

    @Resource
    private IDrawExec drawExec;

    @Resource
    private Map<Constants.Ids, IIdGenerator> idGeneratorMap;
    @Resource
    private KafkaProducer kafkaProducer;

    @Override
    public DrawProcessResult doDrawProcess(DrawProcessReq req) {
        // 1. 领取活动
        PartakeResult partakeResult = activityPartake.doPartake(new PartakeReq(req.getuId(), req.getActivityId()));
        if (!Constants.ResponseCode.SUCCESS.getCode().equals(partakeResult.getCode())&& !Constants.ResponseCode.NOT_CONSUMED_TAKE.getCode().equals(partakeResult.getCode())) {
            return new DrawProcessResult(partakeResult.getCode(), partakeResult.getInfo());
        }
        // 2. 首次成功领取活动，发送 MQ 消息
        if (Constants.ResponseCode.SUCCESS.getCode().equals(partakeResult.getCode())) {
            ActivityPartakeVO activityPartake = new ActivityPartakeVO();
            activityPartake.setuId(req.getuId());
            activityPartake.setActivityId(req.getActivityId());
            activityPartake.setStockCount(partakeResult.getStockCount());
            activityPartake.setStockSurplusCount(partakeResult.getStockSurplusCount());
            // 发送 MQ 消息
            kafkaProducer.sendLotteryActivityPartake(activityPartake);
        }

        Long strategyId = partakeResult.getStrategyId();
        Long takeId = partakeResult.getTakeId();
        Integer userTakeLeftCount = partakeResult.getUserTakeLeftCount();

        // 2. 执行抽奖
        DrawResult drawResult = drawExec.doDrawExec(new DrawReq(req.getuId(), strategyId, takeId));
        if (Constants.DrawState.FAIL.getCode().equals(drawResult.getDrawState())) {
            return new DrawProcessResult(Constants.ResponseCode.LOSING_DRAW.getCode(), Constants.ResponseCode.LOSING_DRAW.getInfo());
        }
        DrawAwardVO drawAwardVO = drawResult.getDrawAwardInfo();

        // 3. 结果落库
        DrawOrderVO drawOrderVO = buildDrawOrderVO(req, strategyId, takeId, drawAwardVO);
        Result recordResult = activityPartake.recordDrawOrder(drawOrderVO);
        if (!Constants.ResponseCode.SUCCESS.getCode().equals(recordResult.getCode())) {
            return new DrawProcessResult(recordResult.getCode(), recordResult.getInfo());
        }

        // 4. 发送MQ，触发发奖流程
        InvoiceVO invoiceVO = buildInvoiceVO(drawOrderVO);
        ListenableFuture<SendResult<String, Object>> future = kafkaProducer.sendLotteryInvoice(invoiceVO);
        future.addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {

            @Override
            public void onSuccess(SendResult<String, Object> stringObjectSendResult) {
                // 4.1 MQ 消息发送完成，更新数据库表 user_strategy_export.mq_state = 1
                activityPartake.updateInvoiceMqState(invoiceVO.getuId(), invoiceVO.getOrderId(), Constants.MQState.COMPLETE.getCode());
            }

            @Override
            public void onFailure(Throwable throwable) {
                // 4.2 MQ 消息发送失败，更新数据库表 user_strategy_export.mq_state = 2 【等待定时任务扫码补偿MQ消息】
                activityPartake.updateInvoiceMqState(invoiceVO.getuId(), invoiceVO.getOrderId(), Constants.MQState.FAIL.getCode());
            }

        });
        // 5. 返回结果
        return new DrawProcessResult(Constants.ResponseCode.SUCCESS.getCode(), Constants.ResponseCode.SUCCESS.getInfo(), drawAwardVO,userTakeLeftCount-1);
    }

    private DrawOrderVO buildDrawOrderVO(DrawProcessReq req, Long strategyId, Long takeId, DrawAwardVO drawAwardVO) {
        long orderId = idGeneratorMap.get(Constants.Ids.SnowFlake).nextId();
        DrawOrderVO drawOrderVO = new DrawOrderVO();
        drawOrderVO.setuId(req.getuId());
        drawOrderVO.setTakeId(takeId);
        drawOrderVO.setActivityId(req.getActivityId());
        drawOrderVO.setOrderId(orderId);
        drawOrderVO.setStrategyId(strategyId);
        drawOrderVO.setAwardId(drawAwardVO.getAwardId());
        drawOrderVO.setAwardType(drawAwardVO.getAwardType());
        drawOrderVO.setAwardName(drawAwardVO.getAwardName());
        drawOrderVO.setAwardContent(drawAwardVO.getAwardContent());
        return drawOrderVO;
    }
    private InvoiceVO buildInvoiceVO(DrawOrderVO drawOrderVO) {
        InvoiceVO invoiceVO = new InvoiceVO();
        invoiceVO.setuId(drawOrderVO.getuId());
        invoiceVO.setOrderId(drawOrderVO.getOrderId());
        invoiceVO.setAwardId(drawOrderVO.getAwardId());
        invoiceVO.setAwardType(drawOrderVO.getAwardType());
        invoiceVO.setAwardName(drawOrderVO.getAwardName());
        invoiceVO.setAwardContent(drawOrderVO.getAwardContent());
        invoiceVO.setShippingAddress(null);
        invoiceVO.setExtInfo(null);
        return invoiceVO;
    }
}
