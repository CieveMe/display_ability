package cn.com.yyxx.yld.supply.manager;

import cn.com.yyxx.yld.supply.data.vo.ResponsePayVO;
import cn.com.yyxx.yld.supply.data.vo.SmMerchantProductSaleOrderVO;

/**
 * 异步支付  支持3.2.以上版本
 */
public interface IStoreAsyncPayManagerV3 {

    /**
     * <p>
     * 支付
     * </p
     *
     * @param order  订单vo
     * @param sbiId  门店ID
     * @param termIp 终端Ip
     * @return cn.com.yyxx.yld.scm.data.pay.ResponsePayVO
     * @author liujun
     * @date 2021/1/19 16:35
     * @since 0.17.0
     */
    ResponsePayVO b2cPay(SmMerchantProductSaleOrderVO order, Integer sbiId, String termIp);

    /**
     * <p>
     * 撤销交易
     * </p
     *
     * @param orderNo 订单编号
     * @param sbiId   门店ID
     * @return cn.com.yyxx.yld.scm.data.pay.ResponsePayVO
     * @author linmeng
     * @date 2020/4/30 9:34
     * @since 0.14.0
     */
    ResponsePayVO cancel(String orderNo, Integer sbiId);


    /**
     * <p>
     * 查询支付结果
     * </p
     *
     * @param orderId 订单ID
     * @param sbiId   门店ID
     * @return cn.com.yyxx.yld.scm.data.pay.ResponsePayVO
     * @author linmeng
     * @date 2020/4/29 16:35
     * @since 0.14.0
     */
    ResponsePayVO query(String orderId, Integer sbiId);

}
