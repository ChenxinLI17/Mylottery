package fr.utc.mylottery.rpc.res;

import fr.utc.mylottery.common.Result;
import fr.utc.mylottery.rpc.dto.DrawDto;

import java.io.Serializable;

public class DrawRes implements Serializable {
    private Result result;
    private DrawDto drawDto;

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public DrawDto getDrawDto() {
        return drawDto;
    }

    public void setDrawDto(DrawDto drawDto) {
        this.drawDto = drawDto;
    }

    public DrawRes(Result result, DrawDto drawDto) {
        this.result = result;
        this.drawDto = drawDto;
    }

}
