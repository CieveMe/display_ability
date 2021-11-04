package cn.com.yyxx.yld.supply.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * <p>
 *
 * </p>
 *
 * @author FYX
 * @version 0.16.0
 * @date 2020/12/16 09:07
 **/
@Data
@Component
public class YqUrlConfig {

    @Value("${yld.yq.url}")
    private String url;

}
