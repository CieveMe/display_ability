package cn.com.yyxx.yld.supply.data.dto;


import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author Administrator
 */
@Data
public class ProductItem {

    /**
     * 商品变更数量    changeStokNum  mpbiStockNumber 只能存在一个值
     */
    BigDecimal changeStokNum;


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


    /**
     * 是否参与积分
     */
    private Boolean mpbiIsIntegral;

    /**
     * 是否参与任何优惠活动
     */
    private Boolean mpbiIsSpecialOffer;

    /**
     * 是否是推荐商品
     */
    private Boolean mpbiIsRecommend;

    /**
     * 标准商品实体
     */
    private StockProductInfoLibDTO lib;
}
