package fr.utc.mylottery.domain.activity.repository.impl;


import fr.utc.mylottery.common.Constants;
import fr.utc.mylottery.dbrouter.strategy.IDBRouterStrategy;
import fr.utc.mylottery.domain.activity.model.req.PartakeReq;
import fr.utc.mylottery.domain.activity.model.res.StockResult;
import fr.utc.mylottery.domain.activity.model.vo.*;
import fr.utc.mylottery.domain.activity.repository.IActivityRepository;
import fr.utc.mylottery.infrastructure.dao.*;
import fr.utc.mylottery.infrastructure.po.*;
import fr.utc.mylottery.infrastructure.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component
public class ActivityRepository implements IActivityRepository {

    @Resource
    private IActivityDao activityDao;
    @Resource
    private IAwardDao awardDao;
    @Resource
    private IStrategyDao strategyDao;
    @Resource
    private IStrategyDetailDao strategyDetailDao;
    @Resource
    private IUserTakeActivityCountDao userTakeActivityCountDao;
    @Resource
    private IDBRouterStrategy dbRouterStrategy;
    @Resource
    private RedisUtil redisUtil;
    private Logger logger = LoggerFactory.getLogger(ActivityRepository.class);

    @Override
    public void addActivity(ActivityVO activity) {
        Activity req = new Activity();
        BeanUtils.copyProperties(activity, req);
        activityDao.insert(req);
    }

    @Override
    public void addAward(List<AwardVO> awardList) {
        List<Award> req = new ArrayList<>();
        for (AwardVO awardVO : awardList) {
            Award award = new Award();
            BeanUtils.copyProperties(awardVO, award);
            req.add(award);
        }
        awardDao.insertList(req);
    }

    @Override
    public void addStrategy(StrategyVO strategy) {
        Strategy req = new Strategy();
        BeanUtils.copyProperties(strategy, req);
        /**
         * 如果之间存在名称不相同的属性，则BeanUtils不对这些属性进行处理
         */
        strategyDao.insert(req);
    }

    @Override
    public void addStrategyDetailList(List<StrategyDetailVO> strategyDetailList) {
        List<StrategyDetail> req = new ArrayList<>();
        for (StrategyDetailVO strategyDetailVO : strategyDetailList) {
            StrategyDetail strategyDetail = new StrategyDetail();
            BeanUtils.copyProperties(strategyDetailVO, strategyDetail);
            req.add(strategyDetail);
        }
        strategyDetailDao.insertList(req);
    }

    @Override
    public boolean alterStatus(Long activityId, Enum<Constants.ActivityState> beforeState, Enum<Constants.ActivityState> afterState) {
        AlterStateVO alterStateVO = new AlterStateVO(activityId,((Constants.ActivityState) beforeState).getCode(),((Constants.ActivityState) afterState).getCode());
        int count = activityDao.alterState(alterStateVO.getActivityId(),alterStateVO.getBeforeState(),alterStateVO.getAfterState());
        return 1 == count;
    }

    @Override
    public Long queryStrategyIdByActivityId(Long activityId) {
        return activityDao.queryActivityById(activityId).getStrategyId();
    }

    @Override
    public ActivityBillVO queryActivityBill(PartakeReq req) {

        logger.info("req.act:{}",req.getActivityId());
        /** 查询活动信息 */
        Activity activity = activityDao.queryActivityById(req.getActivityId());

        dbRouterStrategy.doRouter(req.getuId());
        /** 查询领取次数 */
        UserTakeActivityCount userTakeActivityCountReq = new UserTakeActivityCount();
        userTakeActivityCountReq.setuId(req.getuId());
        userTakeActivityCountReq.setActivityId(req.getActivityId());
        UserTakeActivityCount userTakeActivityCount = userTakeActivityCountDao.queryUserTakeActivityCount(userTakeActivityCountReq);

        dbRouterStrategy.clear();

        /** 封装结果信息 */
        ActivityBillVO activityBillVO = new ActivityBillVO();
        activityBillVO.setuId(req.getuId());
        activityBillVO.setActivityId(req.getActivityId());
        activityBillVO.setActivityName(activity.getActivityName());
        activityBillVO.setBeginDateTime(activity.getBeginDateTime());
        activityBillVO.setEndDateTime(activity.getEndDateTime());
        activityBillVO.setTakeCount(activity.getTakeCount());
        activityBillVO.setStockCount(activity.getStockCount());
        activityBillVO.setStockSurplusCount(activity.getStockSurplusCount());
        activityBillVO.setStrategyId(activity.getStrategyId());
        activityBillVO.setState(activity.getState());
        activityBillVO.setUserTakeLeftCount(null == userTakeActivityCount ? null : userTakeActivityCount.getLeftCount());

        return activityBillVO;
    }
    @Override
    public int subtractionActivityStock(Long activityId) {
        return activityDao.subtractionActivityStock(activityId);
    }

    @Override
    public StockResult subtractionActivityStockByRedis(String uId, Long activityId, Integer stockCount) {
        //  1. 获取抽奖活动库存 Key
        String stockKey = Constants.RedisKey.KEY_LOTTERY_ACTIVITY_STOCK_COUNT(activityId);

        // 2. 增加目前已占用的库存数
        Integer stockUsedCount = (int) redisUtil.incr(stockKey, 1);
        logger.info("Key "+stockKey+" stockUsedCount: "+stockUsedCount);

        // 3. 判断目前已占用的库存数是否超出活动的总库存，进行恢复原始库存
        if (stockUsedCount > stockCount) {
            redisUtil.decr(stockKey, 1);
            return new StockResult(Constants.ResponseCode.OUT_OF_STOCK.getCode(), Constants.ResponseCode.OUT_OF_STOCK.getInfo());
        }

        // 4. 以活动库存占用编号，生成对应加锁Key，细化锁的颗粒度
        String stockTokenKey = Constants.RedisKey.KEY_LOTTERY_ACTIVITY_STOCK_COUNT_TOKEN(activityId, stockUsedCount);

        // 5. 使用 Redis.setNx 加一个分布式锁
        boolean lockToken = redisUtil.setNx(stockTokenKey, 350L);
        if (!lockToken) {
            logger.info("抽奖活动{}用户秒杀{}扣减库存，分布式锁失败：{}", activityId, uId, stockTokenKey);
            return new StockResult(Constants.ResponseCode.ERR_TOKEN.getCode(), Constants.ResponseCode.ERR_TOKEN.getInfo());
        }

        return new StockResult(Constants.ResponseCode.SUCCESS.getCode(), Constants.ResponseCode.SUCCESS.getInfo(), stockTokenKey, stockCount - stockUsedCount);
    }
    @Override
    public void recoverActivityCacheStockByRedis(Long activityId, String tokenKey, String code) {
        // 删除分布式锁 Key
        redisUtil.del(tokenKey);
    }



}

