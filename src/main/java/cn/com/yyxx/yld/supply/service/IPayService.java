package cn.com.yyxx.yld.supply.service;

import cn.com.yyxx.yld.supply.action.BaseResultResponse;
import cn.com.yyxx.yld.supply.data.vo.OrderCancelNewVO;
import cn.com.yyxx.yld.supply.data.vo.ResponsePayVO;
import cn.com.yyxx.yld.supply.data.vo.SmMerchantProductSaleOrderVO;
import cn.com.yyxx.yld.supply.data.vo.UserWithPermissionVO;

import java.util.List;

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
     * @author liujun
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
     * @author liujun
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
     * 现金支付
     * </p>
     *
     * @param order     订单信息
     * @param userInfo  用户信息
     * @param serialNum 设备序列号
     * @param userNo    用户编号
     * @return BaseResultResponse<String>
     */
    BaseResultResponse<String> cashPay(SmMerchantProductSaleOrderVO order, UserWithPermissionVO userInfo, String serialNum, String userNo, String termIp);

    /**
     * <p>
     * 退款新版退款。
     * </p
     *
     * @param orderNo   订单编号
     * @param user      门店ID
     * @param serialNum 机器号
     * @return cn.com.yyxx.yld.scm.data.pay.ResponsePayVO
     * @author liujun
     */
    ResponsePayVO cancelNew(String orderNo, OrderCancelNewVO oc, UserWithPermissionVO user, String serialNum, String userNo);


    /**
     * <p>
     * 撤销交易
     * </p
     *
     * @param orderNo   订单编号
     * @param user      门店ID
     * @param serialNum 机器号
     * @return cn.com.yyxx.yld.scm.data.pay.ResponsePayVO
     * @author liujun
     */
    ResponsePayVO cancel(String orderNo, UserWithPermissionVO user, String serialNum, String userNo, String isOnlyRefund, String refundMethod, String refundReason);


    /**
     * <p>
     * 查询支付结果
     * </p
     *
     * @param orderNo 订单编号
     * @param sbiId   门店ID
     * @return cn.com.yyxx.model.core.BaseResultResponse<cn.com.yyxx.yld.scm.data.pay.ResponsePayVO>
     * @author liujun
     */
    ResponsePayVO query(String orderNo, Integer sbiId);

    /***
     * <P>
     *     上传离线订单（现金支付）
     * </P>
     * @author liujun
     * @date 2021/1/19 11:09
     * @param list 订单列表
     * @param userInfo 用户信息
     * @param serialNum 设备序列号
     * @param userNo 用户编号
     * @param termIp 设备ip
     * @return boolean
     */

    boolean upload(List<SmMerchantProductSaleOrderVO> list, UserWithPermissionVO userInfo, String serialNum, String userNo, String termIp);


    SmMerchantProductSaleOrderVO storeSaleInfo(SmMerchantProductSaleOrderVO order);
}
