package cn.com.yyxx.yld.supply.manager.impl;


import cn.com.yyxx.yld.supply.core.CancelBackProperties;
import cn.com.yyxx.yld.supply.core.PayFactoryProperties;
import cn.com.yyxx.yld.supply.data.vo.*;
import cn.com.yyxx.yld.supply.entity.sm.SmMerchantProductSaleOrder;
import cn.com.yyxx.yld.supply.manager.IPaymentCenterManger;
import cn.com.yyxx.yld.supply.manager.paymentcenter.PaymentCenterFeign;
import cn.com.yyxx.yld.supply.manager.supervip.SuperVIPFeign;
import cn.com.yyxx.yld.supply.redis.RedisUtil;
import cn.com.yyxx.yld.supply.staticMap.StaticDataDef;
import cn.com.yyxx.yld.supply.util.MoneyUtil;
import cn.com.yyxx.yld.supply.util.PayUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *
 * </p>
 *
 * @author linmeng
 * @version 1.0
 * @date 2020/4/28 8:58
 * @since 0.11.0
 */
@Component
public class PaymentCenterMangerImpl implements IPaymentCenterManger {
    private static final String PAY_BY_POS = "POS支付";
    private Logger log = LoggerFactory.getLogger(PaymentCenterMangerImpl.class);
    private static final String PAY_B_C = "PayB_C";
    private static final String SEARCH_B_C = "SearchB_C";
    private static final String CANCEL_B_C = "CancelB_C";
    private static final String RefundB_C = "RefundB_C";

    @Override
    public ResponsePayVO b2cPay(SmMerchantProductSaleOrderVO order, Integer sbiId, String termIp, String paySource) {
        RequestPayVO.RequestPayOrder data = new RequestPayVO.RequestPayOrder();
        //支付来源
        data.setPaySource(paySource);
        //业务类型
        data.setBusinessType(PAY_BY_POS);
        //金额
        data.setAmount(MoneyUtil.yuanToFenLong(order.getMpsoActualPrice()));
        //支付码
        data.setAuthCode(order.getPayCode());
        //订单ID (uuid),支付路由中orderNo为singleNo,orderId为orderNo
        data.setOrderId(order.getMpsoOrderNo());
        //订单编号
        data.setOrderNo(order.getMpsoSingleNo());
        //交易币种(人民币)
        data.setTransactionCurrencyCode(156);
        //订单描述
        data.setDescription("被扫支付");
        //门店ID
        data.setStoreId(sbiId);
        //异步支付回调地址
        data.setNotifyUrl(payFactoryProperties.getReturnNotifyUrl());
        //异步openid回调地址
        data.setNotifyOpenId(payFactoryProperties.getReturnNotifyOpenId());
        //终端(POS)IP
        data.setTermIp(termIp);
        RequestPayVO body = new RequestPayVO();
        //协议
        body.setProtocol(PAY_B_C);
        log.info("*************************************"+JSON.toJSONString(data));
        body.setOrder(data);
        Map<String, Object> encrypt = new PayUtil().encrypt(body, objectMapper, payFactoryProperties);
        if (encrypt == null) {
            log.error("加密失败");
        }
        ResponsePayVO execute = paymentCenterFeign.pay(encrypt);
        return execute;
    }

    @Override
    public Result<Boolean> vipB2cPay(SmMerchantProductSaleOrderVO order, Integer sbiId, String termIp) {
        //1、组装支付参数
        SuperVIPRequestPayVO superVIPRequestPayVO = new SuperVIPRequestPayVO();
        initRequest(order, superVIPRequestPayVO,sbiId);
        //2、发起请求
        Result<Boolean> superVIPResponsePayVO = superVIPFeign.billCheck(superVIPRequestPayVO);
        log.debug("-------------支付请求返回值：******"+superVIPResponsePayVO);
        //3、获取响应结果
        return superVIPResponsePayVO;
    }

