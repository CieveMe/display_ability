package cn.com.yyxx.yld.supply.data.dto;

import lombok.Data;

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
 * @since 2019/6/25 15:25
 */
@Data
public class StockProductInfoLibDTO implements Serializable {

    private static final long serialVersionUID = -5501995688080645621L;


    /**
     * 简码
     */
    private String shortCode;

    /**
     * 商品ID
     */
    private Long pislId;

    /**
     * 分类ID
     */
    private Integer pislCategory;

    /**
     * 分类编号
     */
    private String pislCategoryNo;

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
