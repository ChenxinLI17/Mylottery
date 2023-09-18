package fr.utc.mylottery.domain.award.repository.impl;

import fr.utc.mylottery.domain.award.repository.IAwardRepository;
import fr.utc.mylottery.infrastructure.dao.IAwardDao;
import fr.utc.mylottery.infrastructure.dao.IUserStrategyExportDao;
import fr.utc.mylottery.infrastructure.po.Award;
import fr.utc.mylottery.infrastructure.po.UserStrategyExport;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class AwardRepository implements IAwardRepository {
    @Resource
    private IAwardDao awardDao;
    @Resource
    private IUserStrategyExportDao userStrategyExportDao;
    @Override
    public Award queryAwardInfo(String awardId) {
        return awardDao.queryAwardInfo(awardId);
    }

    @Override
    public void updateUserAwardState(String uId, Long orderId, String awardId, Integer grantState) {
        UserStrategyExport userStrategyExport = new UserStrategyExport();
        userStrategyExport.setuId(uId);
        userStrategyExport.setOrderId(orderId);
        userStrategyExport.setAwardId(awardId);
        userStrategyExport.setGrantState(grantState);
        userStrategyExportDao.updateUserAwardState(userStrategyExport);
    }
}
