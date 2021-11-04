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
 * <p>支付状态查询请求VO对象</p>
 *
 * @author 東
 * @version 1.0.0
 * @program yld-micro-service-model
 * @description 支付状态查询VO对象
 * @date 2020-02-24 14:32
 * @since 0.1.0
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value="支付状态查询请求VO对象", description="支付状态查询请求VO对象")
public class RequestPayQueryVO extends RequestParent implements Serializable {

    private static final long serialVersionUID = -1800856325380252209L;

    @Valid
    @NotNull
    @ApiModelProperty(value = "订单信息", name = "order")
    private RequestPayQueryOrder order;

    @Data
    @ApiModel(value="支付状态查询订单对象", description="支付状态查询订单对象")
    public static class RequestPayQueryOrder {

        @ApiModelProperty(value = "商铺编号", name = "storeId")
        private Integer storeId;

        @NotNull(message = "订单号不可为空")
        @ApiModelProperty(value = "订单号", name = "orderId")
        private String orderId;

    }
}
