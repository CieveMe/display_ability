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
 * @since 2019/4/12 16:02
 */
@Data
@ConfigurationProperties(prefix = "yld.scm.wx.template.config")
@Component
public class WxMpTemplateMessageProperties implements Serializable {

    private static final long serialVersionUID = -1511237482811001325L;

//    /**
//     *报表生成通知
//     */
//    private String generateReport;
//
//    /**
//     *统计结果通知
//     */
//    private String statisticalResult;

    /**
     * 支付结果通知
     */
    private String payResult;

    /**
     * 退款结果通知
     */
    private String refundResult;
}
