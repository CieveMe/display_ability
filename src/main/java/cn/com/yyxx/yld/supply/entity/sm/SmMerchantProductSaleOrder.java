package cn.com.yyxx.yld.supply.entity.sm;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Transient;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 商家商品销售订单表
 * </p>
 *
 * @author linmeng
 * @since 2019-03-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sm_merchant_product_sale_order")
public class SmMerchantProductSaleOrder extends Model<SmMerchantProductSaleOrder> {

    private static final long serialVersionUID = 1L;

    /**
     * 总优惠金额
     */
    @TableField(exist = false)
    private Long discountAmount;

    /**
     * 门店优惠金额
     */
    @TableField(exist = false)
    private Long storeDiscountAmount;

    /**
     * 超级会员优惠金额
     */
    @TableField(exist = false)
    private Long memberDiscountAmount;

    /**
     * 超级会员折扣
     */
    @TableField(exist = false)
    private Long discount;

    /**
     * 超级会员支付码
     */
    @TableField(exist = false)
    private String payCode;

    /**
     * 订单ID
     */
    @TableId(value = "mpso_id", type = IdType.AUTO)
    private Long mpsoId;

    /**
     * 门店ID
     */
    @TableField("mpso_sbi_id")
    private Integer mpsoSbiId;

    /**
     * 门店省份
     */
    @TableField("mpso_sbi_province_id")
    private Integer mpsoSbiProvinceId;

    /**
     * 门店城市
     */
    @TableField("mpso_sbi_city_id")
    private Integer mpsoSbiCityId;

    /**
     * 门店区县
     */
    @TableField("mpso_sbi_area_id")
    private Integer mpsoSbiAreaId;

    /**
     * 店铺类型
     */
    @TableField("mpso_sbi_type")
    private String mpsoSbiType;

    /**
     * 门店编号
     */
    @TableField("mpso_sbi_code")
    private String mpsoSbiCode;

    /**
     * 门店名称
     */
    @TableField("mpso_store_name")
    private String mpsoStoreName;

    /**
     * 设备ID
     */
    @TableField("mpso_sdc_id")
    private Integer mpsoSdcId;

    /**
     * 设备名称
     */
    @TableField("mpso_client_name")
    private String mpsoClientName;

    /**
     * 收银员ID
     */
    @TableField("mpso_ubi_id")
    private Long mpsoUbiId;

    /**
     * 收银员姓名
     */
    @TableField("mpso_cashier")
    private String mpsoCashier;

    /**
     * 收银员工号
     */
    @TableField("mpso_cashier_no")
    private String mpsoCashierNo;

    /**
     * 订单编号
     */
    @TableField("mpso_order_no")
    private String mpsoOrderNo;

    /**
     * 订单单号
     */
    @TableField("mpso_single_no")
    private String mpsoSingleNo;

    /**
     * 订单流水号
     */
    @TableField("mpso_serial_no")
    private String mpsoSerialNo;

    /**
     * 订单状态
     */
    @TableField("mpso_order_status")
    private String mpsoOrderStatus;

    /**
     * 订单时间
     */
    @TableField("mpso_order_time")
    private LocalDateTime mpsoOrderTime;

    /**
     * 支付方式
     */
    @TableField("mpso_payment_method")
    private String mpsoPaymentMethod;

    /**
     * 支付订单号
     */
    @TableField("mpso_pay_order_id")
    private String mpsoPayOrderId;

    /**
     * 支付账单号
     */
    @TableField("mpso_pay_bill_no")
    private String mpsoPayBillNo;

    /**
     * 支付二维码
     */
    @TableField("mpso_pay_qr_code_id")
    private String mpsoPayQrCodeId;

    /**
     * 扫码渠道
     */
    @TableField("mpso_scan_code_channel")
    private String mpsoScanCodeChannel;

    /**
     * 门店会员ID
     */
    @TableField("mpso_smi_id")
    private Long mpsoSmiId;

    /**
     * 会员电话
     */
    @TableField("mpso_smi_phone")
    private String mpsoSmiPhone;

    /**
     * 会员积分
     */
    @TableField("mpso_owner_integration")
    private Long mpsoOwnerIntegration;

    /**
     * 本次积分
     */
    @TableField("mpso_this_integration")
    private Integer mpsoThisIntegration;

    /**
     *是否计算会员
     */
    @TableField("mpso_is_calculate_member")
    private Boolean mpsoIsCalculateMember;


    /**
     *是否补生成
     */
    @TableField("mpso_is_for_generate")
    private Boolean mpsoIsForGenerate;

