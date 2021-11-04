package cn.com.yyxx.yld.supply.data.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 *
 * </p>
 *
 * @author guoyl
 * @version 1.0
 * @since 2020-05-05 10:22
 */
@Data
public class SmOrderDetailForOwnerVO implements Serializable {

    /**
     * 订单详情ID
     */
    @ApiModelProperty(value = "/**", name = "id", example = "1")
    private Long id;

    /**
     * 商品名称
     */
    @ApiModelProperty(value = "商品名称", name = "name", example = "矿泉水")
    private String name;

    /**
     * 商品数量
     */
    @ApiModelProperty(value = "商品数量", name = "number", example = "2")
    private BigDecimal number;

    /**
     * 单价
     */
    @ApiModelProperty(value = "单价", name = "price", example = "2.00")
    private BigDecimal price;

    /**
     * 小计
     */
    @ApiModelProperty(value = "小计", name = "count", example = "1")
    private BigDecimal count;
}
