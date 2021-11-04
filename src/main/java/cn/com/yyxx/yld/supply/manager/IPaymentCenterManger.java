package cn.com.yyxx.yld.supply.manager;

import cn.com.yyxx.yld.supply.data.vo.ResponsePayVO;
import cn.com.yyxx.yld.supply.data.vo.Result;
import cn.com.yyxx.yld.supply.data.vo.SmMerchantProductSaleOrderVO;

/**
 * 异步支付  支持3.2.以上版本
 */
public interface IPaymentCenterManger {

    /**
     * <p>
     * 支付
     * </p
     *
     * @param order 订单vo
     * @param sbiId   门店ID
     * @param termIp 终端Ip
     * @return cn.com.yyxx.yld.scm.data.pay.ResponsePayVO
     * @author hz
     * @date 2021/1/19 16:35
     * @since 0.17.0
     */
    ResponsePayVO b2cPay(SmMerchantProductSaleOrderVO order, Integer sbiId, String termIp, String paySource);

    /**
     * <p>
     * 超级会员被扫支付
     * </p
     *
     * @param order 订单vo
     * @param sbiId   门店ID
     * @param termIp 终端Ip
     * @return cn.com.yyxx.yld.scm.data.pay.ResponsePayVO
     * @author hz
     * @date 2021/1/19 16:35
     * @since 0.17.0
     */
    Result<Boolean> vipB2cPay(SmMerchantProductSaleOrderVO order, Integer sbiId, String termIp);

    /**
     * <p>
     * 查询支付结果
     * </p
     *
     * @param orderId 订单ID
     * @param sbiId   门店ID
     * @return cn.com.yyxx.yld.scm.data.pay.ResponsePayVO
     * @author hz
     * @date 2020/4/29 16:35
     * @since 0.14.0
     */
    ResponsePayVO query(String orderId, Integer sbiId);

}
