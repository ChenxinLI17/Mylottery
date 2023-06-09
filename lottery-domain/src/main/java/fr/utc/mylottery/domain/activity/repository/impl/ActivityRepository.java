package fr.utc.mylottery.domain.activity.repository.impl;


import fr.utc.mylottery.common.Constants;
import fr.utc.mylottery.domain.activity.model.vo.*;
import fr.utc.mylottery.domain.activity.repository.IActivityRepository;
import fr.utc.mylottery.infrastructure.dao.IActivityDao;
import fr.utc.mylottery.infrastructure.dao.IAwardDao;
import fr.utc.mylottery.infrastructure.dao.IStrategyDao;
import fr.utc.mylottery.infrastructure.dao.IStrategyDetailDao;
import fr.utc.mylottery.infrastructure.po.Activity;
import fr.utc.mylottery.infrastructure.po.Award;
import fr.utc.mylottery.infrastructure.po.Strategy;
import fr.utc.mylottery.infrastructure.po.StrategyDetail;
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

}

