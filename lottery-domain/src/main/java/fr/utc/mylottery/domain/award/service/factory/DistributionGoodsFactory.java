package fr.utc.mylottery.domain.award.service.factory;

import fr.utc.mylottery.domain.award.service.goods.IDistributionGoods;
import org.springframework.stereotype.Service;

/**
 * 配送商品简单工厂，提供获取配送服务
 */
@Service
public class DistributionGoodsFactory extends GoodsConfig{
    public IDistributionGoods getDistributionGoodsService(Integer awardType){
        return goodsMap.get(awardType);
    }
}
