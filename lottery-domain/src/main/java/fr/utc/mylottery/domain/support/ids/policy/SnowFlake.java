package fr.utc.mylottery.domain.support.ids.policy;

import fr.utc.mylottery.domain.support.ids.IIdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * @description: algorithm snowflake
 */
@Component
public class SnowFlake implements IIdGenerator {
    private Logger logger = LoggerFactory.getLogger(SnowFlake.class);
    /**
     * starting timestamp
     */
    LocalDateTime dateTime = LocalDateTime.of(2023, 5, 27, 0, 0, 0);
    private final long startTimestamp = dateTime.toEpochSecond(ZoneOffset.UTC);
    /**
     *
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
    @PostConstruct
    public void init() {
        try {
            Socket socket = new Socket("www.google.com", 80);
            InetAddress localAddress = socket.getLocalAddress();
            byte[] ipAddress = localAddress.getAddress();
            workerId = 0;

            for (byte octet : ipAddress) {
                workerId <<= 8;  // 左移8位
                workerId |= (octet & 0xFF);  // 将每个字节与0xFF进行按位与运算后合并
            }

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        logger.info("w3"+workerId);
        this.workerId = workerId >> 16 & 31;
        logger.info("w4"+workerId);
        dataCenterId = 1L;
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
        if(timestamp - startTimestamp < lastTimestamp){
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
        logger.info("time: {},datacenterId: {},workerId: {}, seq: {}",timestamp,dataCenterId,workerId,sequence);
        return (timestamp << timestampLeftShirt)
                | (dataCenterId << dataCenterIdShift)
                | (workerId << workerIdShift)
                | sequence;
    }


}
