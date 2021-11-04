package cn.com.yyxx.yld.supply.data.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author guoyl
 * @version 1.0
 * @since 2020-05-05 09:56
 */
@Data
public class SmSaleOrderForOwnerVO implements Serializable {

    /**
     * 订单状态
     */
    @ApiModelProperty(value = "订单状态", name = "orderStatus", example = "成功")
    private String orderStatus;

    /**
     * 订单状态CODE
     */
    @ApiModelProperty(value = "订单状态CODE", name = "statusCode", example = "SUCCESS")
    private String statusCode;

    /**
     * 订单单号
     */
    @ApiModelProperty(value = "订单单号", name = "singleNo", example = "a9ebd604de7b43a5a9983cdb8b6604d3")
    private String singleNo;

    /**
     * 交易时间
     */
    @ApiModelProperty(value = "交易时间", name = "orderTime", example = "2020-04-02 00:00:00")
    private LocalDateTime mpsoOrderTime;

    /**
     * 交易时间
     */
    @ApiModelProperty(value = "交易时间", name = "orderTime", example = "2020-04-02 00:00:00")
    private String orderTime;

    /**
     * 支付方式
     */
    @ApiModelProperty(value = "支付方式", name = "payType", example = "扫码支付 微信钱包")
    private String payType;

    /**
     * 应收金额
     */
    @ApiModelProperty(value = "应收金额", name = "totalPrice", example = "100.00")
    private BigDecimal totalPrice;

    /**
     * 实收金额
     */
    @ApiModelProperty(value = "实收金额", name = "actualPrice", example = "100.00")
    private BigDecimal actualPrice;

    /**
     * 折扣金额
     */
    @ApiModelProperty(value = "折扣金额", name = "discountPrice", example = "100.00")
    private BigDecimal discountPrice;

    /**
     * 门店ID
     */
    @ApiModelProperty(value = "门店ID", name = "sbiId", example = "10086")
    private Integer sbiId;

    /**
     * 订单编号
     */
    @ApiModelProperty(value = "订单编号", name = "orderNo", example = "a9ebd604de7b43a5a9983cdb8b6604d3")
    private String orderNo;

    /**
     * 失败原因
     */
    @ApiModelProperty(value = "失败原因", name = "failResult", example = "网络异常")
    private String failResult;

    /**
     * 订单详情
     */
    @ApiModelProperty(value = "订单详情", name = "list")
    private List<SmOrderDetailForOwnerVO> list;

}
