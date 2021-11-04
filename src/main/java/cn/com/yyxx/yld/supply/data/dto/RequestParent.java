package cn.com.yyxx.yld.supply.data.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * <p></p>
 *
 * @author 東
 * @version 1.0.0
 * @program yld-micro-service-model
 * @description 支付请求父类
 * @date 2020-02-24 16:38
 * @since 0.1.0
 **/
@Data
@ApiModel(value="支付请求父类VO对象", description="支付请求父类VO对象")
public class RequestParent {

    @Valid
    @NotNull(message = "协议类型不可为空")
    @ApiModelProperty(value = "协议类型", name = "protocol")
    private String protocol;

}
