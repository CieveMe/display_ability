package cn.com.yyxx.yld.supply.data.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * <p>超级会员支付请求VO</p>
 *
 * @author 東
 * @version 1.0.0
 * @program yld-micro-service-model
 * @description 支付请求RequestVO
 * @date 2020-02-24 14:09
 * @since 0.1.0
 **/
@Data
@ApiModel(value = "支付请求VO对象", description = "支付请求VO对象")
public class SuperVIPRequestPayVO implements Serializable {

    private static final long serialVersionUID = 754727617232795515L;


    @NotNull(message = "超级会员卡号不可为空")
    @ApiModelProperty(value = "超级会员卡号", name = "cardNum")
    private String cardNum;
    private OrderInfo orderInfo;

    @Data
    @ApiModel(value = "订单详情信息", description = "orderInfo")
    public static class OrderInfo {
        @NotNull(message = "订单id不可为空")
        @ApiModelProperty(value = "订单id", name = "orderId")
        private String orderId;
        @NotNull(message = "订单No不可为空")
        @ApiModelProperty(value = "订单No", name = "orderNo")
        private String orderNo;
        @NotNull(message = "订单编号不可为空")
        @ApiModelProperty(value = "订单编号", name = "orderSingleNo")
        private String orderSingleNo;
        @NotNull(message = "门店id不可为空")
        @ApiModelProperty(value = "门店id", name = "storeId")
        private String storeId;
        @NotNull(message = "超级会员卡号不可为空")
        @ApiModelProperty(value = "超级会员卡号", name = "cardNo")
        private String cardNo;
        @NotNull(message = "用户名称不可为空")
        @ApiModelProperty(value = "用户名称", name = "userName")
        private String userName;
        @NotNull(message = "userNo不可为空")
        @ApiModelProperty(value = "userNo", name = "userNo")
        private String userNo;
        @NotNull(message = "交易金额不可为空")
        @ApiModelProperty(value = "交易金额(分)", name = "amount")
        private long amount;
        @NotNull(message = "超级会员折扣不可为空")
        @ApiModelProperty(value = "超级会员折扣（万分比）", name = "discount")
        private long discount;
        @NotNull(message = "总优惠金额不可为空")
        @ApiModelProperty(value = "总优惠金额（分）", name = "discountAmount")
        private long discountAmount;
        @NotNull(message = "门店优惠金额不可为空")
        @ApiModelProperty(value = "门店优惠金额（分）", name = "storeDiscountAmount")
        private long storeDiscountAmount;
        @NotNull(message = "会员优惠金额不可为空")
        @ApiModelProperty(value = "会员优惠金额（分）", name = "memberDiscountAmount")
        private long memberDiscountAmount;
        @NotNull(message = "积分优惠金额不可为空")
        @ApiModelProperty(value = "积分优惠金额（分）", name = "integralDiscountAmount")
        private long integralDiscountAmount;
        @NotNull(message = "实际交易金额不可为空")
        @ApiModelProperty(value = "实际交易金额(分)", name = "realAmount")
        private long realAmount;
        @NotNull(message = "订单商品不可为空")
        @ApiModelProperty(value = "订单商品", name = "list")
        private List<OrderItem> list;

        @Data
        @ApiModel(value = "订单详情信息", description = "list")
        public static class OrderItem {
            @NotNull(message = "商品名称不可为空")
            @ApiModelProperty(value = "商品名称", name = "tradeName")
            private String tradeName;
            @NotNull(message = "商品数量不可为空")
            @ApiModelProperty(value = "商品数量", name = "tradeCount")
            private String tradeCount;
            @NotNull(message = "商品总金额不可为空")
            @ApiModelProperty(value = "商品总金额(分)", name = "tradeAmount")
            private String tradeAmount;
            @NotNull(message = "商品单位不可为空")
            @ApiModelProperty(value = "商品单位", name = "tradeUnit")
            private String tradeUnit;
            @NotNull(message = "商品规格不可为空")
            @ApiModelProperty(value = "商品规格", name = "tradeSize")
            private String tradeSize;
        }

    }
}
