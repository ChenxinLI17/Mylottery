package fr.utc.mylottery.infrastructure.dao;

import fr.utc.mylottery.infrastructure.po.Award;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IAwardDao {
    Award queryAwardInfo(String awardId);
}
