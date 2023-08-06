package fr.utc.mylottery;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.context.annotation.EnableAspectJAutoProxy;


@SpringBootApplication
@Configurable
@EnableAspectJAutoProxy
@EnableDubbo
public class LotteryApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(LotteryApplication.class, args);
    }

}

