package fr.utc.mylottery.domain.award.service.goods.impl;

import fr.utc.mylottery.common.Constants;
import fr.utc.mylottery.domain.award.model.req.GoodsReq;
import fr.utc.mylottery.domain.award.model.res.DistributionRes;
import fr.utc.mylottery.domain.award.service.goods.DistributionBase;
import fr.utc.mylottery.domain.award.service.goods.IDistributionGoods;
import org.springframework.stereotype.Component;

@Component
public class DescGoods extends DistributionBase implements IDistributionGoods {

    @Override
    public DistributionRes doDistribution(GoodsReq req) {

        // 模拟发放接口
        logger.info("模拟文字描述奖品发放接口 uId：{} awardContent：{}", req.getuId(), req.getAwardContent());

        super.updateUserAwardState(req.getuId(), req.getOrderId(), req.getAwardId(), Constants.GrantState.COMPLETE.getCode());

        return new DistributionRes(req.getuId(), Constants.AwardState.SUCCESS.getCode(), Constants.AwardState.SUCCESS.getInfo());
    }

    @Override
    public Integer getDistributionGoodsName() {
        return Constants.AwardType.DESC.getCode();
    }

}