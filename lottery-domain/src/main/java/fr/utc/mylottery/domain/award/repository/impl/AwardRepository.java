package fr.utc.mylottery.domain.award.repository.impl;

import fr.utc.mylottery.domain.award.repository.IAwardRepository;
import fr.utc.mylottery.infrastructure.dao.IAwardDao;
import fr.utc.mylottery.infrastructure.po.Award;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class AwardRepository implements IAwardRepository {
    @Resource
    private IAwardDao awardDao;
    @Override
    public Award queryAwardInfo(String awardId) {
        return awardDao.queryAwardInfo(awardId);
    }
}
