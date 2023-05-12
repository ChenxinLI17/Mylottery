package fr.utc.mylottery.infrastructure.dao;

import fr.utc.mylottery.infrastructure.po.Activity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IActivityDao {

    void insert(Activity req);

    Activity queryActivityById(Long activityId);

}