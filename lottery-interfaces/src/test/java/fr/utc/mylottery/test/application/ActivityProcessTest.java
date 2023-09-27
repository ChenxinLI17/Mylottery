package fr.utc.mylottery.test.application;

import com.alibaba.fastjson.JSON;
import fr.utc.mylottery.application.IActivityProcess;
import fr.utc.mylottery.application.req.DrawProcessReq;
import fr.utc.mylottery.application.res.DrawProcessResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.concurrent.ExecutionException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ActivityProcessTest {
    private Logger logger = LoggerFactory.getLogger(ActivityProcessTest.class);

    @Resource
    private IActivityProcess activityProcess;

    @Test
    public void test_doDrawProcess() throws ExecutionException, InterruptedException {
        DrawProcessReq req = new DrawProcessReq();
        req.setuId("lichenxin");
        req.setActivityId(100001L);
        DrawProcessResult drawProcessResult = activityProcess.doDrawProcess(req);

        logger.info("请求入参：{}", JSON.toJSONString(req));
        logger.info("测试结果：{}", JSON.toJSONString(drawProcessResult));
    }

}
