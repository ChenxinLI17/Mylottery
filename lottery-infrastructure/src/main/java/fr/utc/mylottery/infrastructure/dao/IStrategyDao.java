package fr.utc.mylottery.infrastructure.dao;

import fr.utc.mylottery.infrastructure.po.Strategy;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IStrategyDao {
    Strategy queryStrategy(Long strategyId);
}