    public void initRequest(SmMerchantProductSaleOrderVO order, SuperVIPRequestPayVO superVIPRequestPayVO,Integer sbiId) {
        SuperVIPRequestPayVO.OrderInfo orderInfo = new SuperVIPRequestPayVO.OrderInfo();
        //超级会员卡号
        superVIPRequestPayVO.setCardNum(order.getPayCode().substring(3));
//        订单id
        log.debug("-------------订单 id-------------：******"+order.getMpsoSingleNo());
        orderInfo.setOrderId(order.getMpsoSingleNo());
//        订单No
        log.debug("-------------订单 NO-------------：******"+order.getMpsoOrderNo());
        orderInfo.setOrderNo(order.getMpsoOrderNo());
//        订单编号
        orderInfo.setOrderSingleNo(order.getMpsoSingleNo());
//        门店id
        orderInfo.setStoreId(sbiId.toString());
//        用户名称
        orderInfo.setUserName(order.getMpsoCashier());
//        userNo
        orderInfo.setUserNo(order.getMpsoCashierNo());
//        交易金额(分)
        orderInfo.setAmount(order.getMpsoShouldPrice().multiply(new BigDecimal(100)).longValue());
//        折扣（万分比）实际折扣
        orderInfo.setDiscount(order.getMpsoRealDiscount().multiply(new BigDecimal(10000)).longValue());
        log.debug("=============================应付金额 转换前============================"+order.getMpsoShouldPrice());
        log.debug("=============================实付金额 转换前============================"+order.getMpsoActualPrice());
        log.debug("---------应付金额------：******"+order.getMpsoShouldPrice().multiply(new BigDecimal(100)).longValue());
        log.debug("---------实付金额------：******"+order.getMpsoActualPrice().multiply(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_HALF_UP).longValue());
        //应付金额
        Long shouldPriceLong = order.getMpsoShouldPrice().multiply(new BigDecimal(100)).longValue();
        //实付金额
        Long actualPriceLong = order.getMpsoActualPrice().multiply(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_HALF_UP).longValue();
        //总优惠金额
        Long discountAmount = shouldPriceLong - actualPriceLong;
        //优惠金额（分） 原价减去实际金额(包含门店各种优惠：门店折扣、积分抵扣、各种改价等等)
        orderInfo.setDiscountAmount(discountAmount);
        //积分抵扣金额
        Long amountRatioLong = 0L;
        if (order.getMpsoAmountRatio() != null){
            amountRatioLong = order.getMpsoAmountRatio().multiply(new BigDecimal(100)).longValue();
        }
//        Long amountRatioLong = order.getMpsoAmountRatio().multiply(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_HALF_UP).longValue();
        orderInfo.setIntegralDiscountAmount(amountRatioLong);
//        实际交易金额(分)
        orderInfo.setRealAmount(order.getMpsoActualPrice().multiply(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_HALF_UP).longValue());
        //orderInf中超级会员卡号
        orderInfo.setCardNo(order.getPayCode().substring(3));
        //超级会员优惠金额
//        long vipAmount = Long.parseLong(order.getVipAmount().substring(0,order.getVipAmount().indexOf(".")+1));
        long vipAmount = 0;
        if (order.getVipAmount() != null){
            vipAmount = new BigDecimal(order.getVipAmount()).multiply(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_HALF_UP).longValue();
        }
        //超级会员折扣
        long vipDiscount = 10000;
        if (order.getVipDiscount() != null){
            vipDiscount = (long)(Double.parseDouble(order.getVipDiscount()) * 100);
//            vipDiscount = new BigDecimal(order.getVipDiscount()).multiply(new BigDecimal(100)).longValue();
        }
        //门店折扣优惠金额（不包含门店积分抵扣金额）
        long storeDiscountAmount =discountAmount - amountRatioLong - vipAmount;
        log.debug("===========================积分抵扣优惠金额=============================="+amountRatioLong);
        log.debug("===========================超级会员折扣=============================="+vipDiscount);
        log.debug("===========================超级会员优惠金额 转换之前=============================="+order.getVipAmount());
        log.debug("===========================超级会员优惠金额=============================="+vipAmount);
        log.debug("===========================门店折扣优惠金额（不不不不包含门店积分抵扣金额）=============================="+storeDiscountAmount);
        log.debug("===========================门店折扣优惠金额（包含包含包含门店积分抵扣金额）=============================="+discountAmount);
        orderInfo.setStoreDiscountAmount(storeDiscountAmount);
        //超级会员折扣优惠金额
        orderInfo.setMemberDiscountAmount(vipAmount);
//        超级会员折扣
        orderInfo.setDiscount(vipDiscount);

        List<SuperVIPRequestPayVO.OrderInfo.OrderItem> itemList = new ArrayList<>();
        for (int i = 0; i < order.getItems().size(); i++) {
            SuperVIPRequestPayVO.OrderInfo.OrderItem currOrderItem = new SuperVIPRequestPayVO.OrderInfo.OrderItem();
            SmMerchantProductSaleItemVO orderItem = order.getItems().get(i);
            currOrderItem.setTradeName(orderItem.getMpsiName());
            currOrderItem.setTradeAmount(orderItem.getMpsiNowPrice().multiply(new BigDecimal(100)).stripTrailingZeros().toPlainString());
            currOrderItem.setTradeCount(orderItem.getMpsiNum().toPlainString());
            //通过订单拿到unit的code 在查库拿出人看懂的单位名称
//            System.out.println("===============单位ID================="+JSON.toJSONString(orderItem));
            StaticDataDef unitStatic = StaticDataDef.findBySddId(orderItem.getMpsiUnit());
            currOrderItem.setTradeUnit(unitStatic.getSddName());
            currOrderItem.setTradeSize(orderItem.getMpsiSize());
            itemList.add(currOrderItem);
        }
//        订单商品
        orderInfo.setList(itemList);
        superVIPRequestPayVO.setOrderInfo(orderInfo);
    }

