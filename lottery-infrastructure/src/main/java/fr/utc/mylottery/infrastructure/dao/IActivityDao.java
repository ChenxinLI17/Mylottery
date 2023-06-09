package fr.utc.mylottery.infrastructure.dao;

import fr.utc.mylottery.infrastructure.po.Activity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface IActivityDao {
    /**
     * 插入数据
     *
     * @param req 入参
     */
    void insert(Activity req);
    /**
     * 根据活动号查询活动信息
     *
     * @param activityId 活动号
     * @return 活动信息
     */
    Activity queryActivityById(Long activityId);

    /**
     * 变更活动状态
     *
     * @param activityId
     * @param beforeState 更新前状态
     * @param afterState 更新后状态
     * @return 更新数量
     */
    int alterState(@Param("activityId") Long activityId, @Param("beforeState") Integer beforeState, @Param("afterState")Integer afterState);

}