package cn.com.yyxx.yld.supply.core;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author linmeng
 * @version 1.0
 * @date 2020/4/29 11:17
 * @since 0.11.0
 */

@Data
@Component
@ConfigurationProperties(prefix = "yld.payment.center")
public class CancelBackProperties implements Serializable {

    private static final long serialVersionUID = 8861690555197708194L;

    /**
     * 退款回调地址(异步)
     */
    private String refund;

}
