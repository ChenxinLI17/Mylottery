package fr.utc.mylottery.infrastructure.dao;


import fr.utc.mylottery.dbrouter.annotation.DBRouter;
import fr.utc.mylottery.infrastructure.po.UserStrategyExport;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IUserStrategyExportDao {
    /**
     * 新增数据
     * @param userStrategyExport 用户策略
     */
    @DBRouter(key = "orderId")
    void insert(UserStrategyExport userStrategyExport);

    /**
     * 查询数据
     * @param uId 用户ID
     * @return 用户策略
     */
    @DBRouter
    UserStrategyExport queryUserStrategyExportByUId(String uId);
}