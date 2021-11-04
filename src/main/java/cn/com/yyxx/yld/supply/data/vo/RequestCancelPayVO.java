package cn.com.yyxx.yld.supply.data.vo;

import cn.com.yyxx.yld.supply.data.dto.RequestParent;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>支付撤销请求VO对象</p>
 *
 * @author 東
 * @version 1.0.0
 * @program yld-micro-service-model
 * @description 支付撤销请求VO对象
 * @date 2020-02-24 14:40
 * @since 0.1.0
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value="支付撤销请求VO对象", description="支付撤销请求VO对象")
public class RequestCancelPayVO extends RequestParent implements Serializable {

    private static final long serialVersionUID = 1189426673877401435L;

    @Valid
    @NotNull
    @ApiModelProperty(value = "订单信息", name = "order")
    private RequestCancelPayOrder order;

    @Data
    @ApiModel(value="支付撤销订单对象", description="支付撤销订单对象")
    public static class RequestCancelPayOrder {

        @NotNull(message = "订单号不可为空")
        @ApiModelProperty(value = "订单号", name = "orderId")
        private String orderId;

        @NotNull(message = "平台订单号不可为空")
        @ApiModelProperty(value = "平台订单号", name = "tradeNo")
        private String tradeNo;

        @NotNull(message = "退款订单号不可为空")
        @ApiModelProperty(value = "退款订单号", name = "refundOrderId")
        private String refundOrderId;

        @NotNull(message = "退款金额不可为空")
        @ApiModelProperty(value = "退款金额", name = "refundOrderId")
        private Long amount;

        @NotNull(message = "商铺编号不可为空")
        @ApiModelProperty(value = "商铺编号", name = "storeId")
        private Integer storeId;

        @NotNull(message = "回调地址不可为空")
        @ApiModelProperty(value = "回调地址", name = "notifyUrl")
        private String notifyUrl;

        @NotNull(message = "原订单交易日期不可为空")
        @ApiModelProperty(value = "原订单交易日期", name = "payTime")
        private LocalDateTime payTime;

        @ApiModelProperty(value = "原交易订单编号", name = "orderNo")
        private String orderNo;

        @ApiModelProperty(value = "币种", name = "transactionCurrencyCode")
        private Integer transactionCurrencyCode;

        @ApiModelProperty(value = "描述", name = "description")
        private String description;


        /**
         * 支付来源
         * 产品线
         * 1. 盈利多
         * 2. 好柿民
         * 3. 智慧菜场
         */
        @ApiModelProperty(value = "支付来源", name = "paySource")
        private String paySource;

        /**
         * 业务类型
         * 业务线
         * 1. POS支付
         * 2. 代付
         * 3. 划扣
         * 4. 红包雨
         * 5. O2O
         */
        @ApiModelProperty(value = "业务类型", name = "businessType")
        private String businessType;
    }
}
