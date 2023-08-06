package fr.utc.mylottery.test.domain;

import fr.utc.mylottery.domain.support.ids.policy.SnowFlake;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.net.*;
import java.util.Enumeration;

@RunWith(SpringRunner.class)
@SpringBootTest
public class IdTest {
    private Logger logger = LoggerFactory.getLogger(ActivityTest.class);
    @Resource
    private SnowFlake snowFlake;
    @Test
    public void SnowFlakeTest(){
        for(int i= 1; i<10;i++){
            logger.info("雪花算法策略，生成ID：{}", snowFlake.nextId());
        }
//        logger.info("雪花算法策略，生成ID：{}", snowFlake.nextId());
    }
    @Test
    public void IPTest() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    if (!address.isLoopbackAddress() && address.isSiteLocalAddress()) {
                        logger.info("Local IP: " + address.getHostAddress());
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
}


