package fr.utc.mylottery.domain.award.service.factory;

import fr.utc.mylottery.common.Constants;
import fr.utc.mylottery.domain.award.service.goods.IDistributionGoods;
import fr.utc.mylottery.domain.award.service.goods.impl.CouponGoods;
import fr.utc.mylottery.domain.award.service.goods.impl.DescGoods;
import fr.utc.mylottery.domain.award.service.goods.impl.PhysicalGoods;
import fr.utc.mylottery.domain.award.service.goods.impl.RedeemCodeGoods;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GoodsConfig {
    /** 奖品发放策略组 */
    protected static Map<Integer, IDistributionGoods> goodsMap = new ConcurrentHashMap<>();

    @Resource
    private DescGoods descGoods;

    @Resource
    private RedeemCodeGoods redeemCodeGoods;

    @Resource
    private CouponGoods couponGoods;

    @Resource
    private PhysicalGoods physicalGoods;

    @PostConstruct
    //用于指定在对象被创建之后立即执行的方法
    public void init() {
        goodsMap.put(Constants.AwardType.DESC.getCode(), descGoods);
        goodsMap.put(Constants.AwardType.RedeemCodeGoods.getCode(), redeemCodeGoods);
        goodsMap.put(Constants.AwardType.CouponGoods.getCode(), couponGoods);
        goodsMap.put(Constants.AwardType.PhysicalGoods.getCode(), physicalGoods);
    }
}
