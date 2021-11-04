package cn.com.yyxx.yld.supply.data.vo;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author hz
 * @version 1.0
 * @since 2019/2/14 16:23
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("商家商品信息")
public class PmMerchantProductBasicInfoVO implements Serializable {

    private static final long serialVersionUID = 8879029700708575634L;

    /**
     * 商品ID
     */
    private Long mpbiId;

    /**
     * 商品库ID
     */
    private Long mpbiPislId;

    /**
     * 门店ID
     */
    private Integer mpbiSbiId;

    /**
     * 商品编号
     */
    private String mpbiNo;

    /**
     * 商品简码(自编码)
     */
    private String mpbiShortCode;

    /**
     * 零售价
     */
    private BigDecimal mpbiRetailPrice;

    /**
     * 会员价
     */
    private BigDecimal mpbiMemberPrice;

    /**
     * 批发价
     */
    private BigDecimal mpbiWholesalePrice;

    /**
     * 成本价
     */
    private BigDecimal mpbiCostPrice;

    /**
     * 库存数量(允许负数)
     */
    private BigDecimal mpbiStockNumber;

    /**
     * 创建时间
     */
    private LocalDateTime mpbiCreateTime;

    /**
     * 修改时间
     */
    private LocalDateTime mpbiModifyTime;

    /**
     * 是否删除
     */
    private Boolean mpbiIsDelete;

    /**
     * 描述
     */
    private String mpbiDescription;

    /**
     * 排序
     */
    private Integer mpbiSort;

    /**
     * 备注
     */
    private String mpbiRemark;
}
