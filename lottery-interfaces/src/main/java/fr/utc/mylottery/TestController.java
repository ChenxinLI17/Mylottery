package fr.utc.mylottery;

import com.alibaba.fastjson.JSON;
import fr.utc.mylottery.application.IActivityProcess;
import fr.utc.mylottery.application.req.DrawProcessReq;
import fr.utc.mylottery.application.res.DrawProcessResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.ExecutionException;

@RestController
public class TestController {
    private Logger logger = LoggerFactory.getLogger(TestController.class);
    @Resource
    private IActivityProcess activityProcess;
    @PostMapping("post")
    public ResponseEntity<String> test(@RequestParam("uId") String uId) throws ExecutionException, InterruptedException {
        DrawProcessReq req = new DrawProcessReq();
        req.setuId(uId);
        req.setActivityId(100001L);
        DrawProcessResult drawProcessResult = activityProcess.doDrawProcess(req);

        logger.info("请求入参：{}", JSON.toJSONString(req));
        logger.info("测试结果：{}", JSON.toJSONString(drawProcessResult));

        return ResponseEntity.noContent().build();
    }
}
