package cn.com.yyxx.yld.supply.manager.impl;


import cn.com.yyxx.yld.supply.core.PayFactoryProperties;
import cn.com.yyxx.yld.supply.data.vo.*;
import cn.com.yyxx.yld.supply.exception.NotExceptException;
import cn.com.yyxx.yld.supply.manager.IStoreAsyncPayManagerV3;
import cn.com.yyxx.yld.supply.util.MoneyUtil;
import cn.com.yyxx.yld.supply.util.PayUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;


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
public class StoreAsyncPayManagerV3Impl implements IStoreAsyncPayManagerV3 {

    private Logger log = LoggerFactory.getLogger(StoreAsyncPayManagerV3Impl.class);
    private static final String PAY_B_C = "PayB_C";
    private static final String SEARCH_B_C = "SearchB_C";
    private static final String CANCEL_B_C = "CancelB_C";

    @Override
    public ResponsePayVO b2cPay(SmMerchantProductSaleOrderVO order, Integer sbiId, String termIp){
        RequestPayVO.RequestPayOrder data = new RequestPayVO.RequestPayOrder();
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
        body.setOrder(data);
        ResponsePayVO execute;
        try {
            execute = new PayUtil()
                .execute(body, payFactoryProperties.getPayUrl(), ResponsePayVO.class,
                    payFactoryProperties, objectMapper);
        } catch (IOException e) {
            log.warn("订单:{},请求支付路由超时:{}", order.getMpsoOrderNo(),e);
            return new ResponsePayVO("4441", "请求支付路由超时");
        }
        return execute;
    }
    @Override
    public ResponsePayVO cancel(String orderNo, Integer sbiId) {
        RequestCancelPayVO data = new RequestCancelPayVO();
        data.setProtocol(CANCEL_B_C);
        RequestCancelPayVO.RequestCancelPayOrder order = new RequestCancelPayVO.RequestCancelPayOrder();
        order.setOrderId(orderNo);
        order.setStoreId(sbiId);
        data.setOrder(order);
        try {
            return new PayUtil()
                .execute(data, payFactoryProperties.getPayUrl(), ResponsePayVO.class,
                    payFactoryProperties, objectMapper);
        } catch (IOException e) {
            throw new NotExceptException("Json转化异常!");
        }
    }

    @Override
    public ResponsePayVO query(String orderId, Integer sbiId) {
        RequestPayQueryVO data = new RequestPayQueryVO();
        data.setProtocol(SEARCH_B_C);
        RequestPayQueryVO.RequestPayQueryOrder order = new RequestPayQueryVO.RequestPayQueryOrder();
        order.setOrderId(orderId);
        order.setStoreId(sbiId);
        data.setOrder(order);
        try {
            ResponsePayVO responsePayVO = new PayUtil()
                .execute(data, payFactoryProperties.getQueryUrl(), ResponsePayVO.class,
                    payFactoryProperties, objectMapper);
            if (responsePayVO.getStoreId() == null) {
                responsePayVO.setStoreId(sbiId);
            }
            return responsePayVO;
        } catch (IOException e) {
            throw new NotExceptException("Json转化异常!");
        }
    }

    @Autowired
    private PayFactoryProperties payFactoryProperties;
    @Autowired
    private ObjectMapper objectMapper;

}
