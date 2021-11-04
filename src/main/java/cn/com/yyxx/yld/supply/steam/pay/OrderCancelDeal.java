package cn.com.yyxx.yld.supply.steam.pay;

import cn.com.yyxx.yld.supply.data.vo.ResponsePayVO;
import cn.com.yyxx.yld.supply.data.vo.RidesOrderCancelVO;
import cn.com.yyxx.yld.supply.messaging.pay.OrderCancelSink;
import cn.com.yyxx.yld.supply.redis.RedisUtil;
import cn.com.yyxx.yld.supply.service.ISalesOrderService;
import cn.com.yyxx.yld.supply.staticMap.PayStatusCode;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Header;

import javax.annotation.Resource;

/**
 * <p>
 * 订单保存
 * </p>
 *
 * @author liujun
 * @date 2021/01/11 18:49
 * @since 0.17.0
 */
@EnableBinding(OrderCancelSink.class)
public class OrderCancelDeal {
    private Logger log = LoggerFactory.getLogger(OrderCancelDeal.class);

    @StreamListener(OrderCancelSink.YLD_ORDER_CANCEL_IN_PUT)
    public void cancelOrder(String responsePayVoStr,
                            @Header("deliveryAttempt") Long deliveryAttempt //重试次数
         ) {
        try {
            log.info("异步退款接收参数:{}", responsePayVoStr);
            ResponsePayVO responsePayVO = JSON.parseObject(responsePayVoStr, ResponsePayVO.class);
            log.debug("异步退款修改订单开始,订单号:{}", responsePayVO.getOrderNo());
            /*1、Rides取值*/
            String ridesOrderObject = redisUtil.get(responsePayVO.getOrderNo()).toString();
            log.info("******************退款订单信息***************"+ridesOrderObject);
            if (ridesOrderObject == null) {
                log.warn("异步修改订单，rides获取订单失败，订单号：{},订单已处理或订单号错误", responsePayVO.getOrderNo());
                return;
            }

            RidesOrderCancelVO ridesOrder = JSON.parseObject(ridesOrderObject, RidesOrderCancelVO.class);
            /*2、修改订单（为避免重复查库，支付成功后修改库存、发送市民网通知积分、发送实时到账提醒）*/
            boolean saveStatus = salesOrderService.updateCannelOrder(ridesOrder, responsePayVO);
            /*3、退款成功清除Rides*/
            if (saveStatus && (PayStatusCode.SUCCESS.getCode().equals(responsePayVO.getPayStatus()))) {
                redisUtil.del(responsePayVO.getOrderNo());
            }
        } catch (Exception e) {
            log.error("支付异步退款出现异常，失败重试次数为：{}", deliveryAttempt, e);
            throw e;
        }

    }

    @Autowired
    private ISalesOrderService salesOrderService;
    @Resource
    private RedisUtil redisUtil;
    @Autowired
    private ObjectMapper objectMapper;
}
