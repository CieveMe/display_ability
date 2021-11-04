package cn.com.yyxx.yld.supply.data.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p></p>
 *
 * @author DkHan-ZhangShengYi
 * @version 1.0.0
 * @program yld-supply-v2
 * @description 查询退款订单
 * @date 2021-07-07 09:48
 * @since 0.1.0
 **/
@Data
@Accessors(chain = true)
public class OwnerOrderVo {

    /**
     * 门店ID
     */
    private Integer mpsoSbiId;

    /**
     * 店铺类型
     */
    private String mpsoSbiType;

    /**
     * 门店编号
     */
    private String mpsoSbiCode;

    /**
     * 门店名称
     */
    private String mpsoStoreName;

    /**
     * 订单编号
     */
    private String mpsoOrderNo;

    /**
     * 订单单号
     */
    private String mpsoSingleNo;

    /**
     * 交易笔数
     */
    private Integer mpsoPaymentNum;

    /**
     * 交易总额
     */
    private BigDecimal price;

    /**
     * 现金交易笔数
     */
    private BigDecimal cashCustomer;

    /**
     * 线上交易笔数
     */
    private BigDecimal onlineCustomer;


    /**
     * 线上超级会员交易笔数
     */
    private BigDecimal supervipCustomer;

    /**
     * 现金交易金额
     */
    private BigDecimal cashTurnover;

    /**
     * 线上交易金额
     */
    private BigDecimal onlineTurnover;


    /**
     * 线上超级会员交易金额
     */
    private BigDecimal supervipTurnover;




}
