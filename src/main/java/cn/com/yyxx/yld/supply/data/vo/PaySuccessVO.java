package cn.com.yyxx.yld.supply.data.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author linmeng
 * @version 1.0
 * @date 2020/5/5 17:36
 * @since 0.11.0
 */
@Data
public class PaySuccessVO implements Serializable {

    private static final long serialVersionUID = 8014362499400237463L;

    @ApiModelProperty(value = "订单编号", name = "orderNo")
    private String orderNo;

    @ApiModelProperty(value = "支付状态", name = "payStatus")
    private String payStatus;

    @ApiModelProperty(value = "支付方式", name = "payMode")
    private String payMode;

    @ApiModelProperty(value = "金额", name = "amount")
    private Long amount;

    @ApiModelProperty(value = "金额", name = "phone")
    private String phone;
}
