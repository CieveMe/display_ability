package cn.com.yyxx.yld.supply.data.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 *
 * </p>
 *
 * @author linmeng
 * @version 1.0
 * @date 2019/10/19 15:43
 */
@Data
public class AddPointItemDTO implements Serializable {

    private static final long serialVersionUID = -7574691141717899292L;
    /**
     * 商品ID
     */
    private Long mpsiMpbiId;

    /**
     * 商品条码
     */
    private String mpsiCode;

    /**
     * 标准商品ID
     */
    private Long mpsiPislId;

    /**
     * 一级分类
     */
    private Integer mpsiFirstPciId;

    /**
     * 一级分类名称
     */
    private String mpsiFirstPciName;

    /**
     * 二级分类
     */
    private Integer mpsiSecondPciId;

    /**
     * 二级分类名称
     */
    private String mpsiSecondPciName;

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
     * 现价
     */
    private BigDecimal mpsiNowPrice;

    /**
     * 进价
     */
    private BigDecimal mpsiWholesalePrice;

    /**
     * 数量
     */
    private BigDecimal mpsiNum;

    /**
     * 小计
     */
    private BigDecimal mpsiSubTotal;

}
