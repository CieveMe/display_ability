package cn.com.yyxx.yld.supply.data.vo;

import cn.com.yyxx.yld.supply.entity.sm.SmMerchantProductSaleOrder;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * <p>
 *     订单支付结果与操作信息
 * </p>
 *
 * @author liujun
 * @version 1.0.0
 * @program yld-supply-chain-v2
 * @date 2021-01-13 09:29
 * @since 0.17.0
 **/

@Data
@AllArgsConstructor
@ApiModel(value="redis存储退款信息", description="redis存储退款信息")
public class RidesOrderCancelVO {

    public RidesOrderCancelVO() {}

    private SmMerchantProductSaleOrder order;
    private UserWithPermissionVO userInfo;
    private String serialNum;
    private String userNo;
    private String termIp;
    private String scanType;
    /**
     * 退款方式 1：退货退款  2：仅退款 3：仅退货
     */
    private String isOnlyRefund;
    /**
     * 退款方法 1：原路退回  2：现金退回
     */
    private String refundMethod;
    /*
     * 退款原因
     */
    private String refundReason;
}
