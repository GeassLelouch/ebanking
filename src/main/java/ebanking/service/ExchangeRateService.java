// src/main/java/ebanking/service/ExchangeRateService.java
package ebanking.service;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class ExchangeRateService {
    // 定義常數：本位幣
    public static final String BASE_CURRENCY = "TWD";
    
	//每日匯率由外部 API 提供
    private final WebClient client = WebClient.create("https://api.provider.com");
    
    private static final Map<String, BigDecimal> FIXED_RATES = Map.of(
    	    "EUR", new BigDecimal("34.00"),
    	    "GBP", new BigDecimal("40.06"),
    	    "CHF", new BigDecimal("36.40")
    	);

    /**
     * 取得從 baseCurrency 轉到 targetCurrency 的即時匯率
     */
    public BigDecimal getRate(String baseCurrency, String targetCurrency) {
    	
    	BigDecimal rate = FIXED_RATES.get(baseCurrency);
        if (rate == null) {
            throw new IllegalArgumentException("不支援的幣別: " + baseCurrency);
        }
        return rate;
    	//實際上呼叫寫法 每日匯率由外部 API 提供
//        return client.get()
//            .uri(uriBuilder -> uriBuilder
//               .path("/latest")
//               .queryParam("base", baseCurrency)
//               .queryParam("symbols", targetCurrency)
//               .build())
//            .retrieve()
//            .bodyToMono(RateResponse.class)
//            .block()
//            .getRates()
//            .get(targetCurrency);
    }

    //當前匯率主幣別，若固定則呼叫此方法
    public BigDecimal getRateToBase(String transactionCurrency) {
        return getRate(transactionCurrency, BASE_CURRENCY);
    }

    // RateResponse、內部 Map<String,BigDecimal> rates 等 class 自行定義
}
