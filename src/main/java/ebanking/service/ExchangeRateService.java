// src/main/java/ebanking/service/ExchangeRateService.java
package ebanking.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.math.BigDecimal;

@Service
public class ExchangeRateService {
    private final WebClient client = WebClient.create("https://api.your-provider.com");

    /**
     * 取得從 baseCurrency 轉到 targetCurrency 的即時匯率
     */
    public BigDecimal getRate(String baseCurrency, String targetCurrency) {
        // 範例 call & parse，實作依第三方文件調整
        return client.get()
            .uri(uriBuilder -> uriBuilder
               .path("/latest")
               .queryParam("base", baseCurrency)
               .queryParam("symbols", targetCurrency)
               .build())
            .retrieve()
            .bodyToMono(RateResponse.class)
            .block()
            .getRates()
            .get(targetCurrency);
    }

    /**
     * 如果你服務裡都是轉成同個目標幣別（例如 USD），可以包在這裡
     */
    public BigDecimal getRateToBase(String transactionCurrency) {
        return getRate(transactionCurrency, "USD");
    }

    // RateResponse、內部 Map<String,BigDecimal> rates 等 class 自行定義
}
