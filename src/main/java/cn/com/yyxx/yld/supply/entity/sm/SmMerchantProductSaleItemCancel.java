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

/**
 * <p></p>
 *
 * @author DkHan-ZhangShengYi
 * @version 1.0.0
 * @program yld-supply-v2
 * @description 单品退款商品记录
 * @date 2021-07-22 14:23
 * @since 0.1.0
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sm_merchant_product_sale_item_cancel_data")
public class SmMerchantProductSaleItemCancel extends Model<SmMerchantProductSaleItemCancel> {

    private static final long serialVersionUID = 1L;

    /**
     * 单品退款商品记录ID
     */
    @TableId(value = "mpsi_cancel_id", type = IdType.AUTO)
    private Long mpsiCancelId;

    /**
     * 退款ID
     */
    @TableField("mpsi_mpso_cancel_id")
    private Long mpsiMpsoCancelId;

    /**
     * 订单ID
     */
    @TableField("mpsi_mpso_cancel_order_id")
    private Long mpsiMpsoCancelOrderId;


    /**
     * 商品ID
     */
    @TableField("mpsi_cancel_mpbi_id")
    private Long mpsiCancelMpbiId;

    /**
     * 商品名称
     */
    @TableField("mpsi_cancel_name")
    private String mpsiCancelName;

    /**
     * 商品原价
     */
    @TableField("mpsi_cancel_retail_price")
    private BigDecimal mpsiCancelRetailPrice;

    /**
     * 商品现价
     */
    @TableField("mpsi_cancel_now_price")
    private BigDecimal mpsiCancelNowPrice;

    /**
     * 商品数量
     */
    @TableField("mpsi_cancel_num")
    private BigDecimal mpsiCancelNum;

    /**
     * 商品折后小计
     */
    @TableField("mpsi_cancel_sub_total")
    private BigDecimal mpsiCancelSubTotal;

    /**
     * 商品是否退款
     */
    @TableField("mpsi_cancel_is_refund")
    private Boolean mpsiCancelIsRefund;

    /**
     * 商品退款类型
     */
    @TableField("mpsi_cancel_is_refund_type")
    private Integer mpsiCancelIsRefundType;


    /**
     * 退款方式(1 现金收款 2 原路返回 3 微信转账 4 支付宝转账  )
     */
    @TableField("mpsi_cancel_refund_method")
    private Integer mpsiCancelRefundMethod;


    /**
     * 退货原因
     */
    @TableField("mpsi_cancel_refund_reason")

    private String mpsiCancelRefundReason;

}
