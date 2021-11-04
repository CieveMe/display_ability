package cn.com.yyxx.yld.supply.service;

import cn.com.yyxx.yld.supply.entity.sm.SmMerchantProductSaleItem;
import cn.com.yyxx.yld.supply.entity.sm.SmMerchantProductSaleOrder;

import java.util.List;

/**
 * <p>
 *     用户积分处理
 * </p>
 *
 * @author liujun
 * @version 0.17.0
 * @date 2021/01/13 21:26
 **/
public interface IOwnerService {

    /**
     * <p>
     * 销售变更库存 </p
     *
     * @param saleOrder 销售订单
     * @author liujun
     * @date 2021/01/13 21:26
     */
    void sendMessageToOwner(SmMerchantProductSaleOrder saleOrder,Integer typeId, Integer cancelOrderData , String refundMethod);

}
