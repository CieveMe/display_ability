package cn.com.yyxx.yld.supply.data.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderProductCancelItemVO {

    private static final long serialVersionUID = -3239367121233343206L;

    /**
     * 品项编号
     */
    private String mpsiNo;

    /**
     * 商品ID
     */
    private Long mpsiMpbiId;

    /**
     * 商品编号
     */
    @NotBlank
    private String mpsiMpbiNo;

    /**
     * 订单编号
     */
    @NotBlank
    private String mpsiMpsoNo;

    /**
     * 商品条码
     */
    private String mpsiCode;

    /**
     * 商品多码
     */
    private String mpsiBarCodes;

    /**
     * 商品名称
     */
    private String mpsiName;

    /**
     * 商品单位
     */
    private String mpsiUnit;

    /**
     * 商品规格
     */
    private String mpsiSize;

    /**
     * 原价
     */
    private BigDecimal mpsiRetailPrice;

    /**
     * 折扣
     */
    private BigDecimal mpsiDiscount;

    /**
     * 数量
     */
    @NotNull
    private BigDecimal mpsiNum;

    /**
     * 允许小数
     */
    private Boolean mpsiNumAllowDecimal;

    /**
     * 现价
     */
    private BigDecimal mpsiNowPrice;

    /**
     * 进价
     */
    private BigDecimal mpsiWholesalePrice;

    /**
     * 小计
     */
    private BigDecimal mpsiSubTotal;

    /**
     * 折前小计
     */
    private BigDecimal mpsiDiscountTotal;

    /**
     * 是否退款
     */
    private Boolean mpsiIsRefund;

    /**
     * 创建时间
     */
    private LocalDateTime mpsiCreateTime;

    /**
     * 修改时间
     */
    private LocalDateTime mpsiModifyTime;

    /**
     * 是否删除
     */
    private Boolean mpsiIsDelete;

    /**
     * 排序
     */
    private Integer mpsiSort;

    /**
     * 描述
     */
    private String mpsiDescription;

    /**
     * 备注
     */
    private String mpsiRemark;
}