    /**
     * 三方买家ID
     */
    @TableField("mpso_third_buyer_id")
    private String mpsoThirdBuyerId;

    /**
     * 三方买家名称
     */
    @TableField("mpso_third_buyer_name")
    private String mpsoThirdBuyerName;

    /**
     * 商品类数
     */
    @TableField("mpso_row_num")
    private Integer mpsoRowNum;

    /**
     * 商品件数
     */
    @TableField("mpso_total_num")
    private Integer mpsoTotalNum;

    /**
     * 应收金额
     */
    @TableField("mpso_total_price")
    private BigDecimal mpsoTotalPrice;

    /**
     * 实际金额
     */
    @TableField("mpso_real_price")
    private BigDecimal mpsoRealPrice;

    /**
     * 应付金额
     */
    @TableField("mpso_should_price")
    private BigDecimal mpsoShouldPrice;

    /**
     * 实付金额
     */
    @TableField("mpso_actual_price")
    private BigDecimal mpsoActualPrice;

    /**
     * 支付金额
     */
    @TableField("mpso_actual_pay_price")
    private BigDecimal mpsoActualPayPrice;

    /**
     * 现金金额
     */
    @TableField("mpso_cash_price")
    private BigDecimal mpsoCashPrice;

    /**
     * 找零金额
     */
    @TableField("mpso_change_price")
    private BigDecimal mpsoChangePrice;

    /**
     * 毛利金额
     */
    @TableField("mpso_gross_profit")
    private BigDecimal mpsoGrossProfit;

    /**
     * 折扣
     */
    @TableField("mpso_discount")
    private BigDecimal mpsoDiscount;

    /**
     * 折扣
     */
    @TableField("mpso_real_discount")
    private BigDecimal mpsoRealDiscount;

    /**
     * 是否免单
     */
    @TableField("mpso_is_free")
    private Boolean mpsoIsFree;

    /**
     * 是否抹角
     */
    @TableField("mpso_is_wipe_jiao")
    private Boolean mpsoIsWipeJiao;

    /**
     * 打印小票
     */
    @TableField("mpso_is_print_receipt")
    private Boolean mpsoIsPrintReceipt;

    /**
     * 打印次数
     */
    @TableField("mpso_print_frequency")
    private Integer mpsoPrintFrequency;

    /**
     * 支付完成
     */
    @TableField("mpso_is_payment")
    private Boolean mpsoIsPayment;

    /**
     * 支付时间
     */
    @TableField("mpso_pay_time")
    private LocalDateTime mpsoPayTime;

    /**
     * 是否退款
     */
    @TableField("mpso_is_refund")
    private Boolean mpsoIsRefund;


    //--------------------------------------------------------------------------------------------------------------
    /**
     * 是否退款(4.0版本判断是否退款专属字段)
     */
    @TableField("mpso_is_refund_four_version")
    private Boolean mpsoIsRefundFourVersion;
    //--------------------------------------------------------------------------------------------------------------

    /**
     * 退款类型（1 退货退款,2 仅退款,3 仅退货）
     */
    @TableField("mpso_is_refund_type")
    private Integer mpsoIsRefundType;


    /**
     * 退款方式(1 现金收款 2 原路返回 3 微信转账 4 支付宝转账  )
     */
    @TableField("mpso_refund_method")
    private Integer mpsoRefundMethod;


    /**
     * 退货原因
     */
    @TableField("mpso_refund_reason")
    private String mpsoRefundReason;


    /**
     * 退款时间
     */
    @TableField("mpso_refund_time")
    private LocalDateTime mpsoRefundTime;

    /**
     * 创建时间
     */
    @TableField("mpso_create_time")
    private LocalDateTime mpsoCreateTime;

    /**
     * 修改时间
     */
    @TableField("mpso_modify_time")
    private LocalDateTime mpsoModifyTime;

    /**
     * 是否删除
     */
    @TableField("mpso_is_delete")
    private Boolean mpsoIsDelete;

    /**
     * 排序
     */
    @TableField("mpso_sort")
    private Integer mpsoSort;

    /**
     * 描述
     */
    @TableField("mpso_description")
    private String mpsoDescription;

    /**
     * 备注
     */
    @TableField("mpso_remark")
    private String mpsoRemark;

    /**
     * 积分抵扣（元）
     */
    @TableField("mpso_amount_ratio")
    private BigDecimal mpsoAmountRatio;

    /**
     * 本次消耗市民分
     */
    @TableField("mpso_civic_integration")
    private Integer mpsoCivicIntegration;

    /**
     * 明细项
     */
    @TableField(exist = false)
    private List<SmMerchantProductSaleItem> items;

