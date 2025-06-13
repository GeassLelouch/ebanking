package ebanking.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.Map;

public class RateResponse {

    /** 基準貨幣 */
    @JsonProperty("base")
    private String baseCurrency;

    /** 匯率對應日期，格式 e.g. "2025-06-10" 或 時間戳 */
    @JsonProperty("date")
    private String date;

    /** 
     * 各目標幣別對應的匯率 
     * e.g. { "USD": 1.10, "EUR": 0.92 } 
     */
    @JsonProperty("rates")
    private Map<String, BigDecimal> rates;

    public RateResponse() { }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(String baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Map<String, BigDecimal> getRates() {
        return rates;
    }

    public void setRates(Map<String, BigDecimal> rates) {
        this.rates = rates;
    }

    @Override
    public String toString() {
        return "RateResponse{" +
               "baseCurrency='" + baseCurrency + '\'' +
               ", date='" + date + '\'' +
               ", rates=" + rates +
               '}';
    }
}
