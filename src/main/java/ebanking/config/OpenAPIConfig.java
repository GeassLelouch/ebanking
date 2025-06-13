package ebanking.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                    .title("e-Banking Transaction Service API")
                    .version("1.0.0")
                    .description("提供分頁查詢交易紀錄並計算匯率的微服務 API"));
    }
}
