package fr.utc.mylottery.rpc.res;

import fr.utc.mylottery.common.Result;
import fr.utc.mylottery.rpc.dto.LotDto;

import java.io.Serializable;

public class LotRes implements Serializable {
    private Result result;
    private LotDto lot;
    public Result getResult() {
        return result;
    }

    public LotRes(Result result, LotDto lot) {
        this.result = result;
        this.lot = lot;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public LotDto getLot() {
        return lot;
    }

    public void setLot(LotDto lot) {
        this.lot = lot;
    }


}
