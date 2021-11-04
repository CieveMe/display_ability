package cn.com.yyxx.yld.supply.data.vo;

import cn.com.yyxx.yld.supply.entity.sm.SmMerchantProductSaleOrder;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * <p>
 * 订单支付结果与操作信息
 * </p>
 *
 * @author hz
 * @version 1.0.0
 * @program yld-supply-chain-v2
 * @date 2021-01-13 09:29
 * @since 0.17.0
 **/

@Data
@AllArgsConstructor
@ApiModel(value = "redis存储退款信息", description = "redis存储退款信息")
public class RidesOrderNewCancelVO {

    public RidesOrderNewCancelVO() {
    }


    /**
     * 原订单内全部内容
     */
    private SmMerchantProductSaleOrder order;//原订单内全部内容
    /**
     * 退款订单内的内容
     */
    private OrderCancelNewVO cancelorder;//退款订单内的内容

    private UserWithPermissionVO userInfo;
    private String serialNum;
    private String userNo;
    private String termIp;
    private String scanType;

    /**
     * 退款订单记录ID
     */
    private Integer cancelOrderData;
}
