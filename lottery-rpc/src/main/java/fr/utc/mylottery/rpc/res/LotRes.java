package fr.utc.mylottery.rpc.res;

import fr.utc.mylottery.common.Result;
import fr.utc.mylottery.rpc.dto.ActivityDto;
import fr.utc.mylottery.rpc.dto.DrawDto;

import java.io.Serializable;

public class DrawRes implements Serializable {
    private Result result;
    private DrawDto draw;
    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public DrawDto getDraw() {
        return draw;
    }

    public void setDraw(DrawDto draw) {
        this.draw = draw;
    }


}
