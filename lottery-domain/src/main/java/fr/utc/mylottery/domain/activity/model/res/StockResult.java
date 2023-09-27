package fr.utc.mylottery.domain.activity.model.res;


import fr.utc.mylottery.common.Result;

/**
 * @description: 库存处理结果
 */
public class StockResult extends Result {

    /**
     * 库存 Key
     */
    private String stockTokenKey;
    /**
     * activity 库存剩余
     */
    private Integer stockSurplusCount;

    public StockResult(String code, String info) {
        super(code, info);
    }

    public StockResult(String code, String info, String stockTokenKey, Integer stockSurplusCount) {
        super(code, info);
        this.stockTokenKey = stockTokenKey;
        this.stockSurplusCount = stockSurplusCount;
    }

    public String getStockTokenKey() {
        return stockTokenKey;
    }

    public void setStockTokenKey(String stockTokenKey) {
        this.stockTokenKey = stockTokenKey;
    }

    public Integer getStockSurplusCount() {
        return stockSurplusCount;
    }

    public void setStockSurplusCount(Integer stockSurplusCount) {
        this.stockSurplusCount = stockSurplusCount;
    }
}
