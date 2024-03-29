package fr.utc.mylottery.domain.activity.service.deploy;

import fr.utc.mylottery.domain.activity.model.req.ActivityDeployReq;

/**
 * @description: 部署活动配置接口
 */
public interface IActivityDeploy {
    /**
     * 创建活动信息
     *
     * @param req 活动配置信息
     */
    void createActivity(ActivityDeployReq req);

    /**
     * 修改活动信息
     *
     * @param req 活动配置信息
     */
    void updateActivity(ActivityDeployReq req);
}
