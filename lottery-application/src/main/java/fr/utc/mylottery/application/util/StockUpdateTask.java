//package fr.utc.mylottery.application.util;
//
//import fr.utc.mylottery.application.mq.consumer.ActivityPartakeListener;
//import fr.utc.mylottery.domain.activity.model.vo.ActivityPartakeVO;
//import fr.utc.mylottery.domain.activity.service.partake.IActivityPartake;
//import fr.utc.mylottery.infrastructure.util.RedisUtil;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.Resource;
//import java.util.Set;
//
//@Component
//public class StockUpdateTask {
//    private Logger logger = LoggerFactory.getLogger(StockUpdateTask.class);
//    @Resource
//    private RedisUtil redisUtil;
//    @Resource
//    private IActivityPartake activityPartake;
//
//
//    // 在这里注入数据库操作的服务或者DAO
//    @Scheduled(fixedRate = 3000) // 定时任务执行周期，这里设置为每分钟执行一次
//    public void processStockUpdateRequests() {
//        // 获取Redis中的库存更新请求
//        Set<String> cacheKeys = redisUtil.keys("stock_update_request-");
//
//        if (cacheKeys != null && !cacheKeys.isEmpty()) {
//            for (String cacheKey : cacheKeys) {
//                // 从缓存中获取库存更新请求
//                ActivityPartakeVO activityPartakeVO = (ActivityPartakeVO) redisUtil.get(cacheKey);
//
//                // 执行数据库库存更新操作
//                logger.info("1111111");
//                if (activityPartakeVO != null) {
//                    activityPartake.updateActivityStock(activityPartakeVO);
//
//                    // 处理完毕后，从缓存中移除请求记录
//                    redisUtil.del(cacheKey);
//                }
//            }
//        }
//    }
//}
//
