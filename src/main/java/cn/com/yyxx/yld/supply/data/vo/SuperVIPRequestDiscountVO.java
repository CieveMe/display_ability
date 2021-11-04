package cn.com.yyxx.yld.supply.data.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

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
@ApiModel(value = "折扣请求VO对象", description = "折扣请求VO对象")
public class SuperVIPRequestDiscountVO implements Serializable {

    private static final long serialVersionUID = 754727617232795515L;


    @NotNull(message = "门店id不可为空")
    @ApiModelProperty(value = "门店id", name = "storeId")
    private String storeId;

    @NotNull(message = "支付码不可为空")
    @ApiModelProperty(value = "支付码", name = "cardNo")
    private Integer cardNo;
}
