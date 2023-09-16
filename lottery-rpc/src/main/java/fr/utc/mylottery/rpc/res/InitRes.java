package fr.utc.mylottery.rpc.res;

import fr.utc.mylottery.common.Result;
import fr.utc.mylottery.rpc.dto.InitDto;

import java.io.Serializable;


public class InitRes implements Serializable {
    private Result result;
    private InitDto initDto;

    public InitRes(Result result, InitDto initDto) {
        this.result = result;
        this.initDto = initDto;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public InitDto getInitDto() {
        return initDto;
    }

    public void setInitDto(InitDto initDto) {
        this.initDto = initDto;
    }
}
