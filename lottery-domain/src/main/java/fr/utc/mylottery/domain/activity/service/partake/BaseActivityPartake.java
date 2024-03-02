package fr.utc.mylottery.domain.activity.service.partake;

import fr.utc.mylottery.common.Constants;
import fr.utc.mylottery.common.Result;
import fr.utc.mylottery.domain.activity.model.req.PartakeReq;
import fr.utc.mylottery.domain.activity.model.res.GrabResult;
import fr.utc.mylottery.domain.activity.model.res.PartakeResult;
import fr.utc.mylottery.domain.activity.model.res.StockResult;
import fr.utc.mylottery.domain.activity.model.vo.ActivityBillVO;
import fr.utc.mylottery.domain.activity.model.vo.UserTakeActivityVO;
import fr.utc.mylottery.domain.activity.repository.IActivityRepository;
import fr.utc.mylottery.domain.support.ids.IIdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.Map;

public abstract class BaseActivityPartake implements IActivityPartake{
    private Logger logger = LoggerFactory.getLogger(BaseActivityPartake.class);

    @Resource
    protected IActivityRepository activityRepository;
    @Resource
    private Map<Constants.Ids, IIdGenerator> idGeneratorMap;

    protected ActivityBillVO queryActivityBill(PartakeReq req){
        return activityRepository.queryActivityBill(req);
    }

    @Override
    public PartakeResult doPartake(PartakeReq req) {
        // 1. 查询是否存在未执行抽奖领取活动单【user_take_activity 存在 state = 0，领取了但抽奖过程失败的，可以直接返回领取结果继续抽奖】
        UserTakeActivityVO userTakeActivityVO = this.queryNoConsumedTakeActivityOrder(req.getActivityId(), req.getuId());
        if (null != userTakeActivityVO) {
            return buildPartakeResult(userTakeActivityVO.getStrategyId(), userTakeActivityVO.getTakeId(), Constants.ResponseCode.NOT_CONSUMED_TAKE,-1);
        }
        // 2.查询活动账单
        ActivityBillVO activityBillVO = this.queryActivityBill(req);

        // 3.活动信息校验处理【活动库存、状态、日期、个人参与次数】
        Result checkResult = this.checkActivityBill(req, activityBillVO);
        if (!Constants.ResponseCode.SUCCESS.getCode().equals(checkResult.getCode())) {
            return new PartakeResult(checkResult.getCode(), checkResult.getInfo());
        }

        // 4. 扣减活动库存，通过Redis 把活动ID+库存扣减后的值一起作为分布式锁的Key Begin
        StockResult subtractionActivityResult = this.subtractionActivityStockByRedis(req.getuId(), req.getActivityId(), activityBillVO.getStockCount());
        if (!Constants.ResponseCode.SUCCESS.getCode().equals(subtractionActivityResult.getCode())) {
            this.recoverActivityCacheStockByRedis(req.getActivityId(), subtractionActivityResult.getStockTokenKey(), subtractionActivityResult.getCode());
            return new PartakeResult(subtractionActivityResult.getCode(), subtractionActivityResult.getInfo());
        }

        // 5.领取活动信息 user_take_activity_count中扣减left_count字段，在user_take_activity中插入领取活动的记录
        Long orderId = idGeneratorMap.get(Constants.Ids.SnowFlake).nextId();
        GrabResult grabResult = this.grabActivity(req, activityBillVO, orderId);
        if (!Constants.ResponseCode.SUCCESS.getCode().equals(grabResult.getCode())) {
            this.recoverActivityCacheStockByRedis(req.getActivityId(), subtractionActivityResult.getStockTokenKey(), subtractionActivityResult.getCode());
            return new PartakeResult(grabResult.getCode(), grabResult.getInfo());
        }
        // 6. 扣减活动库存，通过Redis End
        this.recoverActivityCacheStockByRedis(req.getActivityId(), subtractionActivityResult.getStockTokenKey(), Constants.ResponseCode.SUCCESS.getCode());

        return buildPartakeResult(activityBillVO.getStrategyId(), orderId, activityBillVO.getStockCount(), subtractionActivityResult.getStockSurplusCount(),Constants.ResponseCode.SUCCESS,grabResult.getUserTakeLeftCount());
    }
    /**
     * 封装结果【返回的策略ID，用于继续完成抽奖步骤】
     * @param strategyId        策略ID
     * @param takeId            领取ID
     * @param stockCount        库存
     * @param stockSurplusCount 剩余库存
     * @param code              状态码
     * @return 封装结果
     */
    private PartakeResult buildPartakeResult(Long strategyId, Long takeId, Integer stockCount, Integer stockSurplusCount, Constants.ResponseCode code, Integer userTakeLeftCount) {
        PartakeResult partakeResult = new PartakeResult(code.getCode(), code.getInfo());
        partakeResult.setStrategyId(strategyId);
        partakeResult.setOrderId(takeId);
        partakeResult.setStockCount(stockCount);
        partakeResult.setStockSurplusCount(stockSurplusCount);
        partakeResult.setUserTakeLeftCount(userTakeLeftCount);
        return partakeResult;
    }
    /**
     * 封装结果【返回的策略ID，用于继续完成抽奖步骤】
     * @param strategyId 策略ID
     * @param takeId     领取ID
     * @param code       状态码
     * @return 封装结果
     */
    private PartakeResult buildPartakeResult(Long strategyId, Long takeId, Constants.ResponseCode code,Integer userTakeLeftCount) {
        PartakeResult partakeResult = new PartakeResult(code.getCode(), code.getInfo());
        partakeResult.setStrategyId(strategyId);
        partakeResult.setOrderId(takeId);
        partakeResult.setUserTakeLeftCount(userTakeLeftCount);
        return partakeResult;
    }

    /**
     * 查询是否存在未执行抽奖领取活动单【user_take_activity 存在 state = 0，领取了但抽奖过程失败的，可以直接返回领取结果继续抽奖】
     * @param activityId 活动ID
     * @param uId        用户ID
     * @return 领取单
     */
    protected abstract UserTakeActivityVO queryNoConsumedTakeActivityOrder(Long activityId, String uId);


    /**
     * 活动信息校验处理，把活动库存、状态、日期、个人参与次数
     * @param partake 参与活动请求
     * @param bill    活动账单
     * @return 校验结果
     */
    protected abstract Result checkActivityBill(PartakeReq partake, ActivityBillVO bill);
    /**
     * 扣减活动库存
     * @param req 参与活动请求
     * @return 扣减结果
     */
    protected abstract Result subtractionActivityStock(PartakeReq req);

    /**
     * 通过Redis扣减活动库存
     * @param uId        用户ID
     * @param activityId 活动号
     * @param stockCount 总库存
     * @return 扣减结果
     */
    protected abstract StockResult subtractionActivityStockByRedis(String uId, Long activityId, Integer stockCount);
    /**
     * 恢复活动库存，通过Redis 【如果非常异常，则需要进行缓存库存恢复，只保证不超卖的特性，所以不保证一定能恢复占用库存，另外最终可以由任务进行补偿库存】
     * @param activityId 活动ID
     * @param tokenKey   分布式 KEY 用于清理
     * @param code       状态
     */
    protected abstract void recoverActivityCacheStockByRedis(Long activityId, String tokenKey, String code);

    /**
     * 领取活动
     * @param partake 参与活动请求
     * @param bill    活动账单
     * @return 领取结果
     */
    protected abstract GrabResult grabActivity(PartakeReq partake, ActivityBillVO bill,Long takeId);

}
