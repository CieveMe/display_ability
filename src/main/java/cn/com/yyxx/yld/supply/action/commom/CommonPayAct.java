package cn.com.yyxx.yld.supply.action.commom;

import cn.com.yyxx.yld.supply.action.BaseResultResponse;
import cn.com.yyxx.yld.supply.action.NewAppBaseAction;
import cn.com.yyxx.yld.supply.data.vo.*;
import cn.com.yyxx.yld.supply.exception.NotExceptException;
import cn.com.yyxx.yld.supply.manager.IPaymentCenterManger;
import cn.com.yyxx.yld.supply.service.IPayService;
import cn.com.yyxx.yld.supply.util.ClientIpUtil;
import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author FYX
 * @version 1.0
 * @date 2020/4/29 15:54
 * @since 0.11.0
 */
@RestController
@RequestMapping("/common/order/factory/b2cv3")
@Api(tags = "收银机-被扫支付、现金支付、取消下单（退款交易）、查询订单支付结果、上传离线订单")
public class CommonPayAct extends NewAppBaseAction {

    /**
     * <p>
     * B扫C支付
     * </p>
     *
     * @param order 订单信息
     * @author liujun
     * @date 2021/01/11 18:49
     * @since 0.17.0
     */
    @RequestMapping(value = "/pay", method = RequestMethod.POST)
    @ApiOperation("被扫提交订单&支付")
    public BaseResultResponse<ResponsePayVO> pay(
            @Valid @RequestBody SmMerchantProductSaleOrderVO order,
            HttpServletRequest request) {

        String serialNum = request.getParameter("serial_num");
        String userNo = request.getParameter("user_no");
        UserWithPermissionVO userInfo = super.getUser(request);
        //获取POS ip
        String termIp = new ClientIpUtil().getClientIpAddress(request);
        return deal(() -> service.b2cPay(order, userInfo, serialNum, userNo, termIp));
    }

    /**
     * <p>
     * 超级会员支付
     * </p>
     *
     * @param order 订单信息
     * @author hezhen
     * @date 2021/03/06 13:38
     */
    @RequestMapping(value = "/vip/pay", method = RequestMethod.POST)
    @ApiOperation("被扫提交订单&支付")
    public BaseResultResponse<ResponsePayVO> vipPay(
            @Valid @RequestBody SmMerchantProductSaleOrderVO order,
            HttpServletRequest request) {

        String serialNum = request.getParameter("serial_num");
        String userNo = request.getParameter("user_no");
        UserWithPermissionVO userInfo = super.getUser(request);
        //获取POS ip
        String termIp = new ClientIpUtil().getClientIpAddress(request);
        return deal(() -> service.vipB2cPay(order, userInfo, serialNum, userNo, termIp));
    }

    @RequestMapping(value = "/microservice_test", method = RequestMethod.GET)
    public String vipDiscount() {
        SmMerchantProductSaleOrderVO order = new SmMerchantProductSaleOrderVO();
        order.setMpsoDiscount(new BigDecimal(800));
        order.setPayCode("001150203162800001");
        order.setMpsoTotalPrice(new BigDecimal(8000));
        SmMerchantProductSaleOrderVO smMerchantProductSaleOrderVO = service.storeSaleInfo(order);
        return smMerchantProductSaleOrderVO.toString();
    }


    /**
     * <p>
     * 现金支付
     * </p>
     *
     * @param order 订单信息
     * @author guoyl
     * @date 2020/6/12 8:54
     * @since 1.0.0
     */
    @RequestMapping(value = "/cash/pay", method = RequestMethod.POST)
    @ApiOperation("现金支付")
    public BaseResultResponse<String> cashPay(
            @Valid @RequestBody SmMerchantProductSaleOrderVO order,
            HttpServletRequest request) {
        String serialNum = request.getParameter("serial_num");
        String userNo = request.getParameter("user_no");
        UserWithPermissionVO userInfo = super.getUser(request);
        String termIp = new ClientIpUtil().getClientIpAddress(request);

        ////解析操作
        return dealBase(() -> service.cashPay(order, userInfo, serialNum, userNo, termIp));
    }


