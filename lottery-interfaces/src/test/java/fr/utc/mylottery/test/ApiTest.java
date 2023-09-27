package fr.utc.mylottery.test;

import com.alibaba.fastjson.JSON;
import fr.utc.mylottery.infrastructure.dao.IActivityDao;
import fr.utc.mylottery.infrastructure.po.Activity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApiTest {

    private final Logger logger = LoggerFactory.getLogger(ApiTest.class);

    @Resource
    IActivityDao activityDao;

    @Test
    public void test_insert() {
        Activity activity = new Activity();
        activity.setActivityId(100003L);
        activity.setActivityName("做");
        activity.setActivityDesc("仅用于插入数据测试");
        activity.setBeginDateTime(new Date());
        activity.setEndDateTime(new Date());
        activity.setStockCount(100);
        activity.setTakeCount(10);
        activity.setState(0);
        activity.setCreator("lcx");
        activityDao.insert(activity);
    }

    @Test
    public void test_select() {
        Activity activity = activityDao.queryActivityById(100002L);
        logger.info("测试结果：{}", JSON.toJSONString(activity));
    }

    //成功
    @Test
    public void test_update(){
        activityDao.alterState(100002L,5,3);
    }

}
