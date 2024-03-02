package fr.utc.mylottery.test;

import com.alibaba.fastjson.JSON;
import fr.utc.mylottery.common.Constants;
import fr.utc.mylottery.domain.award.model.req.GoodsReq;
import fr.utc.mylottery.domain.award.model.res.DistributionRes;
import fr.utc.mylottery.domain.award.service.factory.DistributionGoodsFactory;
import fr.utc.mylottery.domain.award.service.goods.IDistributionGoods;
import fr.utc.mylottery.domain.strategy.model.req.DrawReq;
import fr.utc.mylottery.domain.strategy.model.res.DrawResult;
import fr.utc.mylottery.domain.strategy.model.vo.DrawAwardVO;
import fr.utc.mylottery.domain.strategy.service.draw.IDrawExec;
import fr.utc.mylottery.domain.support.ids.IIdGenerator;
import fr.utc.mylottery.infrastructure.dao.IActivityDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringRunnerTest {
    private Logger logger = LoggerFactory.getLogger(SpringRunnerTest.class);

    @Resource
    private IActivityDao activityDao;

    @Resource
    private IDrawExec drawExec;
    @Resource
    private DistributionGoodsFactory distributionGoodsFactory;
    @Resource
    private IIdGenerator snowflake;
    //成功
    @Test
    public void test_drawExec() {
        drawExec.doDrawExec(new DrawReq("小傅哥", 10001L));
        drawExec.doDrawExec(new DrawReq("小佳佳", 10001L));
        drawExec.doDrawExec(new DrawReq("小蜗牛", 10001L));
        drawExec.doDrawExec(new DrawReq("八杯水", 10001L));
    }
    //success
    @Test
    public void test_award() {
        // 执行抽奖
        DrawResult drawResult = drawExec.doDrawExec(new DrawReq("chenxinli", 10001L));

        // 判断抽奖结果
        Integer drawState = drawResult.getDrawState();
        if (Constants.DrawState.FAIL.getCode().equals(drawState)) {
            logger.info("未中奖 DrawAwardInfo is null");
            return;
        }

        // 封装发奖参数，orderId：2109313442431 为模拟ID，需要在用户参与领奖活动时生成
        DrawAwardVO drawAwardVO = drawResult.getDrawAwardInfo();
        GoodsReq goodsReq = new GoodsReq(drawResult.getuId(), 2109313442431L, drawAwardVO.getAwardId(), drawAwardVO.getAwardName(), drawAwardVO.getAwardContent());

        System.out.println(drawAwardVO.getAwardType());
        // 根据 awardType 从抽奖工厂中获取对应的发奖服务
        IDistributionGoods distributionGoodsService = distributionGoodsFactory.getDistributionGoodsService(drawAwardVO.getAwardType());
        DistributionRes distributionRes = distributionGoodsService.doDistribution(goodsReq);

        logger.info("测试结果：{}", JSON.toJSONString(distributionRes));
    }
    @Test
    public void test_award1() {
        // 执行抽奖
        DrawResult drawResult = drawExec.doDrawExec(new DrawReq("chenxinli", 10001L));

        // 判断抽奖结果
        Integer drawState = drawResult.getDrawState();
        if (Constants.DrawState.FAIL.getCode().equals(drawState)) {
            logger.info("未中奖 DrawAwardInfo is null");
            return;
        }
        DrawAwardVO drawAwardVO = drawResult.getDrawAwardInfo();
        Long orderId =snowflake.nextId();
        GoodsReq goodsReq = new GoodsReq(drawResult.getuId(), orderId, drawAwardVO.getAwardId(), drawAwardVO.getAwardName(), drawAwardVO.getAwardContent());

        System.out.println(drawAwardVO.getAwardType());
        // 根据 awardType 从抽奖工厂中获取对应的发奖服务
        IDistributionGoods distributionGoodsService = distributionGoodsFactory.getDistributionGoodsService(drawAwardVO.getAwardType());
        DistributionRes distributionRes = distributionGoodsService.doDistribution(goodsReq);

        logger.info("订单号：{}", orderId);
        logger.info("测试结果：{}", JSON.toJSONString(distributionRes));
    }
}
