package cn.com.yyxx.yld.supply.service;

import cn.com.yyxx.yld.supply.entity.sm.SmMerchantProductSaleOrder;

/**
 * <p>
 *     用户积分处理
 * </p>
 *
 * @author hz
 * @version 0.17.0
 * @date 2021/01/13 21:26
 **/
public interface IOwnerService {

    /**
     * <p>
     * 销售变更库存 </p
     *
     * @param saleOrder 销售订单
     * @author hz
     * @date 2021/01/13 21:26
     */
    void sendMessageToOwner(SmMerchantProductSaleOrder saleOrder, Integer typeId, Integer cancelOrderData, String refundMethod);

}
