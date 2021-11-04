package cn.com.yyxx.yld.supply.data.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


/**
 * <p>
 * 商家商品销售订单VO
 * </p>
 *
 * @author hk
 * @since 2019-02-13
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SmMerchantProductSaleOrderVO implements Serializable{


    private static final long serialVersionUID = -5016088565638698847L;

    /**
     * 超级会员折扣
     */
    //@NotBlank
    private String vipDiscount;

    /**
     * 超级会员优惠金额
     */
    //@NotBlank
    private String vipAmount;

    /**
     * 门店code
     */
    //@NotBlank
    private String sbiCode;

    /**
     * 订单编号
     */
    @NotBlank
    private String mpsoOrderNo;

    /**
     * 支付码
     */
    //@NotBlank
    private String payCode;

    /**
     * 是否已存在
     */
    private Boolean exist;

    /**
     * 门店名称
     */
    private String mpsoStoreName;

    /**
     * 门店编号
     */
    private String mpsoStoreCode;

    /**
     * 店铺类型
     */
    private String mpsoSbiType;

    /**
     * 数量
     */
    private String mpsiNum;

    /**
     *收银员编号
     */
    private String mpsoUbiNo;

    /**
     * 收银员姓名
     */
    private String mpsoCashier;

    /**
     * 收银员工号
     */
    private String mpsoCashierNo;

    /**
     * 订单单号
     */
    @NotBlank
    private String mpsoSingleNo;

    /**
     * 订单流水号
     */
    @NotBlank
    private String mpsoSerialNo;

    /**
     * 订单状态
     */
    @NotBlank
    private String mpsoOrderStatus;

    /**
     * 订单时间
     */
    @NotNull
    private LocalDateTime mpsoOrderTime;

    /**
     * 支付方式
     */
    @NotBlank
    private String mpsoPaymentMethod;
    /**
     * 支付订单号
     */
    private String mpsoPayOrderId;

    /**
     * 支付账单号
     */
    private String mpsoPayBillNo;

    /**
     * 支付二维码
     */
    private String mpsoPayQrCodeId;

    /**
     * 扫码渠道
     */
    private String mpsoScanCodeChannel;

    /**
     * 门店会员ID
     */
    private Long mpsoSmiId;

    /**
     * 会员电话
     */
    private String mpsoSmiPhone;

    /**
     * 会员积分
     */
    private Long mpsoOwnerIntegration;

    /**
     * 三方买家ID
     */
    private String mpsoThirdBuyerId;

    /**
     * 三方买家名称
     */
    private String mpsoThirdBuyerName;

    /**
     * 本次积分
     */
    private Integer mpsoThisIntegration;

    /**
     * 商品类数
     */
    @NotNull
    private Integer mpsoRowNum;

    /**
     * 商品件数
     */
    @NotNull
    private Integer mpsoTotalNum;

    /**
     * 应收金额
     */
    @NotNull
    private BigDecimal mpsoTotalPrice;


    /**
     * 商品原价
     */
    private String mpsiRetailPrice;


    /**
     * 实际金额
     */
    @NotNull
    private BigDecimal mpsoRealPrice;

    /**
     * 折扣金额  订单表
     */
    private BigDecimal mpsoRealPriceAndMpsoDiscount;

    /**
     * 折扣金额  品项表
     */
    private BigDecimal mpsiRealPriceAndMpsoDiscount;

    /**
     * 进价
     */
    private BigDecimal mpsiWholesalePrice;


    /**
     * 利润
     */
    private BigDecimal priceAndPrice;

    /**
     * 应付金额
     */
    @NotNull
    private BigDecimal mpsoShouldPrice;

    /**
     * 实付金额
     */
    @NotNull
    private BigDecimal mpsoActualPrice;

    /**
     * 支付金额
     */
    private BigDecimal mpsoActualPayPrice;

    /**
     * 现金金额
     */
    private BigDecimal mpsoCashPrice;

    /**
     * 找零金额
     */
    private BigDecimal mpsoChangePrice;

    /**
     * 毛利金额
     */
    private BigDecimal mpsoGrossProfit;

    /**
     * 折扣
     */
    @NotNull
    private BigDecimal mpsoDiscount;

    /**
     * 实际折扣
     */
    @NotNull
    private BigDecimal mpsoRealDiscount;

    /**
     * 积分抵扣（元）
     */
    private BigDecimal mpsoAmountRatio;

    /**
     * 本次消耗市民分
     */
    private Integer mpsoCivicIntegration;

    /**
     * 是否免单
     */
    @NotNull
    private Boolean mpsoIsFree;

    /**
     * 是否抹角
     */
    @NotNull
    private Boolean mpsoIsWipeJiao;

    /**
     * 打印小票
     */
    @NotNull
    private Boolean mpsoIsPrintReceipt;

    /**
     * 打印次数
     */
    @NotNull
    private Integer mpsoPrintFrequency;

    /**
     * 支付完成
     */
    @NotNull
    private Boolean mpsoIsPayment;

    /**
     * 支付时间
     */
    private LocalDateTime mpsoPayTime;

    /**
     * 退款完成
     */
    @NotNull
    private Boolean mpsoIsRefund;

    /**
     * 退款时间
     */
    private LocalDateTime mpsoRefundTime;

    /**
     * 创建时间
     */
    private LocalDateTime mpsoCreateTime;

    /**
     * 修改时间
     */
    private LocalDateTime mpsoModifyTime;

    /**
     * 是否删除
     */
    private Boolean mpsoIsDelete;

    /**
     * 排序
     */
    private Integer mpsoSort;

    /**
     * 描述
     */
    private String mpsoDescription;

    /**
     * 备注
     */
    private String mpsoRemark;

    /**
     * 品项
     */
    private List<SmMerchantProductSaleItemVO> items;

    /**
     * 交易结果
     */
    private List<SmOrderTransCodeResultVO> records;
}
