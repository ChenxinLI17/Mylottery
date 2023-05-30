package fr.utc.mylottery.test.domain;

import fr.utc.mylottery.domain.support.ids.policy.SnowFlake;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class IdTest {
    private Logger logger = LoggerFactory.getLogger(ActivityTest.class);
    @Resource
    private SnowFlake snowFlake;
    @Test
    public void SnowFlakeTest(){
//        for(int i= 1; i<10;i++){
//            logger.info("雪花算法策略，生成ID：{}", snowFlake.nextId());
//        }
        logger.info("雪花算法策略，生成ID：{}", snowFlake.nextId());
    }
    @Test
    public void IPTest(){
        try {
            Socket socket = new Socket("www.google.com", 80);
            InetAddress localAddress = socket.getLocalAddress();
            System.out.println(localAddress);
            byte[] ipAddress = localAddress.getAddress();
            long ipAsLong = 0;

            for (byte octet : ipAddress) {
                ipAsLong <<= 8;  // 左移8位
                ipAsLong |= (octet & 0xFF);  // 将每个字节与0xFF进行按位与运算后合并
            }

            System.out.println(ipAsLong);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

