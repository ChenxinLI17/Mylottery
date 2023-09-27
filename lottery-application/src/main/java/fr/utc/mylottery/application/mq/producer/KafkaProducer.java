package fr.utc.mylottery.application.mq.producer;

import com.alibaba.fastjson.JSON;
import fr.utc.mylottery.domain.activity.model.vo.ActivityPartakeVO;
import fr.utc.mylottery.domain.activity.model.vo.InvoiceVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import javax.annotation.Resource;
import java.util.concurrent.ExecutionException;

/**
 * @description: MQ 消息发送服务
 */
@Component
public class KafkaProducer {
    private Logger logger = LoggerFactory.getLogger(KafkaProducer.class);

    @Resource
    private KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * MQ主题：中奖发货单
     *
     * 启动zk：bin/zookeeper-server-start.sh -daemon config/zookeeper.properties
     * 启动kafka：bin/kafka-server-start.sh -daemon config/server.properties
     * 创建topic：bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic lottery_invoice
     */
    public static final String TOPIC_INVOICE = "lottery_invoice";

    /**
     * MQ主题：活动领取记录
     * 创建topic：bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic lottery_activity_partake
     */
    public static final String TOPIC_ACTIVITY_PARTAKE = "lottery_activity_partake";

    /**
     * 发送中奖物品发货单消息
     * @param invoice 发货单
     */
    public ListenableFuture<SendResult<String, Object>> sendLotteryInvoice(InvoiceVO invoice) {
        String objJson = JSON.toJSONString(invoice);
        logger.info("发送MQ消息(中奖发货单) topic：{} bizId：{} message：{}", TOPIC_INVOICE, invoice.getuId(), objJson);
        return kafkaTemplate.send(TOPIC_INVOICE, objJson);//KafkaTemplate 的 send 方法是异步的，它将消息发送到 Kafka 集群后立即返回，并不等待消息被确认
    }

    /**
     * 发送领取活动记录MQ
     * @param activityPartake 领取活动记录
     */
//    public ListenableFuture<SendResult<String, Object>> sendLotteryActivityPartake(ActivityPartakeVO activityPartake) {
//        String objJson = JSON.toJSONString(activityPartake);
//        logger.info("发送MQ消息(领取活动记录) topic：{} bizId：{} message：{}", TOPIC_ACTIVITY_PARTAKE, activityPartake.getuId(), objJson);
//        return kafkaTemplate.send(TOPIC_ACTIVITY_PARTAKE, objJson);
//    }
    /**
     * 生产者向Kafka集群同步发送领取活动记录MQ
     * @param activityPartake 领取活动记录
     */
    public void sendLotteryActivityPartake(ActivityPartakeVO activityPartake) throws ExecutionException, InterruptedException {
        String objJson = JSON.toJSONString(activityPartake);
        logger.info("发送MQ消息(领取活动记录) topic：{} bizId：{} message：{}", TOPIC_ACTIVITY_PARTAKE, activityPartake.getuId(), objJson);
        kafkaTemplate.send(TOPIC_ACTIVITY_PARTAKE, objJson).get();
    }
}
