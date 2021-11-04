package cn.com.yyxx.yld.supply.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class AuthUrlCofig {

    @Value("${spring.authurl}")
    private String authurl;
}