    /**
     * 扫码结果
     */
    @TableField(exist = false)
    private List<SmOrderTransCodeResult> results;


    /**
     * 客户端IP
     */
    @TableField("term_ip")
    private String termIp;


    /**
     * scan_type
     */
    @TableField("scan_type")
    private String scanType;

    public static final String MPSO_ID = "mpso_id";

    public static final String MPSO_SBI_ID = "mpso_sbi_id";

    public static final String MPSO_SBI_PROVINCE_ID = "mpso_sbi_province_id";

    public static final String MPSO_SBI_CITY_ID = "mpso_sbi_city_id";

    public static final String MPSO_SBI_AREA_ID = "mpso_sbi_area_id";

    public static final String MPSO_SBI_TYPE = "mpso_sbi_type";

    public static final String MPSO_SBI_CODE = "mpso_sbi_code";

    public static final String MPSO_STORE_NAME = "mpso_store_name";

    public static final String MPSO_SDC_ID = "mpso_sdc_id";

    public static final String MPSO_CLIENT_NAME = "mpso_client_name";

    public static final String MPSO_UBI_ID = "mpso_ubi_id";

    public static final String MPSO_CASHIER = "mpso_cashier";

    public static final String MPSO_CASHIER_NO = "mpso_cashier_no";

    public static final String MPSO_ORDER_NO = "mpso_order_no";

    public static final String MPSO_SINGLE_NO = "mpso_single_no";

    public static final String MPSO_SERIAL_NO = "mpso_serial_no";

    public static final String MPSO_ORDER_STATUS = "mpso_order_status";

    public static final String MPSO_ORDER_TIME = "mpso_order_time";

    public static final String MPSO_PAYMENT_METHOD = "mpso_payment_method";

    public static final String MPSO_SCAN_CODE_CHANNEL = "mpso_scan_code_channel";

    public static final String MPSO_SMI_ID = "mpso_smi_id";

    public static final String MPSO_SMI_PHONE = "mpso_smi_phone";

    public static final String MPSO_OWNER_INTEGRATION = "mpso_owner_integration";

    public static final String MPSO_THIS_INTEGRATION = "mpso_this_integration";

    public static final String MPSO_IS_CALCULATE_MEMBER = "mpso_is_calculate_member";

    public static final String MPSO_IS_FOR_GENERATE = "mpso_is_for_generate";

    public static final String MPSO_ROW_NUM = "mpso_row_num";

    public static final String MPSO_TOTAL_NUM = "mpso_total_num";

    public static final String MPSO_TOTAL_PRICE = "mpso_total_price";

    public static final String MPSO_REAL_PRICE = "mpso_real_price";

    public static final String MPSO_SHOULD_PRICE = "mpso_should_price";

    public static final String MPSO_ACTUAL_PRICE = "mpso_actual_price";

    public static final String MPSO_ACTUAL_PAY_PRICE = "mpso_actual_pay_price";

    public static final String MPSO_CASH_PRICE = "mpso_cash_price";

    public static final String MPSO_CHANGE_PRICE = "mpso_change_price";

    public static final String MPSO_GROSS_PROFIT = "mpso_gross_profit";

    public static final String MPSO_DISCOUNT = "mpso_discount";

    public static final String MPSO_REAL_DISCOUNT = "mpso_real_discount";

    public static final String MPSO_IS_FREE = "mpso_is_free";

    public static final String MPSO_IS_WIPE_JIAO = "mpso_is_wipe_jiao";

    public static final String MPSO_IS_PRINT_RECEIPT = "mpso_is_print_receipt";

    public static final String MPSO_PRINT_FREQUENCY = "mpso_print_frequency";

    public static final String MPSO_IS_PAYMENT = "mpso_is_payment";

    public static final String MPSO_PAY_TIME = "mpso_pay_time";

    public static final String MPSO_IS_REFUND = "mpso_is_refund";

    public static final String MPSO_REFUND_TIME = "mpso_refund_time";

    public static final String MPSO_CREATE_TIME = "mpso_create_time";

    public static final String MPSO_MODIFY_TIME = "mpso_modify_time";

    public static final String MPSO_IS_DELETE = "mpso_is_delete";

    public static final String MPSO_SORT = "mpso_sort";

    public static final String MPSO_DESCRIPTION = "mpso_description";

    public static final String MPSO_REMARK = "mpso_remark";

    public static final String MPSO_AMOUNT_RATIO = "mpso_amount_ratio";

    public static final String MPSO_CIVIC_INTEGRATION = "mpso_civic_integration";

    @Override
    protected Serializable pkVal() {
        return this.mpsoId;
    }

}
