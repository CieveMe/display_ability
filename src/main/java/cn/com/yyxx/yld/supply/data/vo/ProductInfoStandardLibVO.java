package cn.com.yyxx.yld.supply.data.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 商品信息标准库
 * </p>
 * @author hk
 * @since 2019-01-24
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class ProductInfoStandardLibVO implements Serializable {


    private static final long serialVersionUID = -2864795070403383214L;

    /**
     * 商品ID
     */
    @JsonIgnore
    private Long pislId;

    /**
     * 商品编号
     */
    private String pislNo;

    /**
     * 分类ID
     */
    private String pislCategoryNo;

    /**
     * 分类名称
     */
    private String pislCategoryName;

    /**
     * 品牌ID
     */
    private String pislBrand;

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
     * 商品单位
     */
    private String pislUnitName;

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
     * 原产国
     */
    private String pislOriginCountry;

    /**
     * 保质期(天)
     */
    private Integer pislShelfLife;

    /**
     * 是否为基本单元
     */
    private Boolean pislIsElementaryUnit;

    /**
     * 建议零售价
     */
    private BigDecimal pislSuggestedRetailPrice;

    /**
     * 建议成本价
     */
    private BigDecimal pislSuggestedCostPrice;

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
     * 条码配置
     */
    private List<PmProductBarCodeConfigVO> codes;
}