    /**
     * <p>
     * 撤销交易V3 </p
     *
     * @param orderNo 订单编号
     * @return cn.com.yyxx.yld.scm.data.pay.ResponsePayVO
     * @author FYX
     * @date 2021/1/19 9:34
     * @since 0.17.0
     */
    @RequestMapping(value = "/cancel/{orderNo}", method = RequestMethod.POST)
    @ApiOperation("退款交易")
    public BaseResultResponse<ResponsePayVO> cancel(@PathVariable String orderNo,
                                                    HttpServletRequest request) {
        UserWithPermissionVO user = super.getUser(request);
        String serialNum = request.getParameter("serial_num");
        if (StrUtil.isEmpty(serialNum)) {
            throw new NotExceptException("无法获取机器序列号");
        }
        String userNo = request.getParameter("user_no");
        if (StrUtil.isEmpty(userNo)) {
            throw new NotExceptException("无法获取用户编号");
        }
        String isOnlyRefund = request.getParameter("is_only_refund");
        String refundMethod = request.getParameter("refundMethod");
        String refundReason = request.getParameter("refundReason");
        ResponsePayVO cancel = service.cancel(orderNo, user, serialNum, userNo, isOnlyRefund, refundMethod, refundReason);
        return deal(() -> cancel);
    }


    /**
     * <p>
     * 退款交易   整单退款/单品退款/
     * <p>
     * 如果是单品退款，则需要前台给退款方式，整单回后，按实际支付金额进行。
     * 退款方式   一共是   单品折扣，整单折后，抹零。 需要对单品进行正向操作
     * 另一个是整单打折后
     * 如果整单打折，则需要前台给予折后金额
     *
     * </p>
     *
     * @param orderNo 订单编号
     * @return cn.com.yyxx.yld.scm.data.pay.ResponsePayVO
     * @author FYX
     * @date 2021/5/11 9:34
     * @since 0.17.0
     */
    @RequestMapping(value = "/cancel_new/{orderNo}", method = RequestMethod.POST)
    @ApiOperation("退款交易/单品退款/整单退款")
    public BaseResultResponse<ResponsePayVO> cancel_new(@PathVariable String orderNo,
                                                        @Valid @RequestBody OrderCancelNewVO oc,
                                                        HttpServletRequest request) {

        UserWithPermissionVO user = super.getUser(request);

        String serialNum = request.getParameter("serial_num");
        if (StrUtil.isEmpty(serialNum)) {
            throw new NotExceptException("无法获取机器序列号");
        }
        String userNo = request.getParameter("user_no");
        if (StrUtil.isEmpty(userNo)) {
            throw new NotExceptException("无法获取用户编号");
        }

        //System.out.println(oc);

        //oc中包含
        ResponsePayVO cancel = service.cancelNew(orderNo, oc, user, serialNum, userNo);
        return deal(() -> cancel);
    }


    /**
     * <p>
     * 查询支付结果 </p>
     *
     * @param orderNo 订单编号
     * @return cn.com.yyxx.model.core.BaseResultResponse<cn.com.yyxx.yld.scm.data.pay.ResponsePayVO>
     * @author liujun
     * @date 2021/1/19 16:19
     * @since 0.17.0
     */
    @RequestMapping(value = "/query/{orderNo}", method = RequestMethod.GET)
    @ApiOperation("查询支付结果")
    public BaseResultResponse<ResponsePayVO> query(@PathVariable String orderNo,
                                                   HttpServletRequest request) {

        UserWithPermissionVO userInfo = super.getUser(request);
        return deal(() -> service.query(orderNo, userInfo.getStoreId()));
    }


    /**
     * <p>
     * 收银端-上传销售记录
     * </p>
     *
     * @param list    请求参数
     * @param request 用户信息
     * @return cn.com.yyxx.model.core.BaseResultResponse<java.lang.Boolean>
     * -----------------------------------
     * @author johnny
     * @date 2020/9/16 18:17
     */
    @ApiOperation("收银端-上传离线销售记录")
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public BaseResultResponse<Boolean> upload(
            @Valid @RequestBody List<SmMerchantProductSaleOrderVO> list, HttpServletRequest request) {
        String serialNum = request.getParameter("serial_num");
        String userNo = request.getParameter("user_no");
        String termIp = new ClientIpUtil().getClientIpAddress(request);
        UserWithPermissionVO userInfo = super.getUser(request);
        return deal(() -> service.upload(list, userInfo, serialNum, userNo, termIp));
    }

    @Autowired
    private IPayService service;
}
