package cn.com.yyxx.yld.supply.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class SmwConfig {

    @Value("${yld.smw.cancel_bill_url}")
    private String host;
    @Value("${yld.smw.url}")
    private String url;
}
