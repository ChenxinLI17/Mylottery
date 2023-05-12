package fr.utc.mylottery.interfaces;

import fr.utc.mylottery.common.Result;
import fr.utc.mylottery.common.Constants;
import fr.utc.mylottery.infrastructure.dao.IActivityDao;
import fr.utc.mylottery.infrastructure.po.Activity;
import fr.utc.mylottery.rpc.res.ActivityRes;
import fr.utc.mylottery.rpc.IActivityBooth;
import fr.utc.mylottery.rpc.dto.ActivityDto;
import fr.utc.mylottery.rpc.req.ActivityReq;
import org.apache.dubbo.config.annotation.Service;



import javax.annotation.Resource;
@Service(interfaceClass = IActivityBooth.class, version = "1.0.0")
public class ActivityBooth implements IActivityBooth {//rpc接口实现类
    @Resource
    private IActivityDao activityDao;

    @Override
    public ActivityRes queryActivityById(ActivityReq req) {

        Activity activity = activityDao.queryActivityById(req.getActivityId());

        ActivityDto activityDto = new ActivityDto();
        activityDto.setActivityId(activity.getActivityId());
        activityDto.setActivityName(activity.getActivityName());
        activityDto.setActivityDesc(activity.getActivityDesc());
        activityDto.setBeginDateTime(activity.getBeginDateTime());
        activityDto.setEndDateTime(activity.getEndDateTime());
        activityDto.setStockCount(activity.getStockCount());
        activityDto.setTakeCount(activity.getTakeCount());

        return new ActivityRes(new Result(Constants.ResponseCode.SUCCESS.getCode(), Constants.ResponseCode.SUCCESS.getInfo()), activityDto);
    }

}

