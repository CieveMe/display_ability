package cn.com.yyxx.yld.supply.service;

import cn.com.yyxx.yld.supply.data.vo.ResponsePayVO;
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
public interface IMemberService {

    /**
     * <p>
     * 现金 member发送销售成功消息
     * </p
     *
     * @param saleOrder 销售订单
     * @param productSaleItems 售卖商品子订单
     * @author liujun
     * @date 2021/01/13 21:26
     */
    void sendCashOrderSuccessMessage(SmMerchantProductSaleOrder saleOrder, List<SmMerchantProductSaleItem> productSaleItems);

    /**
     * <p>
     * 扫码 member发送销售成功消息
     * </p
     *
     * @param saleOrder 销售订单
     * @param productSaleItems 售卖商品子订单
     * @author hz
     * @date 2021/10/11 10:26
     */
    void sendOrderSuccessMessage(SmMerchantProductSaleOrder saleOrder, List<SmMerchantProductSaleItem> productSaleItems, ResponsePayVO responsePayVO);
    /**
     * <p>
     * 市民网发送退款成功消息
     * </p
     *
     * @param singleNo 销售订单单号
     * @author liujun
     * @date 2021/01/13 21:26
     */
//    void sendCancelMessage(String singleNo, String refundMethod);
    void sendCancelMessage(String singleNo, String refundMethod, List<SmMerchantProductSaleItem> items);
}
