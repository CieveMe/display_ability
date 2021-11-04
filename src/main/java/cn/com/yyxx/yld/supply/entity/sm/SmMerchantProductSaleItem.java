package cn.com.yyxx.yld.supply.entity.sm;

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
 * 商家商品销售品项表
 * </p>
 *
 * @author linmeng
 * @since 2019-03-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sm_merchant_product_sale_item")
public class SmMerchantProductSaleItem extends Model<SmMerchantProductSaleItem> {

    private static final long serialVersionUID = 1L;

    /**
     * 品项ID
     */
    @TableId(value = "mpsi_id", type = IdType.AUTO)
    private Long mpsiId;

    /**
     * 品项编号
     */
    @TableField("mpsi_no")
    private String mpsiNo;

    /**
     * 商品ID
     */
    @TableField("mpsi_mpbi_id")
    private Long mpsiMpbiId;

    /**
     * 标准商品ID
     */
    @TableField("mpsi_pisl_id")
    private Long mpsiPislId;

    /**
     * 订单ID
     */
    @TableField("mpsi_mpso_id")
    private Long mpsiMpsoId;

    /**
     * 分类ID
     */
    @TableField("mpsi_pci_id")
    private Integer mpsiPciId;

    /**
     * 一级分类
     */
    @TableField("mpsi_first_pci_id")
    private Integer mpsiFirstPciId;

    /**
     * 一级分类名称
     */
    @TableField("mpsi_first_pci_name")
    private String mpsiFirstPciName;

    /**
     * 二级分类
     */
    @TableField("mpsi_second_pci_id")
    private Integer mpsiSecondPciId;

    /**
     * 二级分类名称
     */
    @TableField("mpsi_second_pci_name")
    private String mpsiSecondPciName;

    /**
     * 商品条码
     */
    @TableField("mpsi_code")
    private String mpsiCode;

    /**
     * 商品多码
     */
    @TableField("mpsi_bar_codes")
    private String mpsiBarCodes;

    /**
     * 商品名称
     */
    @TableField("mpsi_name")
    private String mpsiName;

    /**
     * 商品单位
     */
    @TableField("mpsi_unit")
    private String mpsiUnit;

    /**
     * 商品规格
     */
    @TableField("mpsi_size")
    private String mpsiSize;

    /**
     * 原价
     */
    @TableField("mpsi_retail_price")
    private BigDecimal mpsiRetailPrice;

    /**
     * 折扣
     */
    @TableField("mpsi_discount")
    private BigDecimal mpsiDiscount;

    /**
     * 数量
     */
    @TableField("mpsi_num")
    private BigDecimal mpsiNum;

    /**
     * 允许小数
     */
    @TableField("mpsi_num_allow_decimal")
    private Boolean mpsiNumAllowDecimal;

    /**
     * 现价
     */
    @TableField("mpsi_now_price")
    private BigDecimal mpsiNowPrice;

    /**
     * 进价
     */
    @TableField("mpsi_wholesale_price")
    private BigDecimal mpsiWholesalePrice;

    /**
     * 进价
     */
    @TableField("mpsi_discount_total")
    private BigDecimal mpsiDiscountTotal;

    /**
     * 小计
     */
    @TableField("mpsi_sub_total")
    private BigDecimal mpsiSubTotal;

    /**
     * 是否退款
     */
    @TableField("mpsi_is_refund")
    private Boolean mpsiIsRefund;

    /**
     * 退款类型（1 退货退款,2 仅退款,3 仅退货）
     */
    @TableField("mpsi_is_refund_type")
    private Integer mpsiIsRefundType;


    /**
     * 退款方式(1 现金收款 2 原路返回 3 微信转账 4 支付宝转账  )
     */
    @TableField("mpsi_refund_method")
    private Integer mpsiRefundMethod;


    /**
     * 退货原因
     */
    @TableField("mpsi_refund_reason")

    private String mpsiRefundReason;


    /**
     * 创建时间
     */
    @TableField("mpsi_create_time")
    private LocalDateTime mpsiCreateTime;

    /**
     * 修改时间
     */
    @TableField("mpsi_modify_time")
    private LocalDateTime mpsiModifyTime;

    /**
     * 是否删除
     */
    @TableField("mpsi_is_delete")
    private Boolean mpsiIsDelete;

    /**
     * 是否是临时商品
     */
    @TableField("mpsi_is_temp")
    private Boolean mpsiIsTemp;

    /**
     * 排序
     */
    @TableField("mpsi_sort")
    private Integer mpsiSort;

    /**
     * 描述
     */
    @TableField("mpsi_description")
    private String mpsiDescription;

    /**
     * 备注
     */
    @TableField("mpsi_remark")
    private String mpsiRemark;


    public static final String MPSI_ID = "mpsi_id";

    public static final String MPSI_NO = "mpsi_no";

    public static final String MPSI_MPBI_ID = "mpsi_mpbi_id";

    public static final String MPSI_PISL_ID = "mpsi_pisl_id";

    public static final String MPSI_MPSO_ID = "mpsi_mpso_id";

    public static final String MPSI_PCI_ID = "mpsi_pci_id";

    public static final String MPSI_FIRST_PCI_ID = "mpsi_first_pci_id";

    public static final String MPSI_FIRST_PCI_NAME = "mpsi_first_pci_name";

    public static final String MPSI_SECOND_PCI_ID = "mpsi_second_pci_id";

    public static final String MPSI_SECOND_PCI_NAME = "mpsi_second_pci_name";

    public static final String MPSI_CODE = "mpsi_code";

    public static final String MPSI_BAR_CODES = "mpsi_bar_codes";

    public static final String MPSI_NAME = "mpsi_name";

    public static final String MPSI_UNIT = "mpsi_unit";

    public static final String MPSI_SIZE = "mpsi_size";

    public static final String MPSI_RETAIL_PRICE = "mpsi_retail_price";

    public static final String MPSI_DISCOUNT = "mpsi_discount";

    public static final String MPSI_NUM = "mpsi_num";

    public static final String MPSI_NUM_ALLOW_DECIMAL = "mpsi_num_allow_decimal";

    public static final String MPSI_NOW_PRICE = "mpsi_now_price";

    public static final String MPSI_WHOLESALE_PRICE = "mpsi_wholesale_price";

    public static final String MPSI_DISCOUNT_TOTAL = "mpsi_discount_total";

    public static final String MPSI_SUB_TOTAL = "mpsi_sub_total";

    public static final String MPSI_IS_REFUND = "mpsi_is_refund";

    public static final String MPSI_REFUND_METHOD = "mpsi_refund_method";

    public static final String MPSI_REFUND_REASON = "mpsi_refund_reason";

    public static final String MPSI_CREATE_TIME = "mpsi_create_time";

    public static final String MPSI_MODIFY_TIME = "mpsi_modify_time";

    public static final String MPSI_IS_DELETE = "mpsi_is_delete";

    public static final String MPSI_SORT = "mpsi_sort";

    public static final String MPSI_DESCRIPTION = "mpsi_description";

    public static final String MPSI_REMARK = "mpsi_remark";

    @Override
    protected Serializable pkVal() {
        return this.mpsiId;
    }

}
