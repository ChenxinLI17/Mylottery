package fr.utc.mylottery.dbrouter.config;

import fr.utc.mylottery.dbrouter.interceptor.MybatisInterceptor;
import fr.utc.mylottery.dbrouter.strategy.IDBRouterStrategy;
import fr.utc.mylottery.dbrouter.strategy.impl.DBRouterStrategy;
import org.apache.ibatis.plugin.Interceptor;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.support.TransactionTemplate;


import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Configuration
public class DataSourceAutoConfig implements EnvironmentAware {
    private Map<String, Map<String, Object>> dataSourceMap = new HashMap<>();

    /**
     * 默认数据源配置
     */
    private Map<String, Object> defaultDataSourceConfig;

    /**
     * 分库数量
     */
    private int dbCount;

    /**
     * 分表数量
     */
    private int tbCount;

    /**
     * 路由字段
     */
    private String routerKey;

    @Bean
    public DBRouterConfig dbRouterConfig() {
        return new DBRouterConfig(dbCount, tbCount, routerKey);
    }

    @Bean
    public Interceptor plugin() {
        return new MybatisInterceptor();
    }

    @Bean
    public IDBRouterStrategy dbRouterStrategy() {
        return new DBRouterStrategy();
    }

    @Override
    public void setEnvironment(Environment environment) {
        String prefix = "mini-db-router.jdbc.datasource.";

        dbCount = Integer.parseInt(Objects.requireNonNull(environment.getProperty(prefix + "dbCount")));
        tbCount = Integer.parseInt(Objects.requireNonNull(environment.getProperty(prefix + "tbCount")));
        routerKey = environment.getProperty(prefix + "routerKey");

        // 分库分表数据源 dataSources db01,db02,db03
        String dataSources = environment.getProperty(prefix + "list");
        assert dataSources != null;

        for (String dbInfo : dataSources.split(",")) {
            Map<String, Object> dataSourceProps = new HashMap<>();
            dataSourceProps.put("url", environment.getProperty(prefix + dbInfo + ".url"));
            dataSourceProps.put("username", environment.getProperty(prefix + dbInfo + ".username"));
            dataSourceProps.put("password", environment.getProperty(prefix + dbInfo + ".password"));
            dataSourceMap.put(dbInfo, dataSourceProps);
        }

        // 默认数据源
        String defaultData = environment.getProperty(prefix + "default");
        defaultDataSourceConfig = new HashMap<>();
        defaultDataSourceConfig.put("url", environment.getProperty(prefix + defaultData + ".url"));
        defaultDataSourceConfig.put("username", environment.getProperty(prefix + defaultData + ".username"));
        defaultDataSourceConfig.put("password", environment.getProperty(prefix + defaultData + ".password"));
    }
    @Bean
    public DataSource dataSource() {
        // 创建数据源
        Map<Object, Object> targetDataSources = new HashMap<>();
        for (String dbInfo : dataSourceMap.keySet()) {
            Map<String, Object> objMap = dataSourceMap.get(dbInfo);
            targetDataSources.put(dbInfo, new DriverManagerDataSource(objMap.get("url").toString(), objMap.get("username").toString(), objMap.get("password").toString()));
        }

        // 设置数据源
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        dynamicDataSource.setTargetDataSources(targetDataSources);
        dynamicDataSource.setDefaultTargetDataSource(new DriverManagerDataSource(defaultDataSourceConfig.get("url").toString(), defaultDataSourceConfig.get("username").toString(), defaultDataSourceConfig.get("password").toString()));

        return dynamicDataSource;
    }

    @Bean
    public TransactionTemplate transactionTemplate(DataSource dataSource) {
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
        dataSourceTransactionManager.setDataSource(dataSource);

        TransactionTemplate transactionTemplate = new TransactionTemplate();
        transactionTemplate.setTransactionManager(dataSourceTransactionManager);
        transactionTemplate.setPropagationBehaviorName("PROPAGATION_REQUIRED");
        return transactionTemplate;
    }
}
