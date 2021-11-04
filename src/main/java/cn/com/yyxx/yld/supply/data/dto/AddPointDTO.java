package cn.com.yyxx.yld.supply.data.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author linmeng
 * @version 1.0
 * @date 2019/10/18 21:31
 */
@Data
public class AddPointDTO implements Serializable {

    private static final long serialVersionUID = 7484479062350576696L;

    //门店类型: 1、便利店 3、菜场
    private Integer place;

    //B,C类型
    private String scanType;

    /**
     * 总优惠金额
     */
    private Long discountAmount;

    /**
     * 门店优惠金额
     */
    private Long storeDiscountAmount;

    /**
     * 超级会员优惠金额
     */
    private Long memberDiscountAmount;

    /**
     * 超级会员折扣
     */
    private Long discount;

    /**
     * 超级会员支付码
     */
    private String payCode;

    /**
     * 订单ID
     */
    private Long mpsoId;

    /**
     * 门店ID
     */
    private Integer mpsoSbiId;

    /**
     * 门店名称
     */
    private String mpsoStoreName;

    /**
     * 门店编号
     */
    private String mpsoSbiCode;

    /**
     * 收银员ID
     */
    private Long mpsoUbiId;

    /**
     * 收银员姓名
     */
    private String mpsoCashier;

    /**
     * 收银员工号
     */
    private String mpsoCashierNo;

    /**
     * 订单编号
     */
    private String mpsoOrderNo;

    /**
     * 订单单号
     */
    private String mpsoSingleNo;

    /**
     * 订单时间
     */
    private LocalDateTime mpsoOrderTime;

    /**
     * 支付方式
     */
    private String mpsoPaymentMethod;

    /**
     * 扫码方式
     */
    private Integer mpsoPaymentMode;

    /**
     * 支付订单号
     */
    private String mpsoPayOrderId;

    /**
     * 三方买家ID
     */
    private String mpsoThirdBuyerId;

    /**
     * 门店会员ID
     */
    private Long mpsoSmiId;

    /**
     * 会员电话
     */
    private String mpsoSmiPhone;

    /**
     * 应收金额
     */
    private BigDecimal mpsoTotalPrice;

    /**
     * 应付金额
     */
    private BigDecimal mpsoShouldPrice;

    /**
     * 实付金额
     */
    private BigDecimal mpsoActualPrice;

    /**
     * 积分抵扣（元）
     */
    private BigDecimal mpsoAmountRatio;

    /**
     * 本次消耗市民分
     */
    private Integer mpsoCivicIntegration;

    /**
     * 明细
     */
    private List<AddPointItemDTO> items;
}
