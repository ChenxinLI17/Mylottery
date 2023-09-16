package fr.utc.mylottery.domain.support.ids.policy;

import fr.utc.mylottery.domain.support.ids.IIdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @description: algorithm snowflake
 */
@Component("snowflake")
public class SnowFlake implements IIdGenerator {
    @Resource
    private IPSupport ipSupport;
    private Logger logger = LoggerFactory.getLogger(SnowFlake.class);
    /**
     * starting timestamp : 2023-05-27  00:00:00
     * LocalDateTime dateTime = LocalDateTime.of(2023, 5, 27, 0, 0, 0);
       private final long startTimestamp = dateTime.toEpochSecond(ZoneOffset.UTC);
    /**
     *  timestamp : 41 bits
     *  dataCenterIdBits : 5 bits
     *  workerIdBits : 5 bits
     *  sequence : 12 bits
     */
    private final long dataCenterIdBits = 5L;
    private final long workerIdBits = 5L;
    private final long maxWorkerId = - 1L ^(-1L << workerIdBits);
    private final long maxDatacenterId = - 1L ^(-1L << dataCenterIdBits);
    private final long sequenceBits = 12L;
    private final long workerIdShift = sequenceBits;
    private final long dataCenterIdShift = sequenceBits + workerIdBits;
    private final long timestampLeftShirt = sequenceBits + workerIdBits + dataCenterIdBits;
    private final long sequenceMask = -1L^(-1L<<sequenceBits);
    private long workerId;
    private long dataCenterId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;
    /**
     * use local address IP as workerId
     */
    @PostConstruct
    public void init() throws UnknownHostException {
        InetAddress localAddress = ipSupport.getLocalIP();
        logger.info("ip "+ localAddress);
        if(localAddress == null){
            localAddress = InetAddress.getLocalHost();
        }
        byte[] ipAddress = localAddress.getAddress();
        workerId = 0;

        //IP地址转换成32位二进制
        for (byte octet : ipAddress) {
            workerId <<= 8;  // 左移8位
            workerId |= (octet & 0xFF);  // 将每个字节与0xFF进行按位与运算后合并
        }

        logger.info("w3 "+workerId);
        //取后五位，因为workerId的位数是5
        this.workerId = workerId & 31;
        logger.info("w4 "+workerId);
        dataCenterId = 2L;
    }

    /**
     * Blocking to the next millisecond until a new timestamp is obtained.
     * @return current timestamp
     */
    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while(timestamp <= lastTimestamp){
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }

    @Override
    public synchronized long nextId() {
        long timestamp = System.currentTimeMillis();
        if(timestamp < lastTimestamp){
            // 雪花算法服务器快于时间钟服务器：雪花算法服务器的时间晚于时间钟服务器
            throw new RuntimeException(String.format("Clock moved backwards. Refusing to generate id for %d milliseconds", lastTimestamp));
        }
        if(lastTimestamp == timestamp){
            sequence = (sequence+1) & sequenceMask;
            if(sequence == 0){
                timestamp = tilNextMillis(lastTimestamp);
            }
        }
        else {
            sequence = 0L;
        }
        lastTimestamp = timestamp;
        //logger.info("time: {},datacenterId: {},workerId: {}, seq: {}",timestamp,dataCenterId,workerId,sequence);
        return (timestamp << timestampLeftShirt)
                | (dataCenterId << dataCenterIdShift)
                | (workerId << workerIdShift)
                | sequence;
    }


}
