package cn.com.yyxx.yld.supply.service;

import cn.com.yyxx.yld.supply.data.vo.ResponsePayVO;
import cn.com.yyxx.yld.supply.data.vo.SmMerchantProductSaleOrderVO;
import cn.com.yyxx.yld.supply.data.vo.UserWithPermissionVO;

/**
 * <p>
 *
 * </p>
 *
 * @author FYX
 * @version 0.16.0
 * @date 2020/12/04 10:09
 **/
public interface IPayService {

    /***
     * <P>
     *     B扫C支付（被扫）
     * </P>
     * @author hz
     * @date 2021/1/12 9:19
     * @param order 订单信息
     * @param userInfo 用户信息
     * @param serialNum 设备序列号
     * @param userNo 用户编号
     * @param termIp 设备ip
     * @return cn.com.yyxx.yld.supply.data.vo.ResponsePayVO
     * @since 0.17.0
     */
    ResponsePayVO b2cPay(SmMerchantProductSaleOrderVO order, UserWithPermissionVO userInfo, String serialNum, String userNo, String termIp);


    /***
     * <P>
     *     B扫C支付（被扫）
     * </P>
     * @author hz
     * @date 2021/1/12 9:19
     * @param order 订单信息
     * @param userInfo 用户信息
     * @param serialNum 设备序列号
     * @param userNo 用户编号
     * @param termIp 设备ip
     * @return cn.com.yyxx.yld.supply.data.vo.ResponsePayVO
     * @since 0.17.0
     */
    ResponsePayVO vipB2cPay(SmMerchantProductSaleOrderVO order, UserWithPermissionVO userInfo, String serialNum, String userNo, String termIp);

    /**
     * <p>
     * 查询支付结果
     * </p
     *
     * @param orderNo 订单编号
     * @param sbiId   门店ID
     * @return cn.com.yyxx.model.core.BaseResultResponse<cn.com.yyxx.yld.scm.data.pay.ResponsePayVO>
     * @author hz
     */
    ResponsePayVO query(String orderNo, Integer sbiId);




    SmMerchantProductSaleOrderVO storeSaleInfo(SmMerchantProductSaleOrderVO order);
}