    @Override
    public ResponsePayVO cancel(String orderNo, Integer sbiId, String paySource) {
        RequestCancelPayVO data = new RequestCancelPayVO();
        //Redis取值
        String ridesOrderObject = redisUtil.get(orderNo).toString();
        RidesOrderCancelVO ridesOrder = JSON.parseObject(ridesOrderObject, RidesOrderCancelVO.class);
        SmMerchantProductSaleOrder saleOrder = ridesOrder.getOrder();
        data.setProtocol(RefundB_C);
        RequestCancelPayVO.RequestCancelPayOrder order = new RequestCancelPayVO.RequestCancelPayOrder();
        //支付来源
        order.setPaySource(paySource);
        //业务类型
        order.setBusinessType(PAY_BY_POS);
        order.setOrderId(saleOrder.getMpsoOrderNo());
        order.setStoreId(sbiId);
        // 原订单金额
        Long actualPriceLong = saleOrder.getMpsoActualPrice().multiply(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_HALF_UP).longValue();
        order.setAmount(actualPriceLong);
        order.setPayTime(saleOrder.getMpsoPayTime());
        order.setOrderNo(saleOrder.getMpsoSingleNo());
        order.setDescription("被扫支付");
        //原交易返回的平台订单号
        order.setTradeNo(saleOrder.getMpsoPayOrderId());
        //回调地址
        order.setNotifyUrl(cancelBackProperties.getRefund());
        order.setRefundOrderId(IdUtil.fastSimpleUUID()+"");
        //不传，默认人民币
        order.setTransactionCurrencyCode(156);
        log.info("************退款信息****************"+order);
        data.setOrder(order);
        Map<String, Object> encrypt = new PayUtil().encrypt(data, objectMapper, payFactoryProperties);
        if (encrypt == null) {
            log.error("加密失败");
        }
        return paymentCenterFeign.cancle(encrypt);
    }

    @Override
    public ResponsePayVO query(String orderId, Integer sbiId) {
        RequestPayQueryVO data = new RequestPayQueryVO();
        data.setProtocol(SEARCH_B_C);
        RequestPayQueryVO.RequestPayQueryOrder order = new RequestPayQueryVO.RequestPayQueryOrder();
        order.setOrderId(orderId);
        order.setStoreId(sbiId);
        data.setOrder(order);
        Map<String, Object> encrypt = new PayUtil().encrypt(data, objectMapper, payFactoryProperties);
        if (encrypt == null) {
            log.error("加密失败");
        }
        ResponsePayVO responsePayVO = paymentCenterFeign.query(encrypt);
        if (responsePayVO.getStoreId() == null) {
            responsePayVO.setStoreId(sbiId);
        }
        return responsePayVO;
    }

    @Autowired
    private CancelBackProperties cancelBackProperties;
    @Autowired
    private PayFactoryProperties payFactoryProperties;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PaymentCenterFeign paymentCenterFeign;
    @Autowired
    private SuperVIPFeign superVIPFeign;
    @Resource
    private RedisUtil redisUtil;

}
