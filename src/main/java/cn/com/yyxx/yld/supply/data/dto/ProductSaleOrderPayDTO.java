package cn.com.yyxx.yld.supply.data.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 *
 * </p>
 *
 * @author linmeng
 * @version 1.0
 * @date 2019/9/20 17:35
 */
@Data
public class ProductSaleOrderPayDTO implements Serializable {

    private static final long serialVersionUID = 6509525367136555617L;
    /**
     * 订单ID
     */
    private Long mpsoId;

    /**
     * 设备ID
     */
    private Integer mpsoSdcId;

    /**
     * 订单编号
     */
    private String mpsoOrderNo;
    /**
     * 支付方式
     */
    private String mpsoPaymentMethod;

    /**
     * 支付方式code
     */
    private String mpsoPaymentMethodCode;
    /**
     * 订单状态
     */
    private String mpsoOrderStatus;

    /**
     * 订单状态code
     */
    private String mpsoOrderStatusCode;

    /**
     * 订单状态名称
     */
    private String mpsoOrderStatusName;

    /**
     * 支付金额
     */
    private BigDecimal mpsoActualPayPrice;

    /**
     * 扫码渠道
     */
    private String mpsoScanCodeChannel;

    /**
     * 扫码渠道名称
     */
    private String mpsoScanCodeChannelExt1;

    /**
     * 三方买家id
     */
    private String openId;
}
