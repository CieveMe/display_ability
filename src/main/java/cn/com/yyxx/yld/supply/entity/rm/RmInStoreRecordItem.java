package cn.com.yyxx.yld.supply.entity.rm;

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
 * 入库单明细项
 * </p>
 *
 * @author hz
 * @since 2019-02-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("rm_in_store_record_item")
public class RmInStoreRecordItem extends Model<RmInStoreRecordItem> {

    private static final long serialVersionUID = 1L;

    /**
     * 入库单明细项ID
     */
    @TableId(value = "isri_id", type = IdType.AUTO)
    private Long isriId;

    /**
     * 明细编号
     */
    @TableField("isri_code")
    private String isriCode;

    /**
     * 入库记录ID
     */
    @TableField("isri_isro_id")
    private Integer isriIsroId;

    /**
     * 商品ID
     */
    @TableField("isri_mpbi_id")
    private Long isriMpbiId;

    /**
     * 商品条码
     */
    @TableField("isri_bar_code")
    private String isriBarCode;

    /**
     * 商品多码
     */
    @TableField("isri_bar_codes")
    private String isriCarCodes;

    /**
     * 商品名称
     */
    @TableField("isri_name")
    private String isriName;

    /**
     * 拼音缩写
     */
    @TableField("isri_short_pinyin")
    private String isriShortPinyin;

    /**
     * 商品简码
     */
    @TableField("isri_short_code")
    private String isriShortCode;

    /**
     * 商品单位
     */
    @TableField("isri_unit")
    private String isriUnit;

    /**
     * 商品规格
     */
    @TableField("isri_size")
    private String isriSize;

    /**
     * 商品分类
     */
    @TableField("isri_category")
    private String isriCategory;

    /**
     * 批发价
     */
    @TableField("isri_wholesale_price")
    private BigDecimal isriWholesalePrice;

    /**
     * 零售价
     */
    @TableField("isri_retail_price")
    private BigDecimal isriRetailPrice;

    /**
     * 进价
     */
    @TableField("isri_cost_price")
    private BigDecimal isriCostPrice;

    /**
     * 原库存数
     */
    @TableField("isri_originali_stock_number")
    private BigDecimal isriOriginaliStockNumber;

    /**
     * 入库数量
     */
    @TableField("isri_warehouse_number")
    private BigDecimal isriWarehouseNumber;

    /**
     * 新库存数
     */
    @TableField("isri_inventory_number")
    private BigDecimal isriInventoryNumber;

    /**
     * 入库金额
     */
    @TableField("isri_account")
    private BigDecimal isriAccount;

    /**
     * 创建时间
     */
    @TableField("isri_create_time")
    private LocalDateTime isriCreateTime;

    /**
     * 修改时间
     */
    @TableField("isri_modify_time")
    private LocalDateTime isriModifyTime;

    /**
     * 是否删除
     */
    @TableField("isri_is_delete")
    private Boolean isriIsDelete;

    /**
     * 排序
     */
    @TableField("isri_sort")
    private Integer isriSort;

    /**
     * 描述
     */
    @TableField("isri_description")
    private String isriDescription;

    /**
     * 备注
     */
    @TableField("isri_remark")
    private String isriRemark;


    public static final String ISRI_ID = "isri_id";

    public static final String ISRI_CODE = "isri_code";

    public static final String ISRI_ISRO_ID = "isri_isro_id";

    public static final String ISRI_MPBI_ID = "isri_mpbi_id";

    public static final String ISRI_BAR_CODE = "isri_bar_code";

    public static final String ISRI_BAR_CODES = "isri_bar_codes";

    public static final String ISRI_NAME = "isri_name";

    public static final String ISRI_SHORT_PINYIN = "isri_short_pinyin";

    public static final String ISRI_SHORT_CODE = "isri_short_code";

    public static final String ISRI_UNIT = "isri_unit";

    public static final String ISRI_SIZE = "isri_size";

    public static final String ISRI_CATEGORY = "isri_category";

    public static final String ISRI_WHOLESALE_PRICE = "isri_wholesale_price";

    public static final String ISRI_RETAIL_PRICE = "isri_retail_price";

    public static final String ISRI_COST_PRICE = "isri_cost_price";

    public static final String ISRI_ORIGINALI_STOCK_NUMBER = "isri_originali_stock_number";

    public static final String ISRI_WAREHOUSE_NUMBER = "isri_warehouse_number";

    public static final String ISRI_INVENTORY_NUMBER = "isri_inventory_number";

    public static final String ISRI_ACCOUNT = "isri_account";

    public static final String ISRI_CREATE_TIME = "isri_create_time";

    public static final String ISRI_MODIFY_TIME = "isri_modify_time";

    public static final String ISRI_IS_DELETE = "isri_is_delete";

    public static final String ISRI_SORT = "isri_sort";

    public static final String ISRI_DESCRIPTION = "isri_description";

    public static final String ISRI_REMARK = "isri_remark";

    @Override
    protected Serializable pkVal() {
        return this.isriId;
    }

}
