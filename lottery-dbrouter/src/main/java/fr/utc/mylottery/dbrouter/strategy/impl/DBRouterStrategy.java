package fr.utc.mylottery.dbrouter.strategy.impl;


import fr.utc.mylottery.dbrouter.config.DBContext;
import fr.utc.mylottery.dbrouter.config.DBRouterConfig;

import fr.utc.mylottery.dbrouter.strategy.IDBRouterStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;

public class DBRouterStrategy implements IDBRouterStrategy {
    private Logger logger = LoggerFactory.getLogger(DBRouterStrategy.class);

    @Resource
    private DBRouterConfig dbRouterConfig;

    @Override
    public void doRouter(String dbKeyAttr) {
        int size = dbRouterConfig.getDbCount() * dbRouterConfig.getTbCount();
        logger.info("dbKeyAttr:{}",dbKeyAttr);
        /** 哈希散列 + 扰动算法 结果总是0和8 */
        //int idx = (dbKeyAttr.hashCode() ^ (dbKeyAttr.hashCode() >>> 16)) & (size-1);
        /** 哈希散列 结果总是0和8 */
        //int idx = dbKeyAttr.hashCode() & (size-1);
        /** 整数求模散列 */
        int idx = Math.abs(dbKeyAttr.hashCode()) % size;

        // 库表索引；相当于是把一个长条的桶，切割成段，对应分库分表中的库编号和表编号
        int dbIdx = idx / dbRouterConfig.getTbCount() + 1;
        int tbIdx = idx - dbRouterConfig.getTbCount() * (dbIdx - 1) + 1;

        logger.info("id :{},库：{} ,表：{}",idx,String.format("%02d", dbIdx),String.format("%03d", tbIdx));
        // 设置到 ThreadLocal
        DBContext.setDBKey(String.format("%02d", dbIdx));
        DBContext.setTBKey(String.format("%03d", tbIdx));
        //logger.info("dbc：{} tbc：{}",  dbRouterConfig.getDbCount(), dbRouterConfig.getTbCount());
    }

    @Override
    public void clear(){
        DBContext.clearDBKey();
        DBContext.clearTBKey();
    }
}
