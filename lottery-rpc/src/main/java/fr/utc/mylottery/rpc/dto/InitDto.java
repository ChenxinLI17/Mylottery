package fr.utc.mylottery.rpc.dto;

import java.io.Serializable;
import java.util.List;

public class InitDto implements Serializable {
    private List<String> awardName;

    public InitDto(List<String> awardName) {
        this.awardName = awardName;
    }

    public List<String> getAwardName() {
        return awardName;
    }

    public void setAwardName(List<String> awardName) {
        this.awardName = awardName;
    }
}
