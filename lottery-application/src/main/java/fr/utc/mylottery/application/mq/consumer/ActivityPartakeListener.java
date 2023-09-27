package fr.utc.mylottery.application.mq.consumer;

import com.alibaba.fastjson.JSON;
import fr.utc.mylottery.domain.activity.model.vo.ActivityPartakeVO;
import fr.utc.mylottery.domain.activity.service.partake.IActivityPartake;
import fr.utc.mylottery.infrastructure.util.RedisUtil;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * @description: 抽奖活动领取记录监听消息
 */
@Component
public class ActivityPartakeListener {
    private Logger logger = LoggerFactory.getLogger(ActivityPartakeListener.class);

    @Resource
    private IActivityPartake activityPartake;
    @Resource
    private RedisUtil redisUtil;

    @KafkaListener(topics = "lottery_activity_partake", groupId = "lottery",concurrency = "1")
    public void onMessage(ConsumerRecord<?, ?> record, Acknowledgment ack) {
        Optional<?> message = Optional.ofNullable(record.value());

        // 1. 判断消息是否存在
        if (!message.isPresent()) {
            return;
        }

        // 2. 转化对象
        ActivityPartakeVO activityPartakeVO = JSON.parseObject((String) message.get(), ActivityPartakeVO.class);
        logger.info("消费MQ消息，异步扣减活动库存 message：{}", message.get());

        // 3. 更新数据库库存【实际场景业务体量较大，可能也会由于MQ消费引起并发，对数据库产生压力，所以如果并发量较大，可以把库存记录缓存中，并使用定时任务进行处理缓存和数据库库存同步，减少对数据库的操作次数】
        activityPartake.updateActivityStock(activityPartakeVO);
        ack.acknowledge();
        //String cacheKey = "stock_update_request-"+activityPartakeVO.getActivityId()+"-"+activityPartakeVO.getStockSurplusCount(); // 生成唯一的缓存键
        //redisUtil.set(cacheKey, activityPartakeVO);

    }

}
