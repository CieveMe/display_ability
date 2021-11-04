package cn.com.yyxx.yld.supply.data.vo;

import cn.com.yyxx.yld.supply.data.dto.RequestParent;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>支付请求RequestVO</p>
 *
 * @author 東
 * @version 1.0.0
 * @program yld-micro-service-model
 * @description 支付请求RequestVO
 * @date 2020-02-24 14:09
 * @since 0.1.0
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value="支付请求VO对象", description="支付请求VO对象")
public class RequestPayVO extends RequestParent implements Serializable {

    private static final long serialVersionUID = 754727617232795515L;

    @Valid
    @NotNull
    @ApiModelProperty(value = "订单信息", name = "order")
    private RequestPayOrder order;

    public RequestPayOrder getOrder() {
        return order;
    }

    public void setOrder(RequestPayOrder order) {
        this.order = order;
    }

    @Data
    @ApiModel(value="支付请求订单对象", description="支付请求订单对象")
    public static class RequestPayOrder {

        @NotNull(message = "金额不可为空")
        @ApiModelProperty(value = "金额", name = "amount")
        private Long amount;

        @ApiModelProperty(value = "交易币种", name = "transactionCurrencyCode")
        private int transactionCurrencyCode;

        @NotNull(message = "订单号不可为空")
        @ApiModelProperty(value = "订单号", name = "orderId")
        private String orderId;

        @NotNull(message = "客户端订单号不可为空")
        @ApiModelProperty(value = "客户端订单号", name = "orderNo")
        private String orderNo;

        @ApiModelProperty(value = "订单描述", name = "description")
        private String description;

        @ApiModelProperty(value = "支付授权码", name = "authCode")
        private String authCode;

        @ApiModelProperty(value = "商铺编号", name = "storeId")
        private Integer storeId;

        @ApiModelProperty(value = "回调地址", name = "notifyUrl")
        private String notifyUrl;

        @ApiModelProperty(value = "openId回调地址", name = "notifyOpenId")
        private String notifyOpenId;

        @ApiModelProperty(value = "设备IP", name = "termIp")
        private String termIp;

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
