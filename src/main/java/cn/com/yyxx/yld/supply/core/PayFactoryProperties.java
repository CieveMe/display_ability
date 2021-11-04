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
@ConfigurationProperties(prefix = "yld.scm.pay.factory")
public class PayFactoryProperties implements Serializable {

    private static final long serialVersionUID = 8861690555197708194L;

    /**
     * 支付地址(异步)
     */
    private String payUrl;

    /**
     * 查询地址(同步)
     */
    private String queryUrl;

    /**
     * 是否要加密
     */
    private Boolean encrypt;

    /**
     * 新版支付回调地址
     */
    private String returnNotifyUrl;

    /**
     * 新版支付openId回调地址
     */
    private String returnNotifyOpenId;

}
