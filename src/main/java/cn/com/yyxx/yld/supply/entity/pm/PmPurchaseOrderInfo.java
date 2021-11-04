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
 * 采购订单
 * </p>
 *
 * @author linmeng
 * @since 2019-03-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("pm_purchase_order_info")
public class PmPurchaseOrderInfo extends Model<PmPurchaseOrderInfo> {

    private static final long serialVersionUID = 1L;

    /**
     * 采购订单ID
     */
    @TableId(value = "poi_id", type = IdType.AUTO)
    private Long poiId;

    /**
     * 门店ID
     */
    @TableField("poi_sbi_id")
    private Integer poiSbiId;

    /**
     * 门店名称
     */
    @TableField("poi_store_name")
    private String poiStoreName;

    /**
     * 门店编号
     */
    @TableField("poi_sbi_code")
    private String poiSbiCode;

    /**
     * 订单类型
     */
    @TableField("poi_type")
    private String poiType;

    /**
     * 终端类型
     */
    @TableField("poi_terminal")
    private String poiTerminal;

    /**
     * 设备ID
     */
    @TableField("poi_sdc_id")
    private Integer poiSdcId;

    /**
     * 设备名称
     */
    @TableField("poi_client_name")
    private String poiClientName;

    /**
     * 操作人员ID
     */
    @TableField("poi_ubi_id")
    private Long poiUbiId;

    /**
     * 操作人姓名
     */
    @TableField("poi_cashier")
    private String poiCashier;

    /**
     * 操作人工号
     */
    @TableField("poi_cashier_no")
    private String poiCashierNo;

    /**
     * 采购订单编号
     */
    @TableField("poi_order_no")
    private String poiOrderNo;

    /**
     * 采购订单单号
     */
    @TableField("poi_single_no")
    private String poiSingleNo;

    /**
     * 采购订单状态
     */
    @TableField("poi_order_status")
    private String poiOrderStatus;

    /**
     * 采购订单流水号
     */
    @TableField("poi_serial_no")
    private String poiSerialNo;

    /**
     * 采购订单时间
     */
    @TableField("poi_order_time")
    private LocalDateTime poiOrderTime;

    /**
     * 支付方式
     */
    @TableField("poi_payment_method")
    private String poiPaymentMethod;

    /**
     * 商品类数
     */
    @TableField("poi_row_num")
    private Integer poiRowNum;

    /**
     * 商品件数
     */
    @TableField("poi_total_num")
    private Integer poiTotalNum;

    /**
     * 订单总金额
     */
    @TableField("poi_total_account")
    private BigDecimal poiTotalAccount;

    /**
     * 是否启用
     */
    @TableField("poi_is_enabled")
    private Boolean poiIsEnabled;

    /**
     * 是否入库
     */
    @TableField("poi_is_in")
    private Boolean poiIsIn;

    /**
     * 毛利金额
     */
    @TableField("poi_gross_profit")
    private BigDecimal poiGrossProfit;

    /**
     * 支付完成
     */
    @TableField("poi_is_payment")
    private Boolean poiIsPayment;

    /**
     * 支付时间
     */
    @TableField("poi_pay_time")
    private LocalDateTime poiPayTime;

    /**
     * 是否退款
     */
    @TableField("poi_is_refund")
    private Boolean poiIsRefund;

    /**
     * 退款时间
     */
    @TableField("poi_refund_time")
    private LocalDateTime poiRefundTime;

    /**
     * 创建时间
     */
    @TableField("poi_create_time")
    private LocalDateTime poiCreateTime;

    /**
     * 修改时间
     */
    @TableField("poi_modify_time")
    private LocalDateTime poiModifyTime;

    /**
     * 是否删除
     */
    @TableField("poi_is_delete")
    private Boolean poiIsDelete;

    /**
     * 排序
     */
    @TableField("poi_sort")
    private Integer poiSort;

    /**
     * 描述
     */
    @TableField("poi_description")
    private String poiDescription;

    /**
     * 备注
     */
    @TableField("poi_remark")
    private String poiRemark;


    public static final String POI_ID = "poi_id";

    public static final String POI_SBI_ID = "poi_sbi_id";

    public static final String POI_STORE_NAME = "poi_store_name";

    public static final String POI_SBI_CODE = "poi_sbi_code";

    public static final String POI_TYPE = "poi_type";

    public static final String POI_TERMINAL = "poi_terminal";

    public static final String POI_SDC_ID = "poi_sdc_id";

    public static final String POI_CLIENT_NAME = "poi_client_name";

    public static final String POI_UBI_ID = "poi_ubi_id";

    public static final String POI_CASHIER = "poi_cashier";

    public static final String POI_CASHIER_NO = "poi_cashier_no";

    public static final String POI_ORDER_NO = "poi_order_no";

    public static final String POI_SINGLE_NO = "poi_single_no";

    public static final String POI_ORDER_STATUS = "poi_order_status";

    public static final String POI_SERIAL_NO = "poi_serial_no";

    public static final String POI_ORDER_TIME = "poi_order_time";

    public static final String POI_PAYMENT_METHOD = "poi_payment_method";

    public static final String POI_ROW_NUM = "poi_row_num";

    public static final String POI_TOTAL_NUM = "poi_total_num";

    public static final String POI_TOTAL_ACCOUNT = "poi_total_account";

    public static final String POI_IS_ENABLED = "poi_is_enabled";

    public static final String POI_IS_IN = "poi_is_in";

    public static final String POI_GROSS_PROFIT = "poi_gross_profit";

    public static final String POI_IS_PAYMENT = "poi_is_payment";

    public static final String POI_PAY_TIME = "poi_pay_time";

    public static final String POI_IS_REFUND = "poi_is_refund";

    public static final String POI_REFUND_TIME = "poi_refund_time";

    public static final String POI_CREATE_TIME = "poi_create_time";

    public static final String POI_MODIFY_TIME = "poi_modify_time";

    public static final String POI_IS_DELETE = "poi_is_delete";

    public static final String POI_SORT = "poi_sort";

    public static final String POI_DESCRIPTION = "poi_description";

    public static final String POI_REMARK = "poi_remark";

    @Override
    protected Serializable pkVal() {
        return this.poiId;
    }

}
