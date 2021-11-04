package cn.com.yyxx.yld.supply.data.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 *
 * </P>
 *
 * @author jiaorui
 * @date 2019/3/6 19:44
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("门店商品拆分基础信息")
public class SmStoreProductRuleVO {

    /**
     * 商品拆分规则总数
     */
    @ApiModelProperty("商品拆分规则总数")
    private Integer productRulNum;

    /**
     * 自建商品拆分规则数量
     */
    @ApiModelProperty("自建商品拆分规则数量")
    private Integer oneselfProductRulNum;

    /**
     * 平台商品拆分规则数量
     */
    @ApiModelProperty("平台商品拆分规则数量")
    private Integer platformProductRulNum;
}
