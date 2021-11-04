package cn.com.yyxx.yld.supply.data.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>超级会员支付响应VO</p>
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
public class SuperVIPResponsePayVO implements Serializable {

    private static final long serialVersionUID = 754727617232795515L;
    /**
     *成功：S0A000 失败：非S0A000
     */
    @NotNull(message = "是否成功")
    @ApiModelProperty(value = "是否成功", name = "code")
    String code;
    @NotNull(message = "異常信息")
    @ApiModelProperty(value = "異常信息", name = "msg")
    String msg;
    @NotNull(message = "是否成功")
    @ApiModelProperty(value = "是否成功", name = "data")
    boolean data;
}
