package fr.utc.mylottery.dbrouter.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class DynamicDataSource extends AbstractRoutingDataSource {

    /***
     * @description: 每次数据库访问之前，确定使用哪个数据源，隐式调用
     * @return 数据源
     */
    @Override
        protected Object determineCurrentLookupKey() {
            return "db" + DBContext.getDBKey();
        }
}
