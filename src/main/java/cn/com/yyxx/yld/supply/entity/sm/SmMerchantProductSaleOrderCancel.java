package cn.com.yyxx.yld.supply.entity.sm;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p></p>
 *
 * @author DkHan-ZhangShengYi
 * @version 1.0.0
 * @program yld-supply-v2
 * @description 单品退款订单记录
 * @date 2021-07-22 11:41
 * @since 0.1.0
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sm_merchant_product_sale_order_cancel_data")
public class SmMerchantProductSaleOrderCancel extends Model<SmMerchantProductSaleOrderCancel> {

    private static final long serialVersionUID = 1L;

    /**
     * 订单记录ID
     */
    @TableId(value = "mpso_cancel_id", type = IdType.AUTO)
    private Long mpsoCancelId;

    /**
     * 订单ID
     */
    @TableField(value = "mpso_cancel_order_id")
    private Long mpsoCancelOrderId;

    /**
     * 门店ID
     */
    @TableField("mpso_cancel_sbi_id")
    private Integer mpsoCancelSbiId;

    /**
     * 门店编号
     */
    @TableField("mpso_cancel_sbi_code")
    private String mpsoCancelSbiCode;

    /**
     * 门店名称
     */
    @TableField("mpso_cancel_store_name")
    private String mpsoCancelStoreName;

    /**
     * 订单编号
     */
    @TableField("mpso_cancel_order_no")
    private String mpsoCancelOrderNo;

    /**
     * 订单单号
     */
    @TableField("mpso_cancel_single_no")
    private String mpsoCancelSingleNo;

    /**
     * 订单时间
     */
    @TableField("mpso_cancel_order_time")
    private LocalDateTime mpsoCancelOrderTime;

    /**
     * 支付方式
     */
    @TableField("mpso_cancel_payment_method")
    private String mpsoCancelPaymentMethod;

    /**
     * 应收金额
     */
    @TableField("mpso_cancel_total_price")
    private BigDecimal mpsoCancelTotalPrice;

    /**
     * 实付金额
     */
    @TableField("mpso_cancel_actual_price")
    private BigDecimal mpsoCancelActualPrice;

    /**
     * 折扣
     */
    @TableField("mpso_cancel_discount")
    private BigDecimal mpsoCancelDiscount;

    /**
     * 实际折扣
     */
    @TableField("mpso_cancel_real_discount")
    private BigDecimal mpsoCancelRealDiscount;

    /**
     * 实际折扣
     */
    @TableField("mpso_cancel_order_status")
    private String mpsoCancelOrderStatus;

    /**
     * 扫码渠道
     */
    @TableField("mpso_cancel_scan_code_channel")
    private String mpsoCancelScanCodeChannel;




}
