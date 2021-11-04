package cn.com.yyxx.yld.supply.entity.pm;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 采购订单明细项
 * </p>
 *
 * @author linmeng
 * @since 2019-03-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("pm_purchase_item_info")
public class PmPurchaseItemInfo extends Model<PmPurchaseItemInfo> {

    private static final long serialVersionUID = 1L;

    /**
     * 明细ID
     */
    @TableId(value = "pii_id", type = IdType.AUTO)
    private Long piiId;

    /**
     * 明细编号
     */
    @TableField("pii_no")
    private String piiNo;

    /**
     * 采购订单ID
     */
    @TableField("pii_poi_id")
    private Long piiPoiId;

    /**
     * 商品ID
     */
    @TableField("pii_mpbi_id")
    private Long piiMpbiId;

    /**
     * 商品分类
     */
    @TableField("pii_category")
    private String piiCategory;

    /**
     * 商品编码
     */
    @TableField("pii_product_code")
    private String piiProductCode;

    /**
     * 商品条码
     */
    @TableField("pii_bar_code")
    private String piiBarCode;

    /**
     * 商品条码
     */
    @TableField("pii_bar_codes")
    private String piiBarCodes;

    /**
     * 商品拼音
     */
    @TableField("pii_short_pinyin")
    private String piiShortPinyin;

    /**
     * 商品简码
     */
    @TableField("pii_short_code")
    private String piiShortCode;

    /**
     * 商品名称
     */
    @TableField("pii_name")
    private String piiName;

    /**
     * 商品单位
     */
    @TableField("pii_unit")
    private String piiUnit;

    /**
     * 商品规格
     */
    @TableField("pii_size")
    private String piiSize;

    /**
     * 数量
     */
    @TableField("pii_num")
    private BigDecimal piiNum;

    /**
     * 允许小数
     */
    @TableField("pii_num_allow_decimal")
    private Boolean piiNumAllowDecimal;

    /**
     * 零售价
     */
    @TableField("pii_retail_price")
    private BigDecimal piiRetailPrice;

    /**
     * 批发价
     */
    @TableField("pii_wholesale_price")
    private BigDecimal piiWholesalePrice;

    /**
     * 总金额
     */
    @TableField("pii_sub_total")
    private BigDecimal piiSubTotal;

    /**
     * 预计盈利
     */
    @TableField("pii_expected_payoff")
    private BigDecimal piiExpectedPayoff;

    /**
     * 创建时间
     */
    @TableField("pii_create_time")
    private LocalDateTime piiCreateTime;

    /**
     * 修改时间
     */
    @TableField("pii_modify_time")
    private LocalDateTime piiModifyTime;

    /**
     * 是否删除
     */
    @TableField("pii_is_delete")
    private Boolean piiIsDelete;

    /**
     * 排序
     */
    @TableField("pii_sort")
    private Integer piiSort;

    /**
     * 描述
     */
    @TableField("pii_description")
    private String piiDescription;

    /**
     * 备注
     */
    @TableField("pii_remark")
    private String piiRemark;


    public static final String PII_ID = "pii_id";

    public static final String PII_NO = "pii_no";

    public static final String PII_POI_ID = "pii_poi_id";

    public static final String PII_MPBI_ID = "pii_mpbi_id";

    public static final String PII_CATEGORY = "pii_category";

    public static final String PII_PRODUCT_CODE = "pii_product_code";

    public static final String PII_BAR_CODE = "pii_bar_code";

    public static final String PII_SHORT_PINYIN = "pii_short_pinyin";

    public static final String PII_SHORT_CODE = "pii_short_code";

    public static final String PII_NAME = "pii_name";

    public static final String PII_UNIT = "pii_unit";

    public static final String PII_SIZE = "pii_size";

    public static final String PII_NUM = "pii_num";

    public static final String PII_NUM_ALLOW_DECIMAL = "pii_num_allow_decimal";

    public static final String PII_RETAIL_PRICE = "pii_retail_price";

    public static final String PII_WHOLESALE_PRICE = "pii_wholesale_price";

    public static final String PII_SUB_TOTAL = "pii_sub_total";

    public static final String PII_EXPECTED_PAYOFF = "pii_expected_payoff";

    public static final String PII_CREATE_TIME = "pii_create_time";

    public static final String PII_MODIFY_TIME = "pii_modify_time";

    public static final String PII_IS_DELETE = "pii_is_delete";

    public static final String PII_SORT = "pii_sort";

    public static final String PII_DESCRIPTION = "pii_description";

    public static final String PII_REMARK = "pii_remark";

    @Override
    protected Serializable pkVal() {
        return this.piiId;
    }

}
