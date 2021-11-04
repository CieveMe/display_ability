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
 *  商家商品和标准商品信息
 * </p>
 *
 * @author linmeng
 * @version 1.0
 * @since 2019/2/27 17:40
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("商家商品和标准商品信息")
public class PmMerchantProductBasicAndLibVO implements Serializable {

    private static final long serialVersionUID = -2363771324903600467L;
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
     * 商品ID
     */
    private Long pislId;

    /**
     * 分类ID
     */
    private Integer pislCategory;

    /**
     * 父类ids(id-id-id)
     */
    private String pciParentIds;

    /**
     * 分类名(烟草-粗支烟-烤烟)
     */
    private String pciParentNames;

    /**
     * 品牌ID
     */
    private Integer pislBrand;

    /**
     * 商品编号
     */
    private String pislNo;

    /**
     * 是否平台商品
     */
    private Boolean pislIsPlatformProduct;

    /**
     * 条形码
     */
    private String pislBarCode;

    /**
     * 名称
     */
    private String pislName;

    /**
     * 商品拼音
     */
    private String pislPinyin;

    /**
     * 商品拼音
     */
    private String pislShortPinyin;

    /**
     * 图片
     */
    private String pislPicture;

    /**
     * 商品单位
     */
    private String pislUnit;

    /**
     * 是否是称重
     */
    private Boolean pislIsWeigh;

    /**
     * 是不是计件
     */
    private Boolean pislIsPiece;

    /**
     * 规格
     */
    private String pislSize;

    /**
     * 口味
     */
    private String pislTaste;

    /**
     * 宽度(厘米)
     */
    private BigDecimal pislWidth;

    /**
     * 高度(厘米)
     */
    private BigDecimal pislHeight;

    /**
     * 深度(厘米)
     */
    private BigDecimal pislDepth;

    /**
     * 原产国
     */
    private Integer pislOriginCountry;

    /**
     * 保质期(天)
     */
    private Integer pislShelfLife;

    /**
     * 是否为基本单元
     */
    private Boolean pislIsElementaryUnit;

    /**
     * 关键字
     */
    private String pislKeyword;

    /**
     * 毛重
     */
    private BigDecimal pislRoughWeight;

    /**
     * 建议零售价
     */
    private BigDecimal pislSuggestedRetailPrice;

    /**
     * 建议成本价
     */
    private BigDecimal pislSuggestedCostPrice;

    /**
     * 形态描述
     */
    private String pislMorphologicalDescription;

    /**
     * 产地代码
     */
    private String pislOriginCode;

    /**
     * 创建时间
     */
    private LocalDateTime pislCreateTime;

    /**
     * 修改时间
     */
    private LocalDateTime pislModifyTime;

    /**
     * 是否删除
     */
    private Boolean pislIsDelete;

    /**
     * 描述
     */
    private String pislDescription;

    /**
     * 排序
     */
    private Integer pislSort;

    /**
     * 备注
     */
    private String pislRemark;
}
