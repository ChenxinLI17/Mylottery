package fr.utc.mylottery.domain.award.repository;

import fr.utc.mylottery.infrastructure.po.Award;

public interface IAwardRepository {
    /**
     * 查询奖品信息
     *
     * @param awardId 奖品ID
     * @return        奖品信息
     */
    Award queryAwardInfo(String awardId);
}
