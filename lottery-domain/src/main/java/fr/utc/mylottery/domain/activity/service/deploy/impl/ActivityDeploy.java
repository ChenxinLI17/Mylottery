package fr.utc.mylottery.domain.activity.service.deploy.impl;

import com.alibaba.fastjson.JSON;
import fr.utc.mylottery.domain.activity.model.aggregates.ActivityConfigRich;
import fr.utc.mylottery.domain.activity.model.req.ActivityConfigReq;
import fr.utc.mylottery.domain.activity.model.vo.ActivityVO;
import fr.utc.mylottery.domain.activity.model.vo.AwardVO;
import fr.utc.mylottery.domain.activity.model.vo.StrategyDetailVO;
import fr.utc.mylottery.domain.activity.model.vo.StrategyVO;
import fr.utc.mylottery.domain.activity.repository.IActivityRepository;
import fr.utc.mylottery.domain.activity.service.deploy.IActivityDeploy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
@Service
public class ActivityDeploy implements IActivityDeploy {

    private Logger logger= LoggerFactory.getLogger(ActivityDeploy.class);
    @Resource
    private IActivityRepository activityRepository;
    @Transactional(rollbackFor = Exception.class)//回滚事务
    @Override
    public void createActivity(ActivityConfigReq req) {
        logger.info("创建活动配置开始，activityId:{}",req.getActivityId());
        ActivityConfigRich activityConfigRich = req.getActivityConfigRich();
        try {
            ActivityVO activityVO = activityConfigRich.getActivity();
            activityRepository.addActivity(activityVO);

            StrategyVO strategyVO = activityConfigRich.getStrategy();
            activityRepository.addStrategy(strategyVO);

            List<AwardVO> awardList = activityConfigRich.getAwardList();
            activityRepository.addAward(awardList);

            List<StrategyDetailVO> strategyDetailList = activityConfigRich.getStrategy().getStrategyDetailList();
            activityRepository.addStrategyDetailList(strategyDetailList);

            logger.info("创建活动配置完成，activityId：{}", req.getActivityId());
        }catch (DuplicateKeyException e) {
            logger.error("创建活动配置失败，唯一索引冲突 activityId：{} reqJson：{}", req.getActivityId(), JSON.toJSONString(req), e);
            throw e;
        }
    }

    @Override
    public void updateActivity(ActivityConfigReq req) {

    }
}
